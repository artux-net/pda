
package net.artux.pda.Models.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;


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
    @SerializedName("temp")
    @Expose
    private HashMap<String, String> temp = new HashMap<>();
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

    public HashMap<String, String> getTemp() {
        return temp;
    }

}
