package net.artux.pda.ui.activities;

import net.artux.pda.ui.fragments.quest.models.Stage;
import net.artux.pda.ui.fragments.quest.models.Transfer;

import java.util.HashMap;
import java.util.List;

public interface StageListener {

    void setTitle(String title);
    void setBackground(String backgroundUrl);
    void setLoading(boolean value);
    void processTransfer(Transfer transfer);
    void setStage(Stage stage, boolean summary);
    void playSound();
    void sync(Stage stage, int id);

}
