package net.artux.pda.ui.fragments.chat;

import java.io.Serializable;

public class Dialog implements Serializable {

     public String title;
     public int id;
     public int type;
     public String lastMessage;
     public String avatar;

     public int getAvatarId(){
        return Integer.parseInt(avatar);
    }
}