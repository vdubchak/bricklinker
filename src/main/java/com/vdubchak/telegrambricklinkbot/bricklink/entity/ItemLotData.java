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

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("Quantity: ").append(quantity).append(", Price: ").append(price).append(", Ships to Ukraine: ")
               .append(shipsToMe ? "\uD83C\uDDFA\uD83C\uDDE6" : "\uD83D\uDEAB");
       return sb.toString();
    }
}
