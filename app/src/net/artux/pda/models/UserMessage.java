package net.artux.pda.models;

import net.artux.pda.models.user.Gang;
import net.artux.pda.models.user.UserModel;

import org.joda.time.Instant;

import java.io.Serializable;

public class UserMessage implements Serializable {

    private long id;
    private String login;
    private Gang gang;
    private String avatar;
    private long pdaId;
    private String content;
    private long timestamp;

    public UserMessage(UserModel userModel, String message) {
        this.login = userModel.getLogin();
        this.gang = userModel.getGang();
        this.pdaId = userModel.getPdaId();
        this.avatar = userModel.getAvatar();

        this.content = message;
        timestamp = Instant.now().getMillis();
    }


    public UserMessage(String senderLogin, String message, String avatarId) {
        this.login = senderLogin;
        this.content = message;
        this.avatar = avatarId;
        timestamp = Instant.now().getMillis();
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
