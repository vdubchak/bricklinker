package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BricklinkInfoEntity extends AbstractBricklinkEntity{
    @JsonProperty("data")
    SetData data;

    @Override
    public String toString() {
        if(meta.getCode() == 200 && data != null && data.getName() != null) {
            return data.toString();
        } else {
            return "Nothing found \uD83D\uDE14 Try different set or try again later.";
        }
    }
}
