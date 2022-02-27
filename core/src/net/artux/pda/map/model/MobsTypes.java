package net.artux.pda.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobsTypes {

    List<MobType> mobs = new ArrayList<>();
    HashMap<Integer, ArrayList<Integer>> relations = new HashMap<>();

    public MobType getMobType(int id) {
        for (MobType mobType : mobs){
            if (id== mobType.id){
                return mobType;
            }
        }
        return null;
    }

    public List<Integer> getRelations(int group){
        return relations.get(group);
    }
}
