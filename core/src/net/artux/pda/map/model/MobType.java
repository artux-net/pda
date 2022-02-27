package net.artux.pda.map.model;

import java.util.ArrayList;
import java.util.List;

public class MobType {

    public int id;
    public String name;
    public int group;
    public List<Integer> armor = new ArrayList<>();
    public List<String> weapon = new ArrayList<>();
    public List<String> items = new ArrayList<>();
    public int travel;

    MobType(){
        id = -1;
        name = "Undefined";
        group = 0;
        travel = 0;
    }
}
