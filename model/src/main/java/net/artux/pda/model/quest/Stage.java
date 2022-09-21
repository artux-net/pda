package net.artux.pda.model.quest;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Stage {
    //todo camelCase
    private Integer id;
    private Integer type_stage;
    private String background_url;
    private int[] music;
    private String title;
    private String message;
    private Integer type_message;
    private List<Text> texts = null;
    private List<Transfer> transfers = null;
    private HashMap<String, List<String>> actions;
    private HashMap<String, String> data;

}
