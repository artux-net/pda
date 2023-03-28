package net.artux.pda.map.engine.ecs.entities.model;

import static com.badlogic.gdx.math.MathUtils.random;

import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.user.Gang;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

@PerGameMap
public class GangRelations {

    private HashMap<Gang, Integer[]> relations;

    @Inject
    public GangRelations() {

    }

    public MoodComponent getMoodBy(Gang gang, Set<String> params) {
        return new MoodComponent(gang.getId(), get(gang), params);
    }

    public HashMap<Gang, Integer[]> getRelations() {
        return relations;
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
