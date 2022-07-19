package net.artux.pda.ui.activities;

import net.artux.pda.models.quest.TransferModel;
import net.artux.pda.ui.fragments.quest.models.Stage;

public interface StageListener {

    void setTitle(String title);
    void setBackground(String backgroundUrl);
    void setLoading(boolean value);
    void processTransfer(TransferModel transfer);
    void setStage(Stage stage, boolean summary);
    void playSound();
    void sync(Stage stage, int id);
    void prepareStage(Stage actualStage);
}
