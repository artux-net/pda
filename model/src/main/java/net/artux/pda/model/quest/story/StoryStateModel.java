package net.artux.pda.model.quest.story;

import lombok.Data;

@Data
public class StoryStateModel {

    private boolean current;
    private boolean over;

    private int storyId;
    private int chapterId;
    private int  stageId;

}
