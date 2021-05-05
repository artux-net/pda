package net.artux.pda.ui.fragments.quest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chapter {

    @SerializedName("stages")
    @Expose
    private final List<Stage> stages = null;
    private final List<Sound> music = null;

    public List<Stage> getStages() {
        return stages;
    }

    public List<Sound> getMusics() {
        return music;
    }

}
