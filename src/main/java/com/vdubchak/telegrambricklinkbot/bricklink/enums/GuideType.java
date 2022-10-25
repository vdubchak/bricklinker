package com.vdubchak.telegrambricklinkbot.bricklink.enums;

public enum GuideType {
    STOCK("stock"),
    SOLD("sold"),
    SIMPLE("SIMPLE");
    private final String text;

    GuideType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
