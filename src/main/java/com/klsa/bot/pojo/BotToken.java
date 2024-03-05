package com.klsa.bot.pojo;

import lombok.Data;

@Data
public class BotToken {
    private String access_token;
    private Integer expires_in;
}
