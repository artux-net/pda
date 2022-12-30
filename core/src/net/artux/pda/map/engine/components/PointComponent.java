package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.Point;

public class PointComponent implements Component {

    private final String title;
    private final Type type;
    private final int chapter;
    private final int stage;
    private final Vector2 position;

    public PointComponent(Point point, String title, int chapter, int stage) {
        this.title = title;
        switch (point.getType()) {
            case -1:
            case 7:
                this.type = Type.TRANSFER;
                break;
            default:
                this.type = Type.DIALOG;
                break;
        }
        this.chapter = chapter;
        this.stage = stage;
        position = Mappers.vector2(point.getPos());
    }

    public PointComponent(Point point) {
        this.title = point.getName();
        switch (point.getType()) {
            case -1:
            case 7:
                this.type = Type.TRANSFER;
                break;
            default:
                this.type = Type.DIALOG;
                break;
        }
        this.chapter = Integer.parseInt(point.getData().get("chapter"));
        this.stage = Integer.parseInt(point.getData().get("stage"));
        position = Mappers.vector2(point.getPos());
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public int getChapter() {
        return chapter;
    }

    public int getStage() {
        return stage;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return 31 * chapter * stage;
    }

    enum Type {
        DIALOG,
        TRANSFER
    }
}