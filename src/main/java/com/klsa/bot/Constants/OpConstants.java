package com.klsa.bot.Constants;

public class OpConstants {
    // 服务端进行消息推送
    public static Integer DISPATCH = 0; 

    // 客户端或服务端发送心跳
    public static Integer HEARTBEAT = 1; 

    // 客户端发送鉴权
    public static Integer IDENTIFY = 2; 

    // 客户端恢复连接
    public static Integer RESUME = 6; 

    // 服务端通知客户端重新连接
    public static Integer RECONNECT = 7; 

    // 当 identify 或 resume 的时候，如果参数有错，服务端会返回该消息
    public static Integer INVALID_SESSION = 9; 

    // 当客户端与网关建立 ws 连接之后，网关下发的第一条消息
    public static Integer HELLO = 10; 

    // 当发送心跳成功之后，就会收到该消息
    public static Integer HEARTBEAT_ACK = 11; 

    //仅用于 http 回调模式的回包，代表机器人收到了平台推送的数据
    public static Integer HTTP_CALLBACK_ACK = 12; 
}
