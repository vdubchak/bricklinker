package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemLotData {
    private int quantity;
    @JsonProperty("unit_price")
    private String price;
    @JsonProperty("shipping_available")
    private boolean shipsToMe;
}
