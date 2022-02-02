package org.example.bot.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.api.exception.BadRequestException;
import org.example.bot.api.exception.ServerErrorException;
import org.example.bot.api.service.BotApiService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/telegram/bot/get")
public class TelegramBotApiController {
    private final static Logger log = LogManager.getLogger(TelegramBotApiController.class);
    private final BotApiService botApiService;

    public TelegramBotApiController(BotApiService botApiService) {
        this.botApiService = botApiService;
    }

    @GetMapping(value = "/user&{token}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUser(@PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getUserStats(token), HttpStatus.OK);
    }

    @GetMapping(value = "/chat/{chatId}&{token}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getChat(@PathVariable(name = "chatId") String chatId, @PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getChatStats(chatId, token), HttpStatus.OK);
    }

    @GetMapping(value = "/userByChat/{chatId}&{token}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUserByChat(@PathVariable(name = "chatId") String chatId, @PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getUserStatsByChat(chatId, token), HttpStatus.OK);
    }

    @GetMapping(value = "/formUserHistory/{token}")
    public ResponseEntity<?> formUserHistory(@PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.formUserHistory(token), HttpStatus.OK);
    }

    @GetMapping(value = "/getUserHistory/{token}", produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource> getUserHistory(@PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getUserHistory(token), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e);
        if (e instanceof BadRequestException) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        } else if (e instanceof ServerErrorException) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(e, HttpStatus.NOT_IMPLEMENTED);
        }
    }
}
