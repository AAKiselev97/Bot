package org.example.bot.api.model.telegram;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
public class TGChat implements Serializable {
    private Long chatId;
    private String chatName;
    private Integer badWords;
    private List<TGUser> tgUsers;
    private List<TGUser> top5List;

    public TGUser getUser(String userName) {
        Optional<TGUser> tgUser = tgUsers.stream().filter(s -> Objects.equals(s.getUserName(), userName)).findAny();
        if (tgUser.isPresent()) {
            return tgUser.get();
        } else {
            throw new RuntimeException("user not found");
        }
    }
}
