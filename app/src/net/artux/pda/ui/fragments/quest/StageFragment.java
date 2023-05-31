package net.artux.pda.ui.fragments.quest;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.R;
import net.artux.pda.app.PDAApplication;
import net.artux.pda.common.PropertyFields;
import net.artux.pda.databinding.FragmentQuest0Binding;
import net.artux.pda.databinding.FragmentQuest1Binding;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.views.TypeWriterTextView;

import java.util.List;
import java.util.Properties;

public class StageFragment extends Fragment {

    private LinearLayout sceneResponses;
    private ColorStateList colorStateList;

    private StageModel stage;
    private QuestViewModel questViewModel;

    private FragmentQuest0Binding usualStageBinding;
    private FragmentQuest1Binding chapterOverBinding;


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
        if (stage.getType() == StageType.CHAPTER_OVER) {
            chapterOverBinding = FragmentQuest1Binding.inflate(inflater);
            return chapterOverBinding.getRoot();
        } else {
            usualStageBinding = FragmentQuest0Binding.inflate(inflater);
            return usualStageBinding.getRoot();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        questViewModel = new ViewModelProvider(requireActivity()).get(QuestViewModel.class);
        if (usualStageBinding != null) {
            TypeWriterTextView mainText = usualStageBinding.sceneText;
            sceneResponses = usualStageBinding.sceneResponses;
            colorStateList = mainText.getTextColors();

            mainText.setmText(stage.getContent());
            mainText.setListener(() -> setSceneResponses(stage.getTransfers()));

            Properties properties = ((PDAApplication) getActivity().getApplication())
                    .getProperties();
            if (properties.getProperty(PropertyFields.TESTER_MODE, "false").equals("true")) {
                mainText.setEffect(false);
            }

            usualStageBinding.scrollScene.setOnClickListener(v -> mainText.setEffect(false));
        } else {
            chapterOverBinding.sceneTitle1.setText(stage.getTitle());
            chapterOverBinding.sceneText.setmText(stage.getContent());

            List<TransferModel> transfers = stage.getTransfers();

            Button button = chapterOverBinding.okButton;
            String firstText = transfers.get(0).getText();
            if (firstText != null && !firstText.equals(""))
                button.setText(firstText);
            else
                button.setText(requireActivity().getString(R.string.okay));

            button.setOnClickListener(v -> questViewModel.chooseTransfer((transfers.get(0))));
        }
    }

    private void setSceneResponses(List<TransferModel> transferModels) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (TransferModel transfer : transferModels) {
            Button button = new Button(requireContext());
            button.setLayoutParams(layoutParams);
            button.setPadding(10, 10, 10, 10);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setText(transfer.getText());
            button.setAllCaps(false);
            button.setTextColor(colorStateList);
            if (getActivity() != null) {
                button.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black_overlay));
                button.setOnClickListener(v ->
                        questViewModel.chooseTransfer(transfer));
            }
            sceneResponses.addView(button);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

}
