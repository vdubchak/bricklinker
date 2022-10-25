package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItemsForSaleData {
    @JsonProperty("item")
    private ItemData item;
    @JsonProperty("new_or_used")
    private String newOrUsed;
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("min_price")
    private String minPrice;
    @JsonProperty("max_price")
    private String maxPrice;
    @JsonProperty("avg_price")
    private String averagePrice;
    @JsonProperty("price_detail")
    private List<ItemLotData> shopItems;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCCC Item number: ").append(item.getNo()).append("\n");
        sb.append("\uD83E\uDDFC Condition: ").append(newOrUsed.equals("U") ? "Used" : "New").append("\n");
        sb.append("\uD83D\uDCB1 Currency: ").append(currencyCode).append("\n");
        sb.append("\uD83D\uDCC9 Minimum price: ").append(minPrice).append("\n");
        sb.append("\uD83D\uDCC8 Maximum price: ").append(maxPrice).append("\n");
        sb.append("\uD83E\uDDEE Average price: ").append(averagePrice).append("\n");

        return sb.toString();
    }
}
