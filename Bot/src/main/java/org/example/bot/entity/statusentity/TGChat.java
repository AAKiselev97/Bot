package org.example.bot.entity.statusentity;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

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

    public void checkTop5() {
        tgUsers.sort(Comparator.comparingInt(TGUser::getBadWordsCounter));
        top5List = new ArrayList<>();
        for (int i = 1; i < 6 & i <= tgUsers.size(); i++) {
            top5List.add(tgUsers.get(tgUsers.size() - i));
        }
    }
}
