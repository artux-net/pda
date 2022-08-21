package net.artux.pda.ui.fragments.quest;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import net.artux.pda.R;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.ui.activities.QuestActivity;

import java.util.List;

public class StageFragment extends Fragment {

    private LinearLayout sceneResponses;
    private ColorStateList colorStateList;

    private StageModel stage;
    private QuestController controller;

    public static StageFragment createInstance(StageModel stage, QuestController questController) {
        StageFragment stageFragment = new StageFragment();
        stageFragment.setStage(stage);
        stageFragment.setController(questController);
        return stageFragment;
    }

    public void setStage(StageModel stage) {
        this.stage = stage;
    }

    public void setController(QuestController controller) {
        this.controller = controller;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (stage.getType() == StageType.USUAL)
            return inflater.inflate(R.layout.fragment_quest0, container, false);
        else
            return inflater.inflate(R.layout.fragment_quest1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView mainText = view.findViewById(R.id.sceneText);
        colorStateList = mainText.getTextColors();
        sceneResponses = view.findViewById(R.id.sceneResponses);

        mainText.setText(stage.getContent());

        if (getActivity() != null) {
            ((QuestActivity) getActivity()).setTitle(stage.getTitle());

        }

        if (stage.getType() != StageType.USUAL) {
            TextView title = view.findViewById(R.id.sceneTitle);
            title.setText(stage.getTitle());

            if (getActivity() != null) {
                List<TransferModel> transfers = stage.getTransfers();

                Button button = view.findViewById(R.id.okButton);
                String firstText = transfers.get(0).getText();
                if (firstText != null && !firstText.equals(""))
                    button.setText(firstText);
                else
                    button.setText(getActivity().getString(R.string.okay));

                button.setOnClickListener(v -> controller.chooseTransfer((transfers.get(0))));
            }
        } else {
            setSceneResponses(stage.getTransfers());
        }
    }

    private void setSceneResponses(List<TransferModel> transferModels) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (TransferModel transfer : transferModels) {
            Button button = new Button(getContext());
            button.setLayoutParams(layoutParams);
            button.setPadding(10, 10, 10, 10);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setText(transfer.getText());
            button.setAllCaps(false);
            button.setTextColor(colorStateList);
            if (getActivity() != null) {
                button.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black_overlay));
                button.setOnClickListener(v ->
                        controller.chooseTransfer(transfer));
            }
            sceneResponses.addView(button);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }
}
