package com.vdubchak.telegrambricklinkbot.telegram.bot;

import com.github.kshashov.telegram.api.MessageType;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.BotPathVariable;
import com.github.kshashov.telegram.api.bind.annotation.BotRequest;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.vdubchak.telegrambricklinkbot.bricklink.BrickLinkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@BotController
@Slf4j
public class BrickLinkBotController implements TelegramMvcController {

    private static final String HELP_RESPONSE = """
            Type /info {set-num} to see set info.
            Type /price {set-num} to see price guide.
            Example: /price 75033-1""";
    private final BrickLinkClient brickLinkClient;

    @Value(value = "${bricklink_bot.token}")
    private String token;

    public BrickLinkBotController(BrickLinkClient brickLinkClient) {
        this.brickLinkClient = brickLinkClient;
    }

    @Override
    public String getToken() {
        return token;
    }

    @BotRequest(value = "/price {set:[\\d]+(?:-\\d)?}", type = {MessageType.MESSAGE})
    public BaseRequest pricePrivate(@BotPathVariable("set") String set, User user, Chat chat) {
        String response = brickLinkClient.getPrice(formatSetNumber(set));
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(value = "/price {set:[\\d]+(?:-\\d)?} {state:N|U}", type = {MessageType.MESSAGE})
    public BaseRequest pricePrivateNewUsed(@BotPathVariable("set") String set, @BotPathVariable("state") String state, User user, Chat chat) {
        String response = brickLinkClient.getPrice(formatSetNumber(set), state);
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(value = "/info {set:[\\d]+(?:-\\d)?}", type = {MessageType.MESSAGE})
    public BaseRequest infoPrivate(@BotPathVariable("set") String set, User user, Chat chat) {
        String response = brickLinkClient.getInfo(formatSetNumber(set));
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(type = {MessageType.MESSAGE})
    public BaseRequest defaultResponse(@BotPathVariable("set") String set, User user, Chat chat) {
        return new SendMessage(chat.id(), HELP_RESPONSE);
    }

    private String formatSetNumber(String set) {
        if(!set.contains("-")) {
            set = set + "-1";
        }
        return set;
    }
}
