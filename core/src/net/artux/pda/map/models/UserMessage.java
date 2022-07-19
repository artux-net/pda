package net.artux.pda.map.models;

import java.io.Serializable;
import java.util.Date;

public class UserMessage implements Serializable {

    public int cid = -1;
    public String senderLogin;
    public String message;
    public long time;
    public int groupId = -1;
    public String avatarId;
    public int pdaId = -1;

    public UserMessage(UserGdx userModel, String message) {
        this.senderLogin = userModel.getLogin();
        this.groupId = userModel.getGroup();
        this.pdaId = userModel.getPdaId();
        this.avatarId = userModel.getAvatar();

        this.message = message;
        this.time = new Date().getTime();
    }


    public UserMessage(String senderLogin, String message, String avatarId) {
        this.senderLogin = senderLogin;
        this.message = message;
        this.avatarId = avatarId;
        this.time = new Date().getTime();
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "cid=" + cid +
                ", senderLogin='" + senderLogin + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", groupId=" + groupId +
                ", avatarId=" + avatarId +
                ", pdaId=" + pdaId +
                '}';
    }
}
