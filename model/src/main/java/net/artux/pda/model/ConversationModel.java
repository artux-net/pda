package net.artux.pda.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConversationModel implements Serializable {

    private String title;
    private int id;
    private int type;
    private String lastMessage;
    private String avatar;

}