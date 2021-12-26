package net.artux.pdalib;

import org.joda.time.Instant;

import java.io.Serializable;

public class UserMessage implements Serializable {

    public int cid = -1;
    public String senderLogin;
    public String message;
    public long time;
    public int groupId = -1;
    public String avatarId;
    public int pdaId = -1;

    public UserMessage(Member member, String message) {
        this.senderLogin = member.getLogin();
        this.groupId = member.getGroup();
        this.pdaId = member.getPdaId();
        this.avatarId = member.getAvatar();

        this.message = message;
        this.time = Instant.now().getMillis();
    }


    public UserMessage(String senderLogin, String message, String avatarId) {
        this.senderLogin = senderLogin;
        this.message = message;
        this.avatarId = avatarId;
        this.time = Instant.now().getMillis();
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
