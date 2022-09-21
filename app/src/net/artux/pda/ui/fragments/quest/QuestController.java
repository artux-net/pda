package net.artux.pda.ui.fragments.quest;

import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.TransferModel;

public interface QuestController {

    void beginWithStage(int stageId);
    void beginWithStage(int stageId, boolean sync);
    void chooseTransfer(TransferModel transfer);
    Stage getActualStage();
    int getStoryId();
    int getChapterId();

}
