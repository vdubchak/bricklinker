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
import com.vdubchak.telegrambricklinkbot.bricklink.enums.Condition;
import com.vdubchak.telegrambricklinkbot.bricklink.enums.ItemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@BotController
@Slf4j
public class BrickLinkBotController implements TelegramMvcController {

    private static final String HELP_RESPONSE = """
            Type /info {set-num} to see set info.
            Type /price {set-num} to see price guide.
            Example: /info 75003
            Example: /price 42069 U""";
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
        String response = brickLinkClient.getPrice(ItemType.SET, formatSetNumber(ItemType.SET, set), Condition.NEW).toString();
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(value = "/price {set:[\\d]+(?:-\\d)?} {state:NEW|USED}", type = {MessageType.MESSAGE, MessageType.CALLBACK_QUERY})
    public BaseRequest priceSet(@BotPathVariable("set") String set, @BotPathVariable("state") String stateStr, User user, Chat chat) {
        Condition state = Condition.valueOf(stateStr);
        String response = brickLinkClient.getPrice(ItemType.SET, formatSetNumber(ItemType.SET, set), state).toString();
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(value = "/price {itemType:SET|MINIFIG|PART} {number:[a-z0-9]{0,8}(?:-\\d)?} {state:NEW|USED}", type = {MessageType.MESSAGE, MessageType.CALLBACK_QUERY})
    public BaseRequest priceAll(@BotPathVariable("itemType") String itemTypeStr, @BotPathVariable("number") String number, @BotPathVariable("state") String stateStr, User user, Chat chat) {
        ItemType itemType = ItemType.valueOf(itemTypeStr);
        Condition state = Condition.valueOf(stateStr);
        String response = brickLinkClient.getPrice(itemType, formatSetNumber(itemType, number), state).toString();
        return new SendMessage(chat.id(), response);

    }

    @BotRequest(value = "/info {set:[\\d]+(?:-\\d)?}", type = {MessageType.MESSAGE})
    public BaseRequest infoDefault(@BotPathVariable("set") String number, User user, Chat chat) {
        BricklinkInfoEntity info = brickLinkClient.getInfo(ItemType.SET, formatSetNumber(ItemType.SET, number));
        SendMessage message = new SendMessage(chat.id(), info.toString());
        if(info.getData().getNo() != null) {
            message.replyMarkup(buildInfoMenu(ItemType.SET, number));
        }
        return message;
    }

    @BotRequest(value = "/info {number:[a-z]{1,3}[\\d]+.*}", type = {MessageType.MESSAGE})
    public BaseRequest infoMinifigure(@BotPathVariable("number") String number, User user, Chat chat) {
        BricklinkInfoEntity info = brickLinkClient.getInfo(ItemType.MINIFIG, formatSetNumber(ItemType.MINIFIG, number));
        SendMessage message = new SendMessage(chat.id(), info.toString());
        if(info.getData().getNo() != null) {
            message.replyMarkup(buildInfoMenu(ItemType.MINIFIG, number));
        }
        return message;

    }

    @BotRequest(value = "/help", type = {MessageType.MESSAGE})
    public BaseRequest helpResponse(User user, Chat chat) {
        return new SendMessage(chat.id(), HELP_RESPONSE);
    }

    @BotRequest(value = "/start", type = {MessageType.MESSAGE})
    public BaseRequest startResponse(User user, Chat chat) {
        return new SendMessage(chat.id(), "Welcome to brick link prices chat bot!\n" + HELP_RESPONSE);
    }

    private String formatSetNumber(ItemType type, String number) {
        if (type == ItemType.SET && !number.contains("-")) {
            number = number + "-1";
        }
        return number;
    }

    public Keyboard buildInfoMenu(ItemType type, String setNum) {
        InlineKeyboardButton priceButton = new InlineKeyboardButton("\uD83D\uDCCA Price guide");
        priceButton.callbackData("/price " + type + " " + setNum + " NEW");
        InlineKeyboardButton priceNewButton = new InlineKeyboardButton("\uD83C\uDD95 New");
        priceNewButton.callbackData("/price " + type + " " + setNum + " NEW");
        InlineKeyboardButton priceUsedButton = new InlineKeyboardButton("\uD83E\uDDF9 Used");
        priceUsedButton.callbackData("/price " + type + " " + setNum + " USED");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(priceButton);
        markup.addRow(priceNewButton, priceUsedButton);

        return markup;
    }
    
}
