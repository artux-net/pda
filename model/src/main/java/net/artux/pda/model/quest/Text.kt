package net.artux.pda.model.quest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Text implements Serializable {

    private String text;
    private HashMap<String, List<String>> condition;

}
