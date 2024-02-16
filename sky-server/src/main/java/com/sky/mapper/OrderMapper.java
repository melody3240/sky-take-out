package com.sky.mapper;

import com.sky.anno.AutoFill;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    List<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(if(status = 3,true,null)) as confirmed," +
            "count(if(status = 4,true,null)) as deliveryInProgress," +
            "count(if(status = 2,true,null)) as toBeConfirmed from orders")
    OrderStatisticsVO statistics();

    @Select("select address,address_book_id,amount,cancel_reason,cancel_time,\n" +
            "       checkout_time,ab.consignee consignee,delivery_status,delivery_time,\n" +
            "       estimated_delivery_time,o.id id,number, status, ab.user_id user_id,\n" +
            "       order_time, pay_method, pay_status, remark,ab.phone phone, rejection_reason,\n" +
            "       cancel_time, pack_amount, tableware_number, tableware_status,ab.sex sex,\n" +
            "       province_code, province_name, city_code, city_name, district_code,\n" +
            "       district_name, detail, label, is_default, openid, name, id_number,\n" +
            "       avatar, create_time from orders o join address_book ab on ab.id = o.address_book_id\n" +
            "join user u on o.user_id = u.id where o.id = #{id}")
    OrderVO findDetails(Long id);

    @Select("select * from order_detail where order_id = #{id}")
    List<OrderDetail> findOrderDetailList(Long id);

    @AutoFill("update")
    void cancel(Orders orders);

    @Select("select status from orders where id = #{id}")
    Integer findStatus(Long id);

    @Update("update orders set status = 5 where id = #{id}")
    void complete(Long id);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    @Update("update order set state = 3 where id = #{id}")
    void confirm(Long id);

    @Update("update order set state = 4 where id = #{id}")
    void delivery(Long id);

    void save(Orders order);

    // 根据订单号查询
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    // 修改订单信息
    void update(Orders orders);

    List<Orders> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);
    // 修改订单状态
    @Select("select * from orders where status = #{status} and order_time < #{old15Time}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime old15Time);

    Double sumTurnover(Integer status, LocalDate date);

    Integer findOrderCount(LocalDate date,Integer status);

    @Select("select name,SUM(od.number) as number from orders o join order_detail od on o.id = od.order_id\n" +
            "where DATE(order_time) between #{begin} and #{end} and status = 5 \n" +
            "group by name\n" +
            "order by number desc\n")
    List<Map<String, Object>> findTop10(LocalDate begin,LocalDate end);

    @Select("select count(1) allOrders," +
            "count(if(status = 6,true,null)) cancelledOrders," +
            "sum(status = 5) completedOrders," +
            "sum(status = 3) deliveredOrders," +
            "count(if(status = 2,true,null)) waitingOrders from orders where DATE(order_time) = #{date}")
    OrderOverViewVO findCount4Status(LocalDate date);
}
