package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

@Data
public class BricklinkInfoEntity extends AbstractBricklinkEntity{
    @JsonProperty("data")
    SetData data;

    @Override
    public String toString() {
        if(meta.getCode() == HttpStatus.OK.value() && data != null && StringUtils.isNotEmpty(data.getName())) {
            return data.toString();
        } else {
            return "Nothing found \uD83D\uDE14 Try different set or try again later.";
        }
    }
}
