package com.klsa.bot.pojo;

import lombok.Data;

@Data
public class Authorization {
    private String token;
    private Long intents;
    private Integer[] shard;
}
