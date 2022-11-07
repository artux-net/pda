package net.artux.pda.model.quest;

import lombok.Data;

@Data
public class CheckpointModel {
    private String parameter;
    private String title;
    private Integer chapterId;
    private Integer stageId;
}
