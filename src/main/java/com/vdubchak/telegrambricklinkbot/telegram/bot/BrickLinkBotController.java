package com.vdubchak.telegrambricklinkbot.telegram.bot;

import com.github.kshashov.telegram.api.MessageType;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.BotPathVariable;
import com.github.kshashov.telegram.api.bind.annotation.BotRequest;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.vdubchak.telegrambricklinkbot.bricklink.BrickLinkClient;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkInfoEntity;
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
        String response = brickLinkClient.getPrice(formatSetNumber(set)).toString();
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(value = "/price {set:[\\d]+(?:-\\d)?} {state:N|U}", type = {MessageType.MESSAGE, MessageType.CALLBACK_QUERY})
    public BaseRequest pricePrivateNewUsed(@BotPathVariable("set") String set, @BotPathVariable("state") String state, User user, Chat chat) {
        String response = brickLinkClient.getPrice(formatSetNumber(set), state).toString();
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(value = "/info {set:[\\d]+(?:-\\d)?}", type = {MessageType.MESSAGE})
    public BaseRequest infoPrivate(@BotPathVariable("set") String set, User user, Chat chat) {
        BricklinkInfoEntity info = brickLinkClient.getInfo(formatSetNumber(set));
        SendMessage message = new SendMessage(chat.id(), info.toString());
        message.replyMarkup(buildInfoMenu(info.getData().getNo()));
        return message;

    }

    @BotRequest(type = {MessageType.MESSAGE})
    public BaseRequest defaultResponse(User user, Chat chat) {
        return new SendMessage(chat.id(), HELP_RESPONSE);
    }

    @BotRequest(value = "/help", type = {MessageType.MESSAGE})
    public BaseRequest helpResponse(User user, Chat chat) {
        return new SendMessage(chat.id(), HELP_RESPONSE);
    }

    @BotRequest(value = "/start", type = {MessageType.MESSAGE})
    public BaseRequest startResponse(User user, Chat chat) {
        return new SendMessage(chat.id(), HELP_RESPONSE);
    }

    private String formatSetNumber(String set) {
        if (!set.contains("-")) {
            set = set + "-1";
        }
        return set;
    }

    public Keyboard buildInfoMenu(String setNum) {
        InlineKeyboardButton priceButton = new InlineKeyboardButton("\uD83D\uDCCA Price guide");
        priceButton.callbackData("/price " + setNum + " N");
        InlineKeyboardButton priceNewButton = new InlineKeyboardButton("\uD83C\uDD95 New");
        priceNewButton.callbackData("/price " + setNum + " N");
        InlineKeyboardButton priceUsedButton = new InlineKeyboardButton("\uD83E\uDDF9 Used");
        priceUsedButton.callbackData("/price " + setNum + " U");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(priceButton);
        markup.addRow(priceNewButton, priceUsedButton);

        return markup;
    }

}
