package software.artux.pdanetwork.Views.Quest;

import android.app.Fragment;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devilsoftware.pdanetwork.R;

import java.util.Map;

import software.artux.pdanetwork.Views.Quest.Models.Stage;
import software.artux.pdanetwork.Views.Quest.Models.Transfer;
import software.artux.pdanetwork.activities.QuestActivity;
import software.artux.pdanetwork.app.App;

public class QuestFragment extends Fragment implements View.OnClickListener {

    View mainView;
    TextView mainText;
    LinearLayout sceneResponses;
    Stage stage;
    ColorStateList colorStateList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (mainView == null){
            mainView = inflater.inflate(R.layout.quest_type0, container, false);

            loadScene();

        }

        return mainView;
    }

    public void loadScene(){
        mainText = mainView.findViewById(R.id.sceneText);
        sceneResponses = mainView.findViewById(R.id.sceneResponses);


        mainText.setText(stage.getText().get(0).text);
        colorStateList = mainText.getTextColors();
        setSceneResponses();
        ((QuestActivity) getActivity()).setTitle(stage.getTitle());
        if(!stage.getBackgroundUrl().equals(""))
            ((QuestActivity) getActivity()).setBackground(stage.getBackgroundUrl());
        if(stage.getMusicId() instanceof String){
            ((QuestActivity) getActivity()).setMusic(String.valueOf(stage.getMusicId()));
        }
    }

    public void setSceneResponses(){
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

    boolean checkTransfer(Transfer transfer){
        if (transfer.condition!=null){
            if (!transfer.condition.keySet().isEmpty()){
                return false;

            } else return true;
        } else return true;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @Override
    public void onClick(View v) {

    }
}
