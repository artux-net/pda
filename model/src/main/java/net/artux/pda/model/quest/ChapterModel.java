package net.artux.pda.model.quest;

import java.util.Iterator;
import java.util.List;

import lombok.Data;

@Data
public class ChapterModel {

    private List<Stage> stages;
    private List<Sound> music;

    public Stage getStage(int id) {
        Iterator<Stage> iterator = getStages().iterator();
        Stage stage = null;
        while (iterator.hasNext()) {
            stage = iterator.next();
            if (stage.getId() == id) break;
        }
        return stage;
    }

}
