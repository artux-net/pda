package net.artux.pda.ui.fragments.chat;

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