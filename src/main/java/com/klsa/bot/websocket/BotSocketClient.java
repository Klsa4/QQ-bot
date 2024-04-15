package com.klsa.bot.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import com.klsa.bot.pojo.Heartbeat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.klsa.bot.Constants.OpConstants;
import com.klsa.bot.Service.GetAccessBotService;
import com.klsa.bot.pojo.Authorization;
import com.klsa.bot.pojo.BotToken;
import com.klsa.bot.pojo.Payload;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;

@ClientEndpoint
@Slf4j
@Component
public class BotSocketClient {

    private Session session;

    private Long lastestSequence;

    private Long interval;

    @Autowired
    GetAccessBotService getAccessBotService;


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket 连接已经建立。");
        this.session = session;

    }

    @OnMessage
    public void onMessage(String json, Session session) {
        System.out.println("收到服务器消息：" + json);
        Payload payload = JSON.parseObject(json, Payload.class);


        if (payload.getOp().equals(OpConstants.HELLO)) {
            Heartbeat hb = JSON.parseObject(payload.getD().toString(), Heartbeat.class);
            System.out.println(hb);
            this.interval = hb.getHeartbeat_interval();
            Payload feedback = new Payload();

            heartbeat();

            feedback.setOp(2);

            // 接下来创建所需数据
            Authorization authorization = new Authorization();


            BotToken botToken = getAccessBotService.getAccessToken(5);

            if (botToken == null) {
                log.info("token获取失败");
                return;
            }

            // 格式为"QQBot {AccessToken}"
            authorization.setToken("QQBot " + botToken.getAccess_token());
            authorization.setIntents(513L);
            // 若无需分片，使用[0, 1]即可
//            authorization.setShard(new Integer[] {0, 1});

            feedback.setD(authorization);

            String response = JSON.toJSONString(feedback);
            System.out.println(response);
            sendMessage(response);
        } else if (payload.getOp().equals(OpConstants.DISPATCH)) {

        }
    }

    private void sendMessage(String json) {
        try {
            this.session.getAsyncRemote().sendText(json);
        } catch (Exception e) {
            throw new RuntimeException("发送消息失败");
        }
    }

    @SuppressWarnings("ALL")
    private void heartbeat() {
        Executors.newSingleThreadExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("heartbeat");
                    thread.setDaemon(Boolean.TRUE);
                    return thread;
                }
        ).execute(() -> {
            while (true) {
                try {
                    Payload payload = new Payload();
                    payload.setOp(OpConstants.HEARTBEAT);
                    if (lastestSequence != null) payload.setS(lastestSequence);
                    String json = JSON.toJSONString(payload);
                    System.out.println(json);
                    this.sendMessage(json);
                    LockSupport.parkUntil(interval);
                } catch (Throwable e) {
                    log.error("心跳出错: {}", e.getMessage());
                }
            }
        });
    }

    @OnClose
    public void onClose() {
        System.out.println("WebSocket 连接已经关闭。");
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("WebSocket 连接出现错误：" + t.getMessage());
    }

    public void connect(String url) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(url));
    }

    public void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void close() throws IOException {
        session.close();
    }

}
