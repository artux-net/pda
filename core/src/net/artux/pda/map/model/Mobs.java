package net.artux.pda.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mobs {

    List<Mob> mobs = new ArrayList<>();
    HashMap<Integer, ArrayList<Integer>> relations = new HashMap<>();

    public Mob getMob(int id) {
        for (Mob mob:mobs){
            if (id==mob.id){
                return mob;
            }
        }
        return null;
    }

    public List<Integer> getRelations(int group){
        return relations.get(group);
    }
}
