package net.artux.pda.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BotsTypes {

    List<BotType> mobs = new ArrayList<>();
    HashMap<Integer, Integer[]> relations = new HashMap<>();

    public BotType getMobType(int id) {
        for (BotType botType : mobs) {
            if (id == botType.id) {
                botType.relations = getRelations(id);
                return botType;
            }
        }
        return null;
    }

    public Integer[] getRelations(int group) {
        return relations.get(group);
    }
}
