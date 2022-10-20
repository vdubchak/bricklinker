package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BricklinkPriceEntity extends AbstractBricklinkEntity {
    ItemForSaleData data;
}
