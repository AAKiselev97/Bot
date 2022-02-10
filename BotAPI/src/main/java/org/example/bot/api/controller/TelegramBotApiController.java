package org.example.bot.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bot.api.exception.BadRequestException;
import org.example.bot.api.exception.EnoughRightException;
import org.example.bot.api.exception.ServerErrorException;
import org.example.bot.api.model.telegram.MessageInDB;
import org.example.bot.api.model.telegram.TGChat;
import org.example.bot.api.model.telegram.TGUser;
import org.example.bot.api.service.BotApiService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ApiResponse(
        responseCode = "400",
        description = "BAD_REQUEST, parameters not valid",
        content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = BadRequestException.class)))
        }
)
@ApiResponse(
        responseCode = "500",
        description = "INTERNAL_SERVER_ERROR, server not available",
        content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = ServerErrorException.class)))
        }
)
@ApiResponse(
        responseCode = "403",
        description = "FORBIDDEN, not enough right",
        content = {
                @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = EnoughRightException.class)))
        }
)
@RequestMapping("/telegram/bot/get")
public class TelegramBotApiController {
    private final static Logger log = LogManager.getLogger(TelegramBotApiController.class);
    private final BotApiService botApiService;

    public TelegramBotApiController(BotApiService botApiService) {
        this.botApiService = botApiService;
    }

    @Operation(summary = "Get user by token", parameters = {
            @Parameter(name = "token", description = "**Token**. **Example: f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454**.")
    })
    @Tag(name = "User", description = "get user")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "found user",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TGUser.class)))
            }
    )})
    @GetMapping(value = "/user&{token}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUser(@PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getUserStats(token), HttpStatus.OK);
    }

    @Operation(summary = "Get chat by chatId & token", parameters = {
            @Parameter(name = "token", description = "**Token**. **Example: f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454**."),
            @Parameter(name = "chatId", description = "**chatId**. **Example: 10004114231**.")
    })
    @Tag(name = "Chat", description = "get chat")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "found chat",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TGChat.class)))
            }
    )})
    @GetMapping(value = "/chat/{chatId}&{token}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getChat(@PathVariable(name = "chatId") String chatId, @PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getChatStats(chatId, token), HttpStatus.OK);
    }

    @Operation(summary = "Get user by chatId & token", parameters = {
            @Parameter(name = "token", description = "**Token**. **Example: f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454**."),
            @Parameter(name = "chatId", description = "**chatId**. **Example: 10004114231**.")
    })
    @Tag(name = "User", description = "get user")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "found user by chat",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TGUser.class)))
            }
    )})
    @GetMapping(value = "/userByChat/{chatId}&{token}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUserByChat(@PathVariable(name = "chatId") String chatId, @PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getUserStatsByChat(chatId, token), HttpStatus.OK);
    }

    @Operation(summary = "Form user history by token", parameters = {
            @Parameter(name = "token", description = "**Token**. **Example: f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454**.")
    })
    @Tag(name = "PDF")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "request send"
    )})
    @GetMapping(value = "/formUserHistory/{token}")
    public ResponseEntity<?> formUserHistory(@PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.formUserHistory(token), HttpStatus.OK);
    }

    @Operation(summary = "Get user history by token in formUserHistory", parameters = {
            @Parameter(name = "token", description = "**Token**. **Example: cb151240-9237-4bf9-8ef2-96ca5268f910_1140509085**.")})
    @Tag(name = "PDF")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "return pdf with message history",
            content = {
                    @Content(
                            mediaType = "application/pdf")
            }
    )})
    @GetMapping(value = "/getUserHistory/{token}", produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource> getUserHistory(@PathVariable(name = "token") String token) {
        return new ResponseEntity<>(botApiService.getUserHistory(token), HttpStatus.OK);
    }

    @Operation(summary = "Fulltext search by token & text by page", parameters = {
            @Parameter(name = "token", description = "**Token**. **Example: cb151240-9237-4bf9-8ef2-96ca5268f910_1140509085**."),
            @Parameter(name = "text", description = "**Text**. **Example: random text**"),
            @Parameter(name = "page", description = "**Page**. **Example: 1**.")})
    @Tag(name = "Message", description = "get message")
    @ApiResponses(value = {@ApiResponse(
            responseCode = "200",
            description = "found messages",
            content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MessageInDB.class)))
            }
    )})
    @GetMapping(value = "/searchByText/{token}&{text}/page{page}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<MessageInDB>> searchByText(@PathVariable(name = "token") String token, @PathVariable(name = "text") String text, @PathVariable(name = "page") int page) {
        return new ResponseEntity<>(botApiService.searchByText(token, text, page), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e);
        if (e instanceof BadRequestException) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        } else if (e instanceof ServerErrorException) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(e, HttpStatus.FORBIDDEN);
        }
    }
}
