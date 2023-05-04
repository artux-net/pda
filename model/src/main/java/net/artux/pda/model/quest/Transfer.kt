package net.artux.pda.model.quest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Transfer implements Serializable {

    private int stage;
    private String text;
    private HashMap<String, List<String>> condition;
    private HashMap<String, List<String>> actions;

}
