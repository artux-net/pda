package net.artux.pda.map.engine.entities.model;

import static com.badlogic.gdx.math.MathUtils.random;

import net.artux.pda.map.ecs.battle.MoodComponent;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.GangRelation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

@PerGameMap
public class GangRelations extends ArrayList<GangRelation> {

    @Inject
    public GangRelations() {
    }

    public MoodComponent getMoodBy(Gang gang, Set<String> params) {
        return new MoodComponent(gang, get(gang), params);
    }

    public HashMap<Gang, Integer[]> getRelations() {
        HashMap<Gang, Integer[]> map = new HashMap<>();
        for (Gang gang : Gang.values()) {
            Integer[] relations = new Integer[9];
            for (Gang gang1 : Gang.values()) {
                relations[gang1.getId()] = get(gang).getFor(gang1);
            }
            map.put(gang, relations);
        }
        return map;
    }

    public Gang findEnemyByGang(Gang gang) {
        for (Gang potentialEnemy : Gang.values()) {
            if (getMoodBy(potentialEnemy, Collections.emptySet()).isEnemy(getMoodBy(gang, Collections.emptySet())))
                return potentialEnemy;
        }
        return null;
    }

    public Gang findEnemyByGangFromCurrentMap(Gang gang, GameMap map) {
        List<Gang> gangs = map.getSpawns().stream()
                .map(SpawnModel::getGroup)
                .filter(Objects::nonNull)
                .toList();

        for (Gang potentialEnemy : gangs) {
            if (getMoodBy(potentialEnemy, Collections.emptySet()).isEnemy(getMoodBy(gang, Collections.emptySet())))
                return potentialEnemy;
        }
        return null;
    }

    public Gang random() {
        return Gang.values()[random.nextInt(Gang.values().length)];
    }

    public GangRelation get(Gang group) {
        return get(group.getId());
    }

    public Integer[] getRelations(Gang gang){
        Integer[] relations = new Integer[9];
        GangRelation relation = get(gang);
        for (Gang gang1 : Gang.values()) {
            relations[gang1.getId()] = relation.getFor(gang);
        }
        return relations;
    }
}
