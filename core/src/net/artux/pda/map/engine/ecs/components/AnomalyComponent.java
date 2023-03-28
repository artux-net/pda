package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.engine.ecs.entities.model.Anomaly;

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
        return "Объект: " +
                "Аномалия" +
                '\n' +
                "Наименование: " +
                getAnomaly().getName() +
                '\n' +
                "Размер: " +
                size * 2;
    }

}