
package software.artux.pdanetwork.Models.profile;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Data {

    @SerializedName("equipment")
    @Expose
    private Equipment equipment;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    @SerializedName("stories")
    @Expose
    private List<Story> stories = null;
    @SerializedName("params")
    @Expose
    public Params params;

    public Equipment getEquipment() {
        return equipment;
    }

    public Stats getStats() {
        return stats;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

}
