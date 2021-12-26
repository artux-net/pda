package net.artux.pda.ui.fragments.quest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Chapter {

    @SerializedName("stages")
    @Expose
    private final List<Stage> stages;
    private final List<Sound> music;

    public Chapter(List<Stage> stages, List<Sound> music) {
        this.stages = stages;
        this.music = music;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public List<Sound> getMusics() {
        return music;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(stages, chapter.stages) && Objects.equals(music, chapter.music);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stages, music);
    }
}
