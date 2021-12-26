package net.artux.pda.ui.fragments.quest;

import net.artux.pda.ui.fragments.quest.models.Stage;
import net.artux.pda.ui.fragments.quest.models.Transfer;

public interface QuestController {

    void beginWithStage(int stageId);
    void chooseTransfer(Transfer transfer);
    Stage getActualStage();
    int getStoryId();
    int getChapterId();

}
