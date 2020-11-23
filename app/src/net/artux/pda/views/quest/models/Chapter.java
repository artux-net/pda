package net.artux.pda.views.quest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chapter {

    @SerializedName("stages")
    @Expose
    private List<Stage> stages = null;
    private List<Sound> music = null;

    public List<Stage> getStages() {
        return stages;
    }

    public List<Sound> getMusics() {
        return music;
    }

}
