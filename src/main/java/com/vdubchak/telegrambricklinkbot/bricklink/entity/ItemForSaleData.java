package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItemForSaleData {
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
}
