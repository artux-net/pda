package net.artux.pda.map.model;

import static com.badlogic.gdx.math.MathUtils.random;

import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.model.user.Gang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class GangRelations {

    private final HashMap<Gang, Integer[]> relations;

    public GangRelations(HashMap<Gang, Integer[]> relations) {
        this.relations = relations;
    }

    public MoodComponent getMoodBy(Gang gang, Set<String> params) {
        return new MoodComponent(gang.getId(), get(gang), params);
    }

    public Gang findEnemyByGang(Gang gang) {
        for (Gang potentialEnemy : Gang.values()) {
            if (getMoodBy(potentialEnemy, Collections.emptySet()).isEnemy(getMoodBy(gang, Collections.emptySet())))
                return potentialEnemy;
        }
        return null;
    }

    public Gang random() {
        return Gang.values()[random.nextInt(Gang.values().length)];
    }

    public Integer[] get(Gang group) {
        return relations.get(group);
    }
}
