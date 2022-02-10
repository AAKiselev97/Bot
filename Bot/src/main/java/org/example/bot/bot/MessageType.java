package org.example.bot.bot;

public enum MessageType {
    TEXT,
    COMMAND,
    ;

    public static MessageType parseStringToMessageType(String str) {
        if ("command".equalsIgnoreCase(str)) {
            return COMMAND;
        }
        return TEXT;
    }
}
