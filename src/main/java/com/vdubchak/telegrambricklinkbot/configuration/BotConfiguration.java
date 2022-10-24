package com.vdubchak.telegrambricklinkbot.configuration;

import com.github.kshashov.telegram.config.TelegramBotGlobalProperties;
import com.github.kshashov.telegram.config.TelegramBotGlobalPropertiesConfiguration;
import com.github.kshashov.telegram.config.TelegramBotProperties;
import com.github.kshashov.telegram.handler.TelegramPollingService;
import com.github.kshashov.telegram.handler.TelegramService;
import com.github.kshashov.telegram.handler.TelegramUpdatesHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
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
        if (port != 0 && url.isPresent()) {
            telegramBotBuilder.setWebserverPort(port)
                              .configureBot(token, botBuilder -> {
                                  botBuilder.configure(builder -> builder.okHttpClient(okHttp));
                                  botBuilder.useWebhook(new SetWebhook().url(url.get()));
                              });
        }
    }

    @Bean
    @Qualifier("telegramServicesList")
    @Primary
    List<TelegramService> telegramServices(@Qualifier("telegramBotPropertiesList") List<TelegramBotProperties> botProperties, TelegramUpdatesHandler updatesHandler, TelegramBotGlobalProperties globalProperties, Optional<Javalin> server) {
        List<TelegramService> services = botProperties.stream()
                                                      .map(p -> {
                                                          // Register TelegramBot bean
                                                          TelegramBot bot = p.getBotBuilder().build();

                                                          // Let user process bot instance
                                                          if (globalProperties.getBotProcessors()
                                                                              .containsKey(p.getToken())) {
                                                              globalProperties.getBotProcessors()
                                                                              .get(p.getToken())
                                                                              .accept(bot);
                                                          }

                                                          // Create bot service
                                                          if (p.getWebhook() != null) {
                                                              return new CustomTelegramWebhookService(p, bot,
                                                                                                      updatesHandler,
                                                                                                      server.get());
                                                          } else {
                                                              return new TelegramPollingService(p, bot, updatesHandler);
                                                          }
                                                      }).collect(Collectors.toList());

        if (services.isEmpty()) {
            log.error("No bot configurations found");
        } else {
            log.info("Finished Telegram controllers scanning. Found {} bots", services.size());
        }

        return services;
    }

}
