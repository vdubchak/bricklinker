package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Meta {
    private String description;
    private String message;
    private int code;
}
