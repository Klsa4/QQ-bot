package com.klsa.bot.pojo;

import lombok.Data;

@Data
public class Payload {
    private Integer op;
    private Object d;
    private Long s;
    private String t;
}
