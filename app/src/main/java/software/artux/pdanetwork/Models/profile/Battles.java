
package software.artux.pdanetwork.Models.profile;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Battles {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("wins")
    @Expose
    private Integer wins;
    @SerializedName("history")
    @Expose
    private List<Object> history = null;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public List<Object> getHistory() {
        return history;
    }

    public void setHistory(List<Object> history) {
        this.history = history;
    }

}
