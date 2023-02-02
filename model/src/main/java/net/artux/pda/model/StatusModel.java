package net.artux.pda.model;

import net.artux.pda.model.quest.story.StoryDataModel;

import java.io.Serializable;

import lombok.Data;

@Data
public class StatusModel implements Serializable {

    private boolean success;
    private String description;
    private StoryDataModel storyDataModel;

    public StatusModel() {
    }

    public StatusModel(String message) {
        success = true;
        description = message;
    }

    public StatusModel(Throwable throwable) {
        success = false;
        description = throwable.getMessage();
    }
}