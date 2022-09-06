package net.artux.pda.model;

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


    public UserMessage(String senderLogin, String message, String avatarId) {
        author = new UserModel();
        author.setLogin(senderLogin);
        author.setAvatar(avatarId);

        content = message;
        timestamp = Instant.now();
    }

}
