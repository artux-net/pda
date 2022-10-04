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
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.R;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.ui.viewmodels.StoryViewModel;
import net.artux.pda.ui.views.TypeWriterTextView;

import java.util.List;

public class StageFragment extends Fragment {

    private LinearLayout sceneResponses;
    private ColorStateList colorStateList;

    private StageModel stage;
    private StoryViewModel storyViewModel;

    public static StageFragment createInstance(StageModel stage) {
        StageFragment stageFragment = new StageFragment();
        stageFragment.setStage(stage);
        return stageFragment;
    }

    public void setStage(StageModel stage) {
        this.stage = stage;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (stage.getType() == StageType.CHAPTER_OVER)
            return inflater.inflate(R.layout.fragment_quest1, container, false);
        else
            return inflater.inflate(R.layout.fragment_quest0, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storyViewModel = new ViewModelProvider(requireActivity()).get(StoryViewModel.class);

        TypeWriterTextView mainText = view.findViewById(R.id.sceneText);

        colorStateList = mainText.getTextColors();
        sceneResponses = view.findViewById(R.id.sceneResponses);

        mainText.setmText(stage.getContent());
        mainText.setListener(() -> {
            setSceneResponses(stage.getTransfers());
        });
        if (stage.getType() != StageType.DIALOG)
            mainText.setEffect(false);

        if (stage.getType() == StageType.CHAPTER_OVER) {
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

                button.setOnClickListener(v -> storyViewModel.chooseTransfer((transfers.get(0))));
            }
        } else {
            mainText.setOnClickListener(v -> mainText.setEffect(false));
            view.findViewById(R.id.scrollScene).setOnClickListener(v -> {
                mainText.setEffect(false);
            });
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
                        storyViewModel.chooseTransfer(transfer));
            }
            sceneResponses.addView(button);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

}
