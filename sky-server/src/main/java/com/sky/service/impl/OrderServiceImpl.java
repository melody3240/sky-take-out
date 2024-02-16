package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.ThreadLocalUtil;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        List<Orders> orders = orderMapper.conditionSearch(ordersPageQueryDTO);
        Page page = (Page) orders;
        return new PageResult(page.getTotal(),orders);
    }

    @Override
    public OrderStatisticsVO statistics() {
        return orderMapper.statistics();
    }

    @Override
    public OrderVO findDetails(Long id) {
        OrderVO orderVO = orderMapper.findDetails(id);
        List<OrderDetail> details = orderMapper.findOrderDetailList(id);
        orderVO.setOrderDetailList(details);
        return orderVO;
    }

    @Override
    public void cancel(OrdersCancelDTO ordersCancelDTO) {
        if(ordersCancelDTO.getId() == null){
            throw new BusinessException("请选择要取消的订单");
        }
        // 查询当前订单状态是否可取消
        Integer status =  orderMapper.findStatus(ordersCancelDTO.getId());
        if(status == 1 || status == 2 || status == 4 || status == 5){
            Orders orders = BeanUtil.copyProperties(ordersCancelDTO, Orders.class);
            orders.setCancelTime(LocalDateTime.now());
            orderMapper.cancel(orders);
        }else{
            throw new BusinessException("该状态无法取消订单");
        }
    }

    @Override
    public void complete(Long id) {
        if(id == null){
            throw new BusinessException("请选择要取消的订单");
        }
        // 查询当前订单状态是否可完成
        Integer status =  orderMapper.findStatus(id);
        if(status != 4){
            throw new BusinessException("该状态订单不可完成");
        }
        orderMapper.complete(id);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        if(ordersRejectionDTO.getId() == null){
            throw new BusinessException("请选择要拒绝的订单");
        }
        // 查询当前订单状态是否可取拒绝
        Integer status =  orderMapper.findStatus(ordersRejectionDTO.getId());
        if(status == 2){
            orderMapper.rejection(ordersRejectionDTO);
        }else{
            throw new BusinessException("该状态无法拒绝订单");
        }
    }

    @Override
    public void confirm(Long id) {
        if(id == null){
            throw new BusinessException("请选择要接取的订单");
        }
        // 查询当前订单状态是否可取拒绝
        Integer status =  orderMapper.findStatus(id);
        if(status == 2){
            orderMapper.confirm(id);
        }else{
            throw new BusinessException("该状态无法接取订单");
        }
    }

    @Override
    public void delivery(Long id) {
        if(id == null){
            throw new BusinessException("请选择要派送的订单");
        }
        // 查询当前订单状态是否可取拒绝
        Integer status =  orderMapper.findStatus(id);
        if(status == 3){
            orderMapper.delivery(id);
        }else{
            throw new BusinessException("该状态无法派送订单");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderSubmitVO save(OrdersSubmitDTO ordersSubmitDTO) {
        //1.参数校验,主要校验收货地址有没有
        AddressBook addressBook = addressBookMapper.findById(ordersSubmitDTO.getAddressBookId());
        if (addressBook==null) {
            throw new BusinessException("请先填写收货信息");
        }

        //2.完善订单信息
        //2.1 将dto转成order
        Orders order = BeanUtil.copyProperties(ordersSubmitDTO, Orders.class);

        //2.2 完善各种信息
        String orderNumber = IdUtil.fastSimpleUUID();// //订单号
        order.setNumber(orderNumber);
        order.setStatus(Orders.PENDING_PAYMENT);//待付款

        Long userId = ThreadLocalUtil.getCurrentId();
        order.setUserId(userId);
        order.setOrderTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);//未支付

        //设置收货人信息
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());

        //3.调用orderMapper.save(order)保存订单,返回新增记录的主键
        orderMapper.save(order);

        //4.完善订单项信息(从购物车中获取)
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.shoppingList(userId);
        if (CollUtil.isNotEmpty(shoppingCarts)) {
            //4.0 声明订单项集合
            List<OrderDetail> odList = new ArrayList<>();

            //4.1 遍历购物项集合,获取每个购物项
            for (ShoppingCart sc : shoppingCarts) {
                //4.2 将购物项转换成订单项,完善订单项数据(订单id)
                OrderDetail od = BeanUtil.copyProperties(sc, OrderDetail.class);
                od.setOrderId(order.getId());

                //4.3 将订单项放入订单项集合中
                odList.add(od);
            }

            //5.调用orderDetailMapper.batchSave(list)存放订单项
            orderDetailMapper.batchSave(odList);
        }

        //6.清空购物车
        shoppingCartMapper.deleteShoppingCart(userId);

        //7.构建vo
        return OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(orderNumber)
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();
    }

    // 订单支付
    /*public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = ThreadLocalUtil.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new BusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }*/

    // 订单支付
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {

        // 1.直接修改订单状态
        Orders ordersDB = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        //--------- webSocket 发送消息给客户端 ------------
        Map map = new HashMap();
        map.put("type", 1);//消息类型，1表示来单提醒
        map.put("orderId", orders.getId());
        map.put("content", "订单号：" + ordersPaymentDTO.getOrderNumber());
        //通过WebSocket实现来单提醒，向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
        //--------- webSocket 发送消息给客户端 ------------

        // 2. 返回一个空结果
        return new OrderPaymentVO();
    }

    // 支付成功，修改订单状态
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(ThreadLocalUtil.getCurrentId());
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        List<Orders> orders = orderMapper.historyOrders(ordersPageQueryDTO);
        Page list = (Page) orders;

        List<OrderVO> orderVOList = null;
        if(CollUtil.isNotEmpty(orders)){
             orderVOList = orders.stream().map(order -> {
                OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
                List<OrderDetail> orderDetailList = orderMapper.findOrderDetailList(order.getId());
                orderVO.setOrderDetailList(orderDetailList);
                return orderVO;
            }).collect(Collectors.toList());
        }

        return new PageResult(list.getTotal(),orderVOList);
    }

    @Override
    public void userOrderCancel(Long id) {
        // 1.先查询订单信息
        Orders ordersDB = orderMapper.getById(id);
        // 2.校验订单状态
        if (ordersDB.getStatus() > 2) {
            throw new BusinessException("请联系商家取消订单");
        }
        // 3.构建取消实体
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.CANCELLED)
                .cancelReason("用户取消订单")
                .cancelTime(LocalDateTime.now())
                .build();
        // 4.判断是否付款（学生不写）
        if (ordersDB.getPayStatus().equals(Orders.PENDING_PAYMENT)) {
            try {
                // 微信退款
                weChatPayUtil.refund(ordersDB.getNumber(), ordersDB.getNumber(), new BigDecimal("0.01"), new BigDecimal("0.01"));
                // 支付改为已退款
                orders.setPayStatus(Orders.REFUND);
            } catch (Exception e) {
                throw new BusinessException("微信退款失败：" + ordersDB.getNumber());
            }
        }
        // 5.调用mapper修改订单信息
        orderMapper.update(orders);
    }

    // 再来一单
    @Override
    public void repetition(Long id) {
        // 1.查询明细列表
        List<OrderDetail> detailList = orderDetailMapper.getByOrderId(id);
        // 清空购物车
        shoppingCartMapper.deleteShoppingCart(ThreadLocalUtil.getCurrentId());
        // 2.遍历明细列表
        detailList.forEach(orderDetail -> {
            // 2-1 明细封装购物项
            ShoppingCart shoppingCart = BeanUtil.copyProperties(orderDetail, ShoppingCart.class, "id");
            // 2-2 补充信息
            shoppingCart.setUserId(ThreadLocalUtil.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 2-3 添加到数据库
            shoppingCartMapper.save(shoppingCart);
        });
    }

    @Override
    public void reminder(Long id) {
        // 查询订单是否存在
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }

        //基于WebSocket实现催单
        Map map = new HashMap();
        map.put("type", 2);//2代表用户催单
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
