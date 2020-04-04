package net.artux.pda.Models;
public class Dialog {

    public String name;
    private int toPdaId;
    private int toPdaAvatarId;
    private String toPdaLogin;
    private String lastMessage;

    public Dialog(String name, int toPdaId, String toPdaLogin, int toPdaAvatarId, String lastMessage) {
        this.name = name;
        this.toPdaId = toPdaId;
        this.toPdaLogin = toPdaLogin;
        this.toPdaAvatarId = toPdaAvatarId;
        this.lastMessage = lastMessage;
    }

    public int getToPdaId() {
        return toPdaId;
    }

    public int getToPdaAvatarId() {
        return toPdaAvatarId;
    }

    public String getToPdaLogin() {
        return toPdaLogin;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
