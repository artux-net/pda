package net.artux.pda.model.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class SpawnModel implements Serializable {

    private int id;
    private int r;
    private int n;
    private String pos;
    private boolean angry;
    private boolean ignorePlayer;
    private HashMap<String, List<String>> condition;
    private HashMap<String, String> data;
    private HashMap<String, List<String>> actions;

    public Set<String> getParams() {
        if (data == null)
            data = new HashMap<>();
        if (angry)
            data.put("angry", "");
        if (ignorePlayer)
            data.put("ignorePlayer", "");
        return data.keySet();
    }

}
