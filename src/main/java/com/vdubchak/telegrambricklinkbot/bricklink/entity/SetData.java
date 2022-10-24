package com.vdubchak.telegrambricklinkbot.bricklink.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83C\uDF81 Set Name: ").append(name).append("\n");
        sb.append("\uD83D\uDDD3 Year released: ").append(yearReleased).append("\n");
        sb.append("\uD83D\uDDBC Image link: ").append(imageUrl).append("\n");
        sb.append("\uD83D\uDE9A Weight: ").append(weight).append("g").append("\n");
        sb.append("\uD83D\uDCE6 Dimensions: ").append(dimX).append('x').append(dimY).append('x').append(dimZ).append("cm").append("\n");
        return sb.toString();
    }

}
