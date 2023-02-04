package net.artux.pda.map.engine.ecs.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.Point;

public class PointComponent implements Component {

    private final String title;
    private final Type type;
    private final Vector2 position;

    public PointComponent(Point point) {
        this.title = point.getName();
        switch (point.getType()) {
            case 0:
            case 1:
                type = Type.QUEST;
                break;
            case 4:
                type = Type.SELLER;
                break;
            case 5:
                type = Type.CACHE;
                break;
            case 6:
                type = Type.ADDITIONAL_QUEST;
                break;
            case 7:
                type = Type.TRANSFER;
                break;
            default:
                type = Type.HIDDEN;
                break;
        }
        position = Mappers.vector2(point.getPos());
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        HIDDEN,
        QUEST,
        SELLER,
        CACHE,
        ADDITIONAL_QUEST,
        TRANSFER
    }
}