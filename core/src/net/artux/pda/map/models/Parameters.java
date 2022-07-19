package net.artux.pda.map.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parameters {

    public List<String> keys = new ArrayList<>();
    public HashMap<String, Integer> values = new HashMap<>();

    @Override
    public String toString() {
        return "keys=" + keys +
                ", values=" + values;
    }
}
