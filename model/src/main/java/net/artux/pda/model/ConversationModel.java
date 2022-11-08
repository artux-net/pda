package net.artux.pda.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConversationModel implements Serializable {

    private int id;
    private int type;
    private String title;
    private String lastMessage;
    private String avatar;

}