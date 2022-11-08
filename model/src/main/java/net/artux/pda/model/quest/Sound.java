package net.artux.pda.model.quest;

import java.io.Serializable;

import lombok.Data;

@Data
public class Sound implements Serializable {

    private int id;
    private int type;
    private String name;
    private String url;
    private String[] params;

}
