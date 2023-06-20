package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.Gang;

import java.util.Set;


public class MoodComponent implements Component {

    public final int gandId;
    public final Integer[] relations;

    public boolean player;

    public Entity enemy;

    public boolean immortal;
    public boolean untarget;
    public boolean angry;
    public boolean alwaysIgnored;
    public boolean angryOnPlayer;
    public boolean ignorePlayer;

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

    public MoodComponent() {
        gandId = -1;
        this.relations = new Integer[]{-5, -5, -5, -5, -5, -5, -5, -5, -5};
        this.angry = true;
    }

    public MoodComponent(Group group) {
        this(group.getGang(), group.getRelations(), group.getParams());
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
                    || moodComponent.gandId < relations.length && relations[moodComponent.gandId] < -2 && gandId != moodComponent.gandId; // is person

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