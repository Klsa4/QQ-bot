package com.klsa.bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.klsa.bot.Service.GetAccessBotService;
import com.klsa.bot.websocket.BotSocketClient;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class Start implements CommandLineRunner{
    @Autowired
    GetAccessBotService service;

    @Autowired
    BotSocketClient botSocketClient;

    @Override
    public void run(String... args) throws Exception {
        String wss = service.getWSS();
        log.info("wss地址:{}", wss);
        botSocketClient.connect(wss);
    }
}
