
package net.artux.pda.Models.profile;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Equipment {

    @SerializedName("armor")
    @Expose
    private Armor armor;
    @SerializedName("type0")
    @Expose
    private Type0 type0;
    @SerializedName("type1")
    @Expose
    private Type1 type1;
    @SerializedName("detector")
    @Expose
    private Detector detector;
    @SerializedName("artifacts")
    @Expose
    private List<Object> artifacts = null;

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public Type0 getType0() {
        return type0;
    }

    public void setType0(Type0 type0) {
        this.type0 = type0;
    }

    public Type1 getType1() {
        return type1;
    }

    public void setType1(Type1 type1) {
        this.type1 = type1;
    }

    public Detector getDetector() {
        return detector;
    }

    public void setDetector(Detector detector) {
        this.detector = detector;
    }

    public List<Object> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Object> artifacts) {
        this.artifacts = artifacts;
    }

}
