package net.artux.pda.model;

import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.UserModel;

import java.io.Serializable;
import java.time.Instant;

public class UserMessage implements Serializable {

    private long id;
    private String login;
    private Gang gang;
    private String avatar;
    private long pdaId;
    private String content;
    private Instant timestamp;

    public UserMessage(UserModel userModel, String message) {
        this.login = userModel.getLogin();
        this.gang = userModel.getGang();
        this.pdaId = userModel.getPdaId();
        this.avatar = userModel.getAvatar();

        this.content = message;
        timestamp = Instant.now();
    }


    public UserMessage(String senderLogin, String message, String avatarId) {
        this.login = senderLogin;
        this.content = message;
        this.avatar = avatarId;
        timestamp = Instant.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Gang getGang() {
        return gang;
    }

    public void setGang(Gang gang) {
        this.gang = gang;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getPdaId() {
        return pdaId;
    }

    public void setPdaId(long pdaId) {
        this.pdaId = pdaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
