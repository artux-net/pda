package net.artux.pda.model.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class Point implements Serializable {

    private int type;
    private String name;
    private String pos;
    private HashMap<String, String> data;
    private HashMap<String, List<String>> condition;


}
