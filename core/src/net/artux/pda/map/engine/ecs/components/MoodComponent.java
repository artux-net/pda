package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.Gang;

import java.util.Set;


public class MoodComponent implements Component {

    public int group = -1;
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
        group = storyDataModel.getGang().getId();
        relations = new Integer[9];
        for (Gang gang : Gang.values()) {
            relations[gang.getId()] = storyDataModel.getRelations().getFor(gang);
        }
    }

    public MoodComponent(int group, Integer[] relations, Set<String> params) {
        this.group = group;
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
            return moodComponent.relations[group] < -2;
        else
            //usual compare
            response = moodComponent.group < 0 // is animal
                    || moodComponent.group < relations.length && relations[moodComponent.group] < -2 && group != moodComponent.group; // is person

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
        if (moodComponent.group < relations.length)
            return relations[moodComponent.group];
        else return -10;
    }

    public void setRelation(MoodComponent moodComponent, int relation) {
        if (moodComponent.group < relations.length)
            relations[moodComponent.group] = relation;
    }

}