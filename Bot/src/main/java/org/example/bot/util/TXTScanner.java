package org.example.bot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TXTScanner {
    private static final String HELLO_MESSAGE_FILENAME = "helloText.txt";
    private static final String UNKNOWN_MESSAGE_FILENAME = "unknownText.txt";
    private static final String WORD_SEPARATOR_FILENAME = "wordSeparators.txt";
    private static final String BAD_WORDS_FILENAME = "badWords.txt";


    public static List<String> getUnknownMessageList() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(UNKNOWN_MESSAGE_FILENAME)), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.toList());
        }
    }

    public static List<String> getHelloMessageList() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(HELLO_MESSAGE_FILENAME)), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.toList());
        }
    }

    public static List<String> getWordSeparatorList() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(WORD_SEPARATOR_FILENAME)), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.toList());
        }
    }

    public static List<String> getBadWordsList() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(BAD_WORDS_FILENAME)), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.toList());
        }
    }
}