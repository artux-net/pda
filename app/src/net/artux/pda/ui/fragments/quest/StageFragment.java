package net.artux.pda.ui.fragments.quest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.GsonBuilder;

import net.artux.pda.R;
import net.artux.pda.app.PDAApplication;
import net.artux.pda.common.PropertyFields;
import net.artux.pda.databinding.FragmentQuest0Binding;
import net.artux.pda.databinding.FragmentQuest1Binding;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.ui.activities.LogActivity;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.views.TypeWriterTextView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class StageFragment extends Fragment implements View.OnClickListener {

    private LinearLayout sceneResponses;
    private ColorStateList colorStateList;

    private StageModel stage;
    private QuestViewModel questViewModel;

    private FragmentQuest0Binding usualStageBinding;
    private FragmentQuest1Binding chapterOverBinding;

    private TextView tvTime;
    private ImageView musicImage;

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
            chapterOverBinding.sceneTitle.setText(stage.getTitle());
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
        tvTime = view.findViewById(R.id.sceneTime);
        musicImage = view.findViewById(R.id.musicSetup);
        musicImage.setOnClickListener(this);
        view.findViewById(R.id.closeButton).setOnClickListener(this);
        view.findViewById(R.id.exitButton).setOnClickListener(this);
        view.findViewById(R.id.log).setOnClickListener(this);
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

    private BroadcastReceiver timeChangeReceiver;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    @Override
    public void onStart() {
        super.onStart();
        tvTime.setText(timeFormatter.format(Instant.now()));
        timeChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction() != null && intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    tvTime.setText(timeFormatter.format(Instant.now()));
            }
        };

        requireActivity().registerReceiver(timeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        //release();
        super.onStop();
        if (timeChangeReceiver != null)
            requireActivity().unregisterReceiver(timeChangeReceiver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.musicSetup) {
            /*if (isMuted()) {
                musicImage.setImageDrawable(ResourcesCompat
                        .getDrawable(getResources(), R.drawable.ic_vol_on, requireContext().getTheme()));
                unmute();
            } else {
                musicImage.setImageDrawable(ResourcesCompat
                        .getDrawable(getResources(), R.drawable.ic_vol_off, requireContext().getTheme()));
                mute();
            }*/
        } else if (id == R.id.closeButton) {
            requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        } else if (id == R.id.exitButton)
            questViewModel.exitStory();
        else if (id == R.id.log) {
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
}
