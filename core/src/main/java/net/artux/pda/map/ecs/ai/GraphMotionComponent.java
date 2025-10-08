package net.artux.pda.map.ecs.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.engine.pathfinding.FlatTiledNode;
import net.artux.engine.pathfinding.TiledSmoothableGraphPath;

import java.util.Iterator;

public class GraphMotionComponent implements Component {

    public Vector2 movementTarget;
    public FlatTiledNode tempTarget;
    TiledSmoothableGraphPath<FlatTiledNode> path;
    public Iterator<FlatTiledNode> iterator;

    public GraphMotionComponent(Vector2 movementTarget) {
        setMovementTarget(movementTarget);
        path = new TiledSmoothableGraphPath<>();
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return path;
    }

    public void setMovementTarget(Vector2 movementTarget) {
        this.movementTarget = movementTarget;
    }

    public boolean isActive() {
        return movementTarget != null;
    }

    public void disable() {
        movementTarget = null;
        reset();
    }

    public void reset() {
        path.clear();
        tempTarget = null;
        iterator = null;
    }
}