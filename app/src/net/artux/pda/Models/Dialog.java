package net.artux.pda.Models;

public class Dialog {

    public String name;
    public int type;
    public String lastMessage;
    public String login;
    public int pda;
    public int group;
    public String avatar;

    public int getAvatarId(){
        return Integer.parseInt(avatar);
    }
}