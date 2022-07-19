package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pda.map.models.UserGdx;


public class MoodComponent implements Component {

    public int group = -1;
    public Integer[] relations;
    public boolean angry;
    public boolean player;
    public boolean ignorePlayer;

    public Entity enemy;

    public MoodComponent(UserGdx userModel) {
        player = true;
        angry = false;
        group = userModel.getGroup();
        relations = new Integer[8];
        for (int i = 0; i < 8; i++) {
            relations[i] =userModel.getRelation(i);
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
}