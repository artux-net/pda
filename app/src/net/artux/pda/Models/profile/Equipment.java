
package net.artux.pda.Models.profile;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Equipment {

    @SerializedName("armor")
    @Expose
    private Item armor = new Item();
    @SerializedName("type0")
    @Expose
    private Item firstWeapon = new Item();
    @SerializedName("type1")
    @Expose
    private Item secondWeapon = new Item();
    @SerializedName("detector")
    @Expose
    private Item detector = new Item();
    @SerializedName("artifacts")
    @Expose
    private List<Object> artifacts = new ArrayList<>();

    public List<Object> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Object> artifacts) {
        this.artifacts = artifacts;
    }

}
