
package net.artux.pda.map.models;

public class Story {

    private Integer storyId;
    private Integer lastChapter;
    private Integer lastStage;

    public Story() {
    }

    public Story(String[] values) {
        this.storyId = Integer.parseInt(values[0]);
        this.lastChapter = Integer.parseInt(values[1]);
        this.lastStage = Integer.parseInt(values[2]);
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }

    public Integer getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(Integer lastChapter) {
        this.lastChapter = lastChapter;
    }

    public Integer getLastStage() {
        return lastStage;
    }

    public void setLastStage(Integer lastStage) {
        this.lastStage = lastStage;
    }

    @Override
    public String toString() {
        return "Story{" +
                "Id=" + storyId +
                ", lastChapter=" + lastChapter +
                ", lastStage=" + lastStage +
                '}';
    }
}
