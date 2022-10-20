package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SetData extends ItemData{
    private String name;
    @JsonProperty("category_id")
    private int categoryId;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("year_released")
    private int yearReleased;
    private String weight;
    @JsonProperty("dim_x")
    private String dimX;
    @JsonProperty("dim_y")
    private String dimY;
    @JsonProperty("dim_z")
    private String dimZ;

}
