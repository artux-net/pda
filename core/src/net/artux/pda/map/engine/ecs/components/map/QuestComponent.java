package net.artux.pda.map.engine.ecs.components.map;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.map.Point;

public class QuestComponent implements Component {

    private final int chapter;
    private final int stage;

    public QuestComponent(Point point) {
        this.chapter = Integer.parseInt(point.getData().get("chapter"));
        this.stage = Integer.parseInt(point.getData().get("stage"));
    }

    public int getChapter() {
        return chapter;
    }

    public int getStage() {
        return stage;
    }

    public boolean contains(int chapter, int stage){
        return this.chapter == chapter && this.stage == stage;
    }

}