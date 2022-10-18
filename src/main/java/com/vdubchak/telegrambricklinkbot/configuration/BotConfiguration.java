package com.vdubchak.telegrambricklinkbot.configuration;

import com.vdubchak.telegrambricklinkbot.bricklink.auth.BricklinkHeadersProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class BotConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(bricklinkHeadersProvider()));
        return restTemplate;
    }

    @Bean
    public BricklinkHeadersProvider bricklinkHeadersProvider() {
        return new BricklinkHeadersProvider();
    }
}
