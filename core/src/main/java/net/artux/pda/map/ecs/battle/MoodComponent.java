package net.artux.pda.map.ecs.battle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.map.ecs.ai.StalkerGroup;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.GangRelation;

import java.util.Set;

/**
 * Настроение сущности,
 * отвечает за отношение текущей сущности к другим (по группировке или виду),
 * определяет текущего врага сущности
 */
public class MoodComponent implements Component {

    public final int gandId;
    public final Integer[] relations;

    public boolean player;

    public Entity enemy;

    public boolean immortal; // бессмертный
    public boolean untarget; // невозможно выбрать как врага
    public boolean angry; // злой на всех, даже если отношение дружелюбное будет атаковать
    public boolean alwaysIgnored; // всегда будет проигнорирован
    public boolean angryOnPlayer; // всегда атаковать игрока
    public boolean ignorePlayer; // всегда игнорировать игрока

    public MoodComponent(StoryDataModel storyDataModel) {
        player = true;
        angry = false;
        gandId = storyDataModel.getGang().getId();
        relations = new Integer[9];
        for (Gang gang : Gang.values()) {
            relations[gang.getId()] = storyDataModel.getRelations().getFor(gang);
        }
    }

    public MoodComponent(Gang gang, Integer[] relations, Set<String> params) {
        this.gandId = gang.getId();
        if (relations.length != Gang.values().length)
            throw new RuntimeException("Wrong length");
        this.relations = relations;
        this.alwaysIgnored = params.contains("alwaysIgnored");
        this.angry = params.contains("angry");
        immortal = params.contains("immortal");
        untarget = params.contains("untarget");
        this.angryOnPlayer = params.contains("angryOnPlayer");
        this.ignorePlayer = params.contains("ignorePlayer");
    }

    public MoodComponent(Gang gang, GangRelation relation, Set<String> params) {
        this.gandId = gang.getId();
        relations = new Integer[9];
        for (Gang gang1 : Gang.values()) {
            relations[gang1.getId()] = relation.getFor(gang);
        }
        this.alwaysIgnored = params.contains("alwaysIgnored");
        this.angry = params.contains("angry");
        immortal = params.contains("immortal");
        untarget = params.contains("untarget");
        this.angryOnPlayer = params.contains("angryOnPlayer");
        this.ignorePlayer = params.contains("ignorePlayer");
    }

    public MoodComponent() {
        gandId = -1;
        this.relations = new Integer[]{-100, -100, -100, -100, -100, -100, -100, -100, -100};
        this.angry = true;
    }

    public MoodComponent(Gang gang) {
        gandId = gang.getId();
        this.relations = new Integer[]{100, 100, 100, 100, 100, 100, 100, 100, 100};
        this.ignorePlayer = true;
        this.alwaysIgnored = true;
    }

    public MoodComponent(StalkerGroup stalkerGroup) {
        this(stalkerGroup.getGang(), stalkerGroup.getRelations(), stalkerGroup.getParams());
    }

    public boolean isEnemy(MoodComponent moodComponent) {
        boolean response;
        if (moodComponent.alwaysIgnored)
            return false;
        if (ignorePlayer && moodComponent.player)
            return false;
        if (angryOnPlayer && moodComponent.player)
            return true;
        if (angry)
            return true;
        if (moodComponent.player)
            return moodComponent.relations[gandId] < -2;
        else
            //usual compare
            response = moodComponent.gandId < 0 // is animal
                    || moodComponent.gandId < relations.length && relations[moodComponent.gandId] < -40 && gandId != moodComponent.gandId; // is person

        return response;
    }

    public Entity getEnemy() {
        return enemy;
    }

    public void setEnemy(Entity enemy) {
        this.enemy = enemy;
    }

    public boolean hasEnemy() {
        return enemy != null;
    }

    public int getRelation(MoodComponent moodComponent) {
        if (moodComponent.gandId < relations.length)
            return relations[moodComponent.gandId];
        else return -10;
    }

    public void setRelation(MoodComponent moodComponent, int relation) {
        if (moodComponent.gandId < relations.length)
            relations[moodComponent.gandId] = relation;
    }

}