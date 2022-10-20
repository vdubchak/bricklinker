package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BricklinkInfoEntity extends AbstractBricklinkEntity{
    @JsonProperty("data")
    SetData data;
}
