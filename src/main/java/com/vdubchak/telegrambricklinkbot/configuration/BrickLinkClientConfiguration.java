package com.vdubchak.telegrambricklinkbot.configuration;

import com.vdubchak.telegrambricklinkbot.bricklink.auth.BricklinkHeadersProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class BrickLinkClientConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(messageConverters);
        restTemplate.setInterceptors(Collections.singletonList(bricklinkHeadersProvider()));
        return restTemplate;
    }

    @Bean
    public BricklinkHeadersProvider bricklinkHeadersProvider() {
        return new BricklinkHeadersProvider();
    }
}
