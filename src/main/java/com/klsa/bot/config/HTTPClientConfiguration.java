package com.klsa.bot.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HTTPClientConfiguration {
    
    @Bean
    public HttpClient httpClient() {
        return HttpClients.createDefault();
    }
}
