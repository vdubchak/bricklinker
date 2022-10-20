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

    @Override
    public void configure(TelegramBotGlobalProperties.Builder builder) {
        builder.setWebserverPort(Integer.parseInt(System.getenv("PORT")))
                .configureBot(token, botBuilder -> botBuilder.useWebhook(new SetWebhook().url(url)));
    }
}
