package net.artux.pda.model;

import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.io.Serializable;
import java.time.Instant;

import lombok.Data;

@Data
public class UserMessage implements Serializable {

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
        author.setPdaId(storyDataModel.getPdaId());
        author.setNickname(storyDataModel.getNickname());
        author.setGang(storyDataModel.getGang());

        content = message;
        timestamp = Instant.now();
    }

    public UserMessage(String senderLogin, String message, String avatarId) {
        author = new UserModel();
        author.setLogin(senderLogin);
        author.setAvatar("avatars/a" + avatarId + ".png");

        content = message;
        timestamp = Instant.now();
    }

}
