
package software.artux.pdanetwork.Models.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import software.artux.pdanetwork.Models.profile.Achievements;

public class Stats {

    @SerializedName("achievements")
    @Expose
    private Achievements achievements;
    @SerializedName("battles")
    @Expose
    private Battles battles;

    public Achievements getAchievements() {
        return achievements;
    }

    public void setAchievements(Achievements achievements) {
        this.achievements = achievements;
    }

    public Battles getBattles() {
        return battles;
    }

    public void setBattles(Battles battles) {
        this.battles = battles;
    }

}
