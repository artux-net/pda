package net.artux.pda.model.quest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Stage implements Serializable {

    private Integer id;
    private Integer typeStage;
    private String background;
    private int[] music;
    private String title;
    private String message;
    private Integer typeMessage;
    private List<Text> texts = null;
    private List<Transfer> transfers = null;
    private HashMap<String, List<String>> actions;
    private HashMap<String, String> data;

}
