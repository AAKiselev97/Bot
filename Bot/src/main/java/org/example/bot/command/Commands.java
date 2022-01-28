package org.example.bot.command;

public enum Commands {
    START("/start - команда для первого входа"),
    HELLO("/hello - поздороваться с ботом"),
    HELP("/help - отправляет список команд"),
    STAT("/stat [@userName] - отправляет статистику пользователя @userName в этом чате"),
    STATCHAT("/statchat - отправляет статистику по данному чату"),
    GETCHATID("/getChatId - отправляет id чата, где была вызвана команда"),
    TOP("/top - отправляет топ 5 пользователей по активности в данном чате"),
    TOKEN("/token - отправляет токен для получения статистики"),
    EMPTY("/empty - ничего не делает");
    private final String description;

    Commands(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + " - " + description;
    }

    public static Commands stringToCommand(String string) {
        for (Commands commands : Commands.values()) {
            if (commands.name().equalsIgnoreCase(string.trim())) {
                return commands;
            }
        }
        return EMPTY;
    }

    public static boolean isCommand(String string) {
        return !stringToCommand(string).equals(EMPTY);
    }
}
