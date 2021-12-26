package net.artux.pda.ui.fragments.quest;

import net.artux.pda.ui.fragments.quest.models.Stage;

import java.util.HashMap;

public interface StageNavigation {

    interface View{
        void setStage(Stage stage);
        void setController(QuestController controller);
        HashMap<String, String> getData();
    }

}
