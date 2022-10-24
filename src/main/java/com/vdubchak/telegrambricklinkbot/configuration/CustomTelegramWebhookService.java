package com.vdubchak.telegrambricklinkbot.configuration;

import com.github.kshashov.telegram.config.TelegramBotProperties;
import com.github.kshashov.telegram.handler.TelegramUpdatesHandler;
import com.github.kshashov.telegram.handler.TelegramWebhookService;
import com.pengrad.telegrambot.TelegramBot;
import io.javalin.Javalin;

import javax.validation.constraints.NotNull;

public class CustomTelegramWebhookService extends TelegramWebhookService {

    public CustomTelegramWebhookService(@NotNull TelegramBotProperties botProperties, TelegramBot bot, @NotNull TelegramUpdatesHandler updatesHandler, @NotNull Javalin server) {
        super(botProperties, bot, updatesHandler, server);
    }

    @Override
    public void stop() {
        //squelch
    }
}
