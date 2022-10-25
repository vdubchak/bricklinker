package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ItemLotSoldData {
    private int quantity;
    @JsonProperty("unit_price")
    private String price;
    @JsonProperty("seller_country_code")
    private String sellerLocation;
    @JsonProperty("buyer_country_code")
    private String buyerLocation;
    @JsonProperty("date_ordered")
    LocalDateTime dateOrdered;

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append("Quantity: ").append(quantity).append(", Price: ").append(price).append(", Seller: ")
               .append(decorateCountry(sellerLocation)).append(", Buyer: ").append(decorateCountry(buyerLocation))
               .append(", Date: ").append(dateOrdered.toLocalDate().format(DateTimeFormatter.ISO_DATE));
       return sb.toString();
    }

    private String decorateCountry(String code) {
        int OFFSET = 127397;
        if(code == null || code.length() != 2) {
            return "";
        }
        if (code.equalsIgnoreCase("uk")) {
            code = "gb";
        }
        code = code.toUpperCase();
        StringBuilder emojiStr = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            emojiStr.appendCodePoint(code.charAt(i) + OFFSET);
        }
        return emojiStr.toString();
    }
}
