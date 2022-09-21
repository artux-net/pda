package net.artux.pda.model.quest;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Transfer {

    private int stage_id;
    private HashMap<String, List<String>> condition;
    private String text;
    private HashMap<String, List<String>> actions;

}
