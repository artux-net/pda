package net.artux.pda.model.quest.story;

import java.io.Serializable;

import lombok.Data;

@Data
public class StoryStateModel implements Serializable {

    private boolean current;
    private boolean over;

    private int storyId;
    private int chapterId;
    private int stageId;

    @Override
    public String toString() {
        return "StoryStateModel{" +
                "current=" + current +
                ", over=" + over +
                ", storyId=" + storyId +
                ", chapterId=" + chapterId +
                ", stageId=" + stageId +
                '}';
    }
}
