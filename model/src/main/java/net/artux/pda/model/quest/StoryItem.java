package net.artux.pda.model.quest;

import lombok.Data;

@Data
public class StoryItem {

    private int id;
    private String title;
    private String iconUrl;
    private String desc;
    private boolean complete;

}
