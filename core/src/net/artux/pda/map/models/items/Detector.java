package net.artux.pda.map.models.items;

public class Detector extends Wearable {

    private DetectorType detectorType;

    public DetectorType getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(DetectorType detectorType) {
        this.detectorType = detectorType;
    }
}
