package com.vdubchak.telegrambricklinkbot.bricklink.enums;

public enum Condition {
        NEW("N"),
        USED("U");
        private final String text;

        Condition(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
}
