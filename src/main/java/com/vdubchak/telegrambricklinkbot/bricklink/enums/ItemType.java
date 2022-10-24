package com.vdubchak.telegrambricklinkbot.bricklink.enums;

public enum ItemType {
    SET("SET"),
    MINIFIG("MINIFIG"),
    PART("PART");
    private final String text;

    ItemType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
