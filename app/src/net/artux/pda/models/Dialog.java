package net.artux.pda.models;

public class Dialog {

     public String title;
     public int id;
     public int type;
     public String lastMessage;
     public String avatar;

     public int getAvatarId(){
        return Integer.parseInt(avatar);
    }
}