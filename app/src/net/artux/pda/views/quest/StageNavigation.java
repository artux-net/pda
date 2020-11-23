package net.artux.pda.views.quest;

import net.artux.pda.views.quest.models.Stage;

import java.util.HashMap;

public interface StageNavigation {

    interface View{
        void setStage(Stage stage);
        HashMap<String, String> getData();
    }

}
