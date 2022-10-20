package com.vdubchak.telegrambricklinkbot.configuration;

import com.github.kshashov.telegram.config.TelegramBotGlobalProperties;
import com.github.kshashov.telegram.config.TelegramBotGlobalPropertiesConfiguration;
import com.pengrad.telegrambot.request.SetWebhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotConfiguration implements TelegramBotGlobalPropertiesConfiguration {

    @Value("${bricklink_bot.token}")
    private String token;
    @Value("${bricklink_bot.url}")
    private String url;

    @Value("${bricklink_bot.port}")
    private int port;

    @Override
    public void configure(TelegramBotGlobalProperties.Builder builder) {
        builder.setWebserverPort(port)
                .configureBot(token, botBuilder -> botBuilder.useWebhook(new SetWebhook().url(url)));
    }
}
