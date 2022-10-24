package com.vdubchak.telegrambricklinkbot.configuration;

import com.github.kshashov.telegram.config.TelegramBotGlobalProperties;
import com.github.kshashov.telegram.config.TelegramBotGlobalPropertiesConfiguration;
import com.pengrad.telegrambot.request.SetWebhook;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class BotConfiguration implements TelegramBotGlobalPropertiesConfiguration {

    @Value("${bricklink_bot.token}")
    private String token;
    @Value("${bricklink_bot.url}")
    private Optional<String> url;

    @Value("${bricklink_bot.port}")
    private int port;

    @Override
    public void configure(TelegramBotGlobalProperties.Builder telegramBotBuilder) {

        OkHttpClient okHttp = new OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .build();
        if(port != 0 && url.isPresent()) {
            telegramBotBuilder.setWebserverPort(port)
                              .configureBot(token, botBuilder -> {
                                  botBuilder.configure(builder -> builder.okHttpClient(okHttp));
                                  botBuilder.useWebhook(new SetWebhook().url(url.get()));
                              });
        }
    }
}
