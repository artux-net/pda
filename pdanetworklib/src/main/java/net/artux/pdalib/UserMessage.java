package net.artux.pdalib;

import java.io.Serializable;

public class UserMessage implements Serializable {

    public int cid = -1;
    public String senderLogin;
    public String message;
    public long time;
    public int groupId;
    public String avatarId;
    public int pdaId;

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
