package com.klsa.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class QQConfiguration {

    @Value("${mybot.appId}")
    private String appId;

    @Value("${mybot.appSecret}")
    private String appSecret;

    @Value("${mybot.token}")
    private String token;

    @Value("${mybot.QQNumber}")
    private String QQNumber;
}
