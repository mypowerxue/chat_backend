package com.example.demo.controller.socket;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.example.demo.controller.vo.TestMessageVo;
import com.example.demo.mapper.bean.TestMessage;
import com.example.demo.service.TestMessageService;
import com.example.demo.utils.SpringUtils;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;

@ServerEndpoint("/webSocket/{userId}")
@Component
public class WebSocketServer {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    public static Map<Integer, WebSocketServer> clients = new ConcurrentHashMap<>(); //连接池
    private static Gson gson = new Gson();  //解析类
    private static WebSocketServer quartzManager;

    private Session session;
    private int userId;

    private TestMessageService testMessageService;

    @OnOpen
    public void onOpen(@PathParam("userId") Integer userId, Session session) {
        this.userId = userId;
        this.session = session;
        System.out.println(userId + "链接成功！");
        clients.put(userId, this);
        testMessageService = SpringUtils.getApplicationContext().getBean(TestMessageService.class);

        //尝试读取该用户的未读消息 并推送给该用户
        List<TestMessage> testMessages = testMessageService.selectMessage(userId, 2);
        if (testMessages != null && testMessages.size() != 0) {
            for (TestMessage testMessage : testMessages) {
                testMessage.setId(null);
                sendMessageTo(this, testMessage, true);
            }
        }
    }

    @OnClose
    public void onClose() {
        this.userId = -1;
        this.session = null;
        System.out.println(userId + "链接断开！");
        clients.remove(userId);
    }

    @OnMessage
    public void onMessage(String message) {
        TestMessageVo testMessage = gson.fromJson(message, TestMessageVo.class);
        testMessage.setTime(System.currentTimeMillis());
        sendMessage(testMessage);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(TestMessageVo testMessage) {
        Integer receiveId = testMessage.getReceiveId();
        WebSocketServer webSocketServer = null;
        for (WebSocketServer item : clients.values()) {
            if (item.userId == receiveId) {
                webSocketServer = item;
                break;
            }
        }
        TestMessage testMessage1 = new TestMessage();
        testMessage1.setStatus(2);
        testMessage1.setCreateTime(new Date(testMessage.getTime()));
        testMessage1.setReceiveId(testMessage.getReceiveId());
        testMessage1.setSendId(testMessage.getSendId());
        testMessage1.setMessage(testMessage.getMessage());
        testMessage1.setMessageType(testMessage.getMessageType());
        testMessage1.setSendType(testMessage.getSendType());

        sendMessageTo(webSocketServer, testMessage1, false);
    }

    private void sendMessageTo(WebSocketServer webSocketServer, TestMessage testMessage, boolean isUpdate) {
        Integer sendId = testMessage.getSendId();
        Integer receiveId = testMessage.getReceiveId();

        //开始标记聊天记录
        if (webSocketServer != null) {
            testMessage.setStatus(1);
            if (isUpdate) {
                testMessageService.updateMessage(testMessage);
            } else {
                testMessageService.insertMessage(testMessage);
            }
            testMessage.setSendId(sendId);
            testMessage.setReceiveId(receiveId);
            webSocketServer.session.getAsyncRemote().sendText(gson.toJson(testMessage));
        } else {
            testMessage.setStatus(2);
            if (isUpdate) {
                testMessageService.updateMessage(testMessage);
            } else {
                testMessageService.insertMessage(testMessage);
            }
        }
    }

}