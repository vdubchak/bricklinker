package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class AbstractBricklinkEntity {
    Meta meta;
}
