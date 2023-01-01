package net.artux.pda.model.quest;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MissionModel implements Serializable {
    private String title;
    private String name;
    private List<CheckpointModel> checkpoints;

    public boolean hasParams(String... params) {
        return getCurrentCheckpoint(params) != null;
    }

    public CheckpointModel getCurrentCheckpoint(String... params) {
        for (CheckpointModel checkpointModel : checkpoints) {
            if (checkpointModel.isActual(params))
                return checkpointModel;
        }
        return null;
    }

}
