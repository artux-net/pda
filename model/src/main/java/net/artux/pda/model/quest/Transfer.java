package net.artux.pda.model.quest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Transfer implements Serializable {

    private int stageId;
    private HashMap<String, List<String>> condition;
    private String text;
    private HashMap<String, List<String>> actions;

}
