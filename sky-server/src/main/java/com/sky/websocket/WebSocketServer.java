package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端。
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{cid}")
public class WebSocketServer {

    //存放会话对象
    private static Map<String, Session> sessionMap = new ConcurrentHashMap();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("cid") String cid) {
        System.out.println("客户端：" + cid + "建立连接");
        sessionMap.put(cid, session);
    }

    /**
     * 连接关闭调用的方法
     * @param cid
     */
    @OnClose
    public void onClose(@PathParam("cid") String cid) {
        System.out.println("连接断开:" + cid);
        sessionMap.remove(cid);
    }


    /**
     * 监听客户端发送过来的消息
     * @param message 客户端发送过来的消息
     * @param cid 客户端ID
     */
    @OnMessage
    public void onMessage(String message, @PathParam("cid") String cid) {
        System.out.println("收到来自客户端：" + cid + "的信息:" + message);
    }

    /**
     * 群发消息
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
