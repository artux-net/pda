package net.artux.pda.model.quest;

import java.io.Serializable;

import lombok.Data;

@Data
public class CheckpointModel implements Serializable {
    private String parameter;
    private String title;
    private Integer chapterId;
    private Integer stageId;
}
