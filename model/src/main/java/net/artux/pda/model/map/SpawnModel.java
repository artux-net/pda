package net.artux.pda.model.map;

import net.artux.pda.model.user.Gang;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class SpawnModel implements Serializable {

    private int id;
    private String title;
    private String description;
    private Gang group;
    private Strength strength;
    private int r;
    private int n;
    private String pos;
    private HashMap<String, String> data;
    private HashMap<String, List<String>> actions;
    private HashMap<String, List<String>> condition;

    public Set<String> getParams() {
        if (data == null)
            data = new HashMap<>();
        return data.keySet();
    }

}
