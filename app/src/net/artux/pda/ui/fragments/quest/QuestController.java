package net.artux.pda.ui.fragments.quest;

import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.ui.fragments.quest.models.Stage;

public interface QuestController {

    void beginWithStage(int stageId);
    void beginWithStage(int stageId, boolean sync);
    void chooseTransfer(TransferModel transfer);
    Stage getActualStage();
    int getStoryId();
    int getChapterId();

}
