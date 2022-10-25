package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.UserModel;


public class MoodComponent implements Component {

    public int group = -1;
    public Integer[] relations;
    public boolean angry;
    public boolean player;
    public boolean ignorePlayer;

    public Entity enemy;

    public MoodComponent(UserModel userModel) {
        player = true;
        angry = false;
        group = userModel.getGang().getId();
        relations = new Integer[9];
        for (Gang gang : Gang.values()) {
            relations[gang.getId()] = userModel.getRelations().getFor(gang);
        }
    }

    public MoodComponent(int group, Integer[] relations, boolean angry) {
        this.group = group;
        this.relations = relations;
        this.angry = angry;
    }

    public boolean isEnemy(MoodComponent moodComponent) {
        boolean response;
        if ((ignorePlayer && moodComponent.player) || (player && moodComponent.ignorePlayer))
            response = false;
        else
            response = moodComponent.group < 0 // is animal
                    || angry
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