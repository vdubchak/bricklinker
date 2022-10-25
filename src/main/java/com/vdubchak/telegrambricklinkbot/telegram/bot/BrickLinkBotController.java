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
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkHistoricPriceEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkInfoEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.entity.BricklinkPriceEntity;
import com.vdubchak.telegrambricklinkbot.bricklink.enums.Condition;
import com.vdubchak.telegrambricklinkbot.bricklink.enums.GuideType;
import com.vdubchak.telegrambricklinkbot.bricklink.enums.ItemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@BotController
@Slf4j
public class BrickLinkBotController implements TelegramMvcController {

    private static final String HELP_RESPONSE = """
            Type /info {minifigure/set-number} to see item info.
            Type /price {item-num} to see price guide.
            Example: /info 4950
            Example: /info sw0547
            Example: /price 42069 USED""";
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

    @BotRequest(value = "/price {itemType:SET|MINIFIG|PART} {number:[a-z0-9]{0,8}(?:-\\d)?} {state:NEW|USED} {details:STOCK|SIMPLE|SOLD}",
            type = {MessageType.MESSAGE, MessageType.CALLBACK_QUERY})
    public BaseRequest priceAll(@BotPathVariable("itemType") String itemTypeStr,
                                @BotPathVariable("number") String number,
                                @BotPathVariable("state") String stateStr,
                                @BotPathVariable("details") String detailsStr,
                                User user, Chat chat) {
        ItemType itemType = ItemType.valueOf(itemTypeStr);
        Condition state = Condition.valueOf(stateStr);
        GuideType details = GuideType.valueOf(detailsStr);
        StringBuilder sb = new StringBuilder();
        if(details == GuideType.STOCK || details == GuideType.SIMPLE) {
            BricklinkPriceEntity response = brickLinkClient.getPrice(itemType, formatSetNumber(itemType, number),
                                                                     state);
            sb.append(response.toString());
            if (details == GuideType.STOCK && response.getData().getShopItems().size() > 0) {
                sb.append("\uD83D\uDCB0 Lots for sale: ").append("\n");
                response.getData().getShopItems().forEach(item -> sb.append(item.toString()).append("\n"));
            }
        } else if (details == GuideType.SOLD) {
            BricklinkHistoricPriceEntity response = brickLinkClient.getPriceHistory(itemType,
                                                                            formatSetNumber(itemType, number), state);
            sb.append(response);
        }
        return new SendMessage(chat.id(), sb.toString());

    }

    @BotRequest(value = "/info {set:[\\d]+(?:-\\d)?}", type = {MessageType.MESSAGE})
    public BaseRequest infoSet(@BotPathVariable("set") String number, User user, Chat chat) {
        BricklinkInfoEntity info = brickLinkClient.getInfo(ItemType.SET, formatSetNumber(ItemType.SET, number));
        SendMessage message = new SendMessage(chat.id(), info.toString());
        if(info.getData().getNo() != null) {
            message.replyMarkup(buildInfoMenu(ItemType.SET, number, chat));
        }
        return message;
    }

    @BotRequest(value = "/info {number:[a-z]{1,3}[\\d]+.*}", type = {MessageType.MESSAGE})
    public BaseRequest infoMinifigure(@BotPathVariable("number") String number, User user, Chat chat) {
        BricklinkInfoEntity info = brickLinkClient.getInfo(ItemType.MINIFIG, formatSetNumber(ItemType.MINIFIG, number));
        SendMessage message = new SendMessage(chat.id(), info.toString());
        if(info.getData().getNo() != null) {
            message.replyMarkup(buildInfoMenu(ItemType.MINIFIG, number, chat));
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

    public Keyboard buildInfoMenu(ItemType type, String setNum, Chat chat) {
        InlineKeyboardButton priceNewButton = new InlineKeyboardButton("\uD83C\uDD95 New item price");
        priceNewButton.callbackData("/price " + type + " " + setNum + " NEW SIMPLE");
        InlineKeyboardButton priceUsedButton = new InlineKeyboardButton("\uD83E\uDDF9 Used item price");
        priceUsedButton.callbackData("/price " + type + " " + setNum + " USED SIMPLE");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.addRow(priceNewButton, priceUsedButton);
        if(chat.type() != Chat.Type.supergroup && chat.type() != Chat.Type.channel && chat.type() != Chat.Type.group) {
            InlineKeyboardButton priceNewDetailedButton = new InlineKeyboardButton("\uD83D\uDCCA New price guide");
            priceNewDetailedButton.callbackData("/price " + type + " " + setNum + " NEW STOCK");
            InlineKeyboardButton priceUsedDetailedButton = new InlineKeyboardButton("\uD83D\uDCCA Used price guide");
            priceUsedDetailedButton.callbackData("/price " + type + " " + setNum + " USED STOCK");
            markup.addRow(priceNewDetailedButton, priceUsedDetailedButton);
            InlineKeyboardButton priceNewHistoryButton = new InlineKeyboardButton("\uD83D\uDD50 New price history");
            priceNewHistoryButton.callbackData("/price " + type + " " + setNum + " NEW SOLD");
            InlineKeyboardButton priceUsedHistoryButton = new InlineKeyboardButton("\uD83D\uDD50 Used price history");
            priceUsedHistoryButton.callbackData("/price " + type + " " + setNum + " USED SOLD");
            markup.addRow(priceNewHistoryButton, priceUsedHistoryButton);
        } else {
            InlineKeyboardButton botLinkButton = new InlineKeyboardButton("More info");
            botLinkButton.url("https://t.me/Bricklinking_Bot");
            markup.addRow(botLinkButton);
        }
        return markup;
    }
    
}
