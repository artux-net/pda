package net.artux.pda.Views.Quest;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import net.artux.pda.Models.profile.Data;
import net.artux.pda.Models.profile.Item;
import net.artux.pda.R;
import net.artux.pda.Views.Quest.Models.Stage;
import net.artux.pda.Views.Quest.Models.Transfer;
import net.artux.pda.activities.QuestActivity;
import net.artux.pda.app.App;

import java.util.List;

public class QuestFragment extends Fragment implements View.OnClickListener {

    private View mainView;
    private LinearLayout sceneResponses;
    private Stage stage;
    private ColorStateList colorStateList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mainView == null){
            mainView = inflater.inflate(R.layout.quest_type0, container, false);

            loadScene();
        }

        return mainView;
    }

    private void loadScene(){
        TextView mainText = mainView.findViewById(R.id.sceneText);
        sceneResponses = mainView.findViewById(R.id.sceneResponses);


        mainText.setText(stage.getText().get(0).text);
        colorStateList = mainText.getTextColors();
        setSceneResponses();
        ((QuestActivity) getActivity()).setTitle(stage.getTitle());
        if (stage.getMessage()!=null && !stage.getMessage().equals(""))

        if(!stage.getBackgroundUrl().equals(""))
            ((QuestActivity) getActivity()).setBackground(stage.getBackgroundUrl());
        if(stage.getMusicId() instanceof String){
            ((QuestActivity) getActivity()).setMusic(String.valueOf(stage.getMusicId()));
        }
    }

    private void setSceneResponses(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (final Transfer transfer : stage.getTransfers()) {
            if(checkTransfer(transfer)) {
                Button button = new Button(App.getContext());
                button.setLayoutParams(layoutParams);
                button.setPadding(10, 10, 10, 10);
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setBackgroundColor(ContextCompat.getColor(App.getContext(), R.color.black_overlay));
                button.setText(transfer.text);
                button.setTextColor(colorStateList);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((QuestActivity) getActivity()).getSceneController().loadStage(transfer.stage_id);
                    }
                });

                sceneResponses.addView(button);
            }
        }
    }

    private boolean containsItem(List<Item> items, String sid){
        if(isInteger(sid)){
            int id = Integer.parseInt(sid);
            for (Item item : items){
                if (item.getId()==id)
                    return true;
            }
        }

        return false;
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private boolean checkTransfer(Transfer transfer){
        if (transfer.condition!=null){
            if (!transfer.condition.keySet().isEmpty()){
                Data data = App.getDataManager().getMember().getData();
                if(data!=null)
                for(String condition : transfer.condition.keySet()){
                    switch (condition){
                        case "has":
                            for (String has : transfer.condition.get(condition)){
                                if (!data.params.params.contains(has)
                                        && !data.params.values.containsKey(has)
                                        && !containsItem(data.getItems(), has))
                                            return false;
                            }
                            break;
                        case "!has":
                            for (String has : transfer.condition.get(condition)){
                                if (data.params.params.contains(has)
                                        || data.params.values.containsKey(has)
                                        || containsItem(data.getItems(), has))
                                            return false;

                            }
                            break;
                    }
                } else return false;
                return true;
            } else return true;
        } else return true;
    }

    void setStage(Stage stage){
        this.stage = stage;
    }

    @Override
    public void onClick(View v) {

    }
}
