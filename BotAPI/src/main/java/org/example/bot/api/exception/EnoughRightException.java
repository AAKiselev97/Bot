package org.example.bot.api.exception;

public class EnoughRightException extends RuntimeException {
    public EnoughRightException() {
    }

    public EnoughRightException(String message) {
        super(message);
    }

    public EnoughRightException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnoughRightException(Throwable cause) {
        super(cause);
    }

    public EnoughRightException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
