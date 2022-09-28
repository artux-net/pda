package net.artux.pda.model.quest;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Transfer {

    private int stageId;
    private HashMap<String, List<String>> condition;
    private String text;
    private HashMap<String, List<String>> actions;

}
