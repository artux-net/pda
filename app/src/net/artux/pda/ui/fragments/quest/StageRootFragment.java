package net.artux.pda.ui.fragments.quest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.GsonBuilder;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentQuestRootBinding;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.repositories.QuestSoundManager;
import net.artux.pda.ui.activities.LogActivity;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.viewmodels.QuestViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StageRootFragment extends Fragment implements View.OnClickListener {

    @Inject
    protected QuestSoundManager soundManager;

    private QuestViewModel questViewModel;
    private FragmentQuestRootBinding rootBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootBinding = FragmentQuestRootBinding.inflate(inflater);
        return rootBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questViewModel = new ViewModelProvider(requireActivity()).get(QuestViewModel.class);
        rootBinding.musicSetup.setOnClickListener(this);
        rootBinding.closeButton.setOnClickListener(this);
        rootBinding.log.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.musicSetup) {
            soundManager.mute();
            if (soundManager.getMuted()) {
                rootBinding.musicSetup.setImageDrawable(ResourcesCompat
                        .getDrawable(getResources(), R.drawable.ic_vol_off, requireContext().getTheme()));
            } else {
                rootBinding.musicSetup.setImageDrawable(ResourcesCompat
                        .getDrawable(getResources(), R.drawable.ic_vol_on, requireContext().getTheme()));
            }
        } else if (id == R.id.closeButton) {
            requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        } else if (id == R.id.log) {
            Stage stage = questViewModel.getCurrentStage();
            StoryDataModel dataModel = questViewModel.getStoryData().getValue();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();

            assert dataModel != null;
            String logStage = "Story: " + questViewModel.getCurrentStoryId() + "\n" +
                    "Chapter: " + questViewModel.getCurrentChapterId() + "\n \n" +
                    "Parameters: " + gsonBuilder.create().toJson(dataModel.getParameters()) + "\n \n" +
                    "Stage: " + gsonBuilder.create().toJson(stage);


            Intent intent = new Intent(requireContext(), LogActivity.class);
            intent.putExtra("text", logStage);
            startActivity(intent);
        }
    }

    public void setStage(StageModel stage) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        StageFragment stageFragment = StageFragment.createInstance(stage);
        transaction
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.stageContainer, stageFragment, "stage")
                .commitNow();


        if (stage.getType() != StageType.CHAPTER_OVER)
            rootBinding.sceneTitle.setText(stage.getTitle());
        else
            rootBinding.sceneTitle.setText("");
    }
}
