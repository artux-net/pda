package net.artux.pda.ui.fragments.quest;

import net.artux.pda.model.quest.ChapterModel;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.ui.activities.StageListener;

import java.io.Serializable;

public class SceneQuestController implements Serializable, QuestController {

    private Stage actualStage;
    private final StageListener listener;
    private final int storyId;
    private final int chapterId;
    private final ChapterModel chapter;

    public SceneQuestController(StageListener listener, int storyId, int chapterId, ChapterModel chapter) {
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
        beginWithStage(stageId, false);
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
        actualStage = chapter.getStage(id);
        if (sync)
            synchronize(actualStage, id);
        listener.prepareStage(actualStage);
        listener.setStage(actualStage, sync);
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
