package net.artux.pda.model.chat;

import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class UserMessage implements Serializable {

    private UUID id;
    private Type type;
    private UserModel author;
    private String content;
    private Instant timestamp;

    public UserMessage(UserModel userModel, String message) {
        author = userModel;
        content = message;
        timestamp = Instant.now();
    }

    public UserMessage(StoryDataModel storyDataModel, String message) {
        author = new UserModel();
        author.setLogin(storyDataModel.getLogin());
        author.setAvatar(storyDataModel.getAvatar());
        author.setPdaId((long) storyDataModel.getPdaId());
        author.setNickname(storyDataModel.getNickname());
        author.setGang(storyDataModel.getGang());

        content = message;
        timestamp = Instant.now();
    }

    public UserMessage(String senderLogin, String message, String avatarId) {
        author = new UserModel();
        author.setLogin(senderLogin);
        author.setAvatar(avatarId);
        author.setName(senderLogin);
        author.setNickname("");
        author.setRole(UserModel.Role.ADMIN);
        author.setPdaId(-1L);
        content = message;
        timestamp = Instant.now();
    }

    public static UserMessage event(ChatEvent chatEvent) {
        return new UserMessage("System", chatEvent.getContent(), "0");
    }

    public enum Type {
        OLD,
        NEW,
        UPDATE,
        DELETE
    }


}
