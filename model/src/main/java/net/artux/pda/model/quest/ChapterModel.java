package net.artux.pda.model.quest;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ChapterModel implements Serializable {

    private List<Stage> stages;
    private List<Sound> music;

    public Stage getStage(int id) {
        for (Stage stage : getStages()) {
            if (stage.getId() == id)
                return stage;
        }
        return null;
    }

}
