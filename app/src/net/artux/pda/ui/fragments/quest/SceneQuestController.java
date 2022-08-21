package net.artux.pda.ui.fragments.quest;

import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.ui.activities.StageListener;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pda.ui.fragments.quest.models.Stage;

import java.io.Serializable;
import java.util.Iterator;

public class SceneQuestController implements Serializable, QuestController {

    private Stage actualStage;
    private final StageListener listener;
    private final int storyId;
    private final int chapterId;
    private final Chapter chapter;

    public SceneQuestController(StageListener listener, int storyId, int chapterId, Chapter chapter) {
        this.listener = listener;
        this.storyId = storyId;
        this.chapterId = chapterId;
        this.chapter = chapter;
        listener.setLoading(true);
    }

    public void beginWithStage(int stageId, boolean sync) {
        listener.setTitle("Глава " + chapterId);
        loadStage(stageId, sync);
    }

    public void beginWithStage(int stageId) {
        listener.setTitle("Глава " + chapterId);
        loadStage(stageId, false);
    }

    @Override
    public void chooseTransfer(TransferModel transfer) {
        listener.processTransfer(transfer);
        loadStage(transfer.getStageId());
    }

    private void synchronize(Stage stage, int id) {
        listener.sync(stage, id);
    }

    void loadStage(int id) {
        loadStage(id, true);
    }

    void loadStage(int id, boolean sync) {
        actualStage = getStage(id);
        if (sync)
            synchronize(actualStage, id);
        listener.prepareStage(actualStage);
        listener.setStage(actualStage, sync);
    }

    private Stage getStage(int id) {
        Iterator<Stage> iterator = chapter.getStages().iterator();
        Stage stage = null;
        while (iterator.hasNext()) {
            stage = iterator.next();
            if (stage.getId() == id) break;
        }
        return stage;
    }

    public Stage getActualStage() {
        return actualStage;
    }

    public int getStoryId() {
        return storyId;
    }

    public int getChapterId() {
        return chapterId;
    }

}
