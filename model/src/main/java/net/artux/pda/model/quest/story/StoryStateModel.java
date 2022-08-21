package net.artux.pda.model.quest.story;

public class StoryStateModel {

    private boolean current;
    private boolean over;

    private int storyId;
    private int chapterId;
    private int  stageId;

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public boolean isCurrent() {
        return current;
    }

    public boolean isOver() {
        return over;
    }

    public int getStoryId() {
        return storyId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getStageId() {
        return stageId;
    }
}
