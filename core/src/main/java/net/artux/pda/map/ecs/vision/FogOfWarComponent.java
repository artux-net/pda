package net.artux.pda.map.ecs.vision;

import com.badlogic.ashley.core.Component;

public class FogOfWarComponent implements Component {

    public float visionCoefficient;
    public boolean isCameraVisible;

    public void setVisionCoefficient(float visionCoefficient) {
        this.visionCoefficient = visionCoefficient;
    }

    public float getVisionCoefficient() {
        return visionCoefficient;
    }

    public boolean isVisible() {
        return visionCoefficient > 0.3f;
    }

    public boolean isCameraVisible() {
        return isCameraVisible;
    }

    public void setCameraVisible(boolean cameraVisible) {
        isCameraVisible = cameraVisible;
    }
}
