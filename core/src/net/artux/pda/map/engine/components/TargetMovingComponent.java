package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.pathfinding.TiledNode;
import net.artux.pda.map.engine.pathfinding.TiledSmoothableGraphPath;

import java.util.Iterator;

public class TargetMovingComponent implements Component {
    public Targeting targeting;
    public Vector2 movementTarget;
    TiledSmoothableGraphPath<FlatTiledNode> path;
    public Iterator<FlatTiledNode> iterator;
    public FlatTiledNode tempTarget;

    public TargetMovingComponent(Targeting targeting) {
        this.targeting = targeting;
        path = new TiledSmoothableGraphPath<FlatTiledNode>();
    }

    public void setNextTarget(){
        setMovementTarget(targeting.getTarget());
    }

    public Targeting getTargeting() {
        return targeting;
    }

    public void setMovementTarget(Vector2 movementTarget) {
        this.movementTarget = movementTarget;
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return path;
    }

    public interface Targeting{

        Vector2 getTarget();

    }

}