package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import lombok.Data;

@Data
public class BricklinkPriceEntity extends AbstractBricklinkEntity {
    ItemForSaleData data;

    @Override
    public String toString() {
        if(meta.getCode() == 200 && data != null && data.getItem() != null && data.getItem().getNo() != null) {
            return data.toString();
        } else {
            return "Nothing found \uD83D\uDE14 Try different set or try again later.";
        }
    }
}
