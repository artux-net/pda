package net.artux.pda.views.quest;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.artux.pda.app.App;
import net.artux.pda.views.quest.models.Stage;
import net.artux.pda.views.quest.models.Text;
import net.artux.pda.views.quest.models.Transfer;
import net.artux.pdalib.Checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SceneFragment extends Fragment implements StageNavigation.View{

    private Stage stage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMessage();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public HashMap<String, String> getData() {
        return stage.getData();
    }

    String getTitle(){
        if (stage.getTitle()!=null)
            return stage.getTitle();
        else
            return "";
    }

    String getBackground(){
        if (stage.getBackgroundUrl()!=null)
            return stage.getBackgroundUrl();
        else return "";

    }

    int[] getMusics(){
        return stage.getMusics();
    }

    void showMessage(){
        if (stage!=null && !stage.getMessage().trim().equals(""))
            Toast.makeText(getContext(), stage.getMessage(), Toast.LENGTH_LONG).show();
    }

    List<Text> getTexts(){
        List<Text> texts = new ArrayList<>();
        for (final Text text : stage.getText())
            if(Checker.check(text.condition, App.getDataManager().getMember())) {
                texts.add(text);
            }
        return texts;
    }

    List<Transfer> getTransfers(){
        List<Transfer> transfers = new ArrayList<>();
        for (final Transfer transfer : stage.getTransfers())
            if(Checker.check(transfer.condition, App.getDataManager().getMember())) {
                transfers.add(transfer);
            }
        return transfers;
    }



}
