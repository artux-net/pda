package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import net.artux.pdalib.Member;

public class MoodComponent implements Component {

    public int group = -1;
    public Integer[] relations;
    public boolean angry;
    public boolean player;
    public boolean ignorePlayer;

    public Entity enemy;

    public MoodComponent(Member member) {
        player = true;
        angry = false;
        group = member.getGroup();
        relations = member.relations.toArray(new Integer[0]);
    }

    public MoodComponent(int group, Integer[] relations, boolean angry) {
        this.group = group;
        this.relations = relations;
        this.angry = angry;
    }

    public boolean isEnemy(MoodComponent moodComponent) {
        boolean response;
        if ((ignorePlayer && moodComponent.player) || (player&& moodComponent.ignorePlayer))
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