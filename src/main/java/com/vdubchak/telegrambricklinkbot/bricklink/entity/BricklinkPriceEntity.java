package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BricklinkPriceEntity extends AbstractBricklinkEntity {
    ItemsForSaleData data;

    @Override
    public String toString() {
        if(meta.getCode() == HttpStatus.OK.value() && data != null && data.getItem() != null && data.getItem().getNo() != null) {
            return data.toString();
        } else {
            return "Nothing found \uD83D\uDE14 Try different set or try again later.";
        }
    }
}
