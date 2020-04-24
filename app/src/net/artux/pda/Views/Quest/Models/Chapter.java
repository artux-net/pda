package net.artux.pda.Views.Quest.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chapter {

    @SerializedName("stages")
    @Expose
    private List<Stage> stages = null;

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

}
