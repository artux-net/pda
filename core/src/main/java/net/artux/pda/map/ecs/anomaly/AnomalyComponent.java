package net.artux.pda.map.ecs.anomaly;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Timer;

import net.artux.engine.utils.LocaleBundleHolder;
import net.artux.pda.map.engine.entities.model.Anomaly;

public class AnomalyComponent implements Component {

    private final Anomaly anomaly;
    private final int size;
    private long timeToActivate;
    private Timer.Task delayedInteraction;

    public AnomalyComponent(Anomaly anomaly, int size) {
        this.anomaly = anomaly;
        this.size = size;
    }

    public boolean isScheduled(){
        return delayedInteraction != null && delayedInteraction.isScheduled();
    }

    public Anomaly getAnomaly() {
        return anomaly;
    }

    public void setDelayedInteraction(Timer.Task delayedInteraction) {
        timeToActivate = delayedInteraction.getExecuteTimeMillis();
        this.delayedInteraction = delayedInteraction;
    }

    public long getTimeToActivate() {
        return timeToActivate;
    }

    public Timer.Task getDelayedInteraction() {
        return delayedInteraction;
    }

    public int getSize() {
        return size;
    }

    public String desc() {
        return LocaleBundleHolder.get("anomaly.desc.header") +
                '\n' +
                LocaleBundleHolder.get("anomaly.desc.name", getAnomaly().getTitle()) +
                '\n' +
                LocaleBundleHolder.get("anomaly.desc.size", size * 2);
    }

}