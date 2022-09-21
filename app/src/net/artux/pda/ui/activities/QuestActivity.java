package net.artux.pda.ui.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.databinding.FragmentNotificationBinding;
import net.artux.pda.gdx.MapHelper;
import net.artux.pda.model.Summary;
import net.artux.pda.model.UserMessage;
import net.artux.pda.model.mapper.StageMapper;
import net.artux.pda.model.quest.Chapter;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.model.quest.TransferModel;
import net.artux.pda.model.quest.UserDataCompanion;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.fragments.quest.QuestController;
import net.artux.pda.ui.fragments.quest.SceneQuestController;
import net.artux.pda.ui.fragments.quest.SellerActivity;
import net.artux.pda.ui.fragments.quest.StageFragment;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.viewmodels.SummaryViewModel;
import net.artux.pda.ui.viewmodels.UserViewModel;
import net.artux.pda.utils.MultiExoPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;


@AndroidEntryPoint
public class QuestActivity extends AppCompatActivity implements View.OnClickListener, StageListener {

    private QuestController sceneController;

    private TextView tvTime;
    private ImageSwitcher switcher;

    private String background_url = "";

    private BroadcastReceiver _broadcastReceiver;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private UserViewModel viewModel;
    private QuestViewModel questViewModel;

    private InterstitialAd mInterstitialAd;
    private MultiExoPlayer multiExoPlayer;

    private SummaryViewModel summaryViewModel;
    private Summary summary;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        if (viewModel == null)
            viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        if (questViewModel == null)
            questViewModel = new ViewModelProvider(this).get(QuestViewModel.class);
        if (summaryViewModel == null)
            summaryViewModel = new ViewModelProvider(this).get(SummaryViewModel.class);

        tvTime = findViewById(R.id.sceneTime);
        ImageView musicImage = findViewById(R.id.musicSetup);
        musicImage.setOnClickListener(this);
        findViewById(R.id.closeButton).setOnClickListener(this);
        findViewById(R.id.exitButton).setOnClickListener(this);
        findViewById(R.id.log).setOnClickListener(this);
        switcher = findViewById(R.id.switcher);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        questViewModel.getStoryData().observe(this, new Observer<StoryDataModel>() {
            @Override
            public void onChanged(StoryDataModel memberResult) {
                startLoading(memberResult.getCurrent());
                questViewModel.getStoryData().removeObserver(this);
            }
        });


        questViewModel.getStatus().observe(this, status -> {
            if (!status.isSuccess()) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("status", status);
                startActivity(intent);
                finish();
            } else
                Toast.makeText(getApplicationContext(), status.getDescription(), Toast.LENGTH_LONG).show();
        });
        summary = summaryViewModel.getCachedSummary(Summary.getCurrentId()).getValue();
        userModel = viewModel.getFromCache();

        if (summary == null)
            summary = new Summary();
        questViewModel.updateData();
    }

    public void sync(Stage stage, int id) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("story:" + sceneController.getStoryId() + ":" + sceneController.getChapterId() + ":" + id);

        HashMap<String, List<String>> actions = stage.getActions();
        if (actions == null)
            actions = new HashMap<>();

        actions.put("set", arrayList);
        questViewModel.applyActions(actions);

        Timber.d("Synced, story: " + sceneController.getStoryId() + ", chapter: " + sceneController.getChapterId() + ", stage: " + id);
    }

    @Override
    public void prepareStage(Stage actualStage) {
        if (actualStage != null && actualStage.getMessage() != null && !actualStage.getMessage().trim().equals("")) {
            FragmentNotificationBinding binding = FragmentNotificationBinding.inflate(getLayoutInflater());
            binding.notificationTitle.setText("Уведомление");
            binding.notificationContent.setText(actualStage.getMessage());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(binding.getRoot());
            AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            dialog.show();
        }

        setBackground(actualStage.getBackgroundUrl());
    }

    private void startLoading(StoryStateModel currentState) {
        int[] keys = getIntent().getIntArrayExtra("keys");
        // keys for loading specific stage
        if (keys == null) {
            int storyId = getIntent().getIntExtra("story", -1);
            if (storyId < 0) {
                // если нет номера истории в намерении
                if (currentState != null)
                    storyId = currentState.getStoryId();
                else {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("section", "stories");
                    startActivity(intent);
                    finish();
                }
            }

            if (currentState != null) {
                int chapter = getIntent().getIntExtra("chapter", currentState.getChapterId());
                int stage = getIntent().getIntExtra("stage", currentState.getStageId());
                // загрузка последней стадии или намеренной
                loadChapter(currentState.getStoryId(), chapter, stage, getIntent().hasExtra("chapter") && getIntent().hasExtra("stage"));
            } else
                loadChapter(storyId, 1, 0, true);// первое открытие

        } else
            loadChapter(keys[0], keys[1], keys[2], true);
    }

    void loadChapter(int storyId, int chapterId, int stageId, boolean sync) {
        questViewModel.getChapter(storyId, chapterId).observe(this, new Observer<Chapter>() {
            @Override
            public void onChanged(Chapter chapter) {
                if (chapter != null) {
                    sceneController = new SceneQuestController(QuestActivity.this, storyId, chapterId, chapter);
                    sceneController.beginWithStage(stageId, sync);

                    questViewModel.getChapter().removeObserver(this);
                    multiExoPlayer = new MultiExoPlayer(QuestActivity.this, chapter.getMusic());
                    for (Stage stage : chapter.getStages()) {
                        if (stage.getBackgroundUrl() != null && !stage.getBackgroundUrl().equals("")) {
                            String background_url;
                            if (!stage.getBackgroundUrl().contains("http")) {
                                background_url = "https://" + BuildConfig.URL + "/" + stage.getBackgroundUrl();
                            } else
                                background_url = stage.getBackgroundUrl();

                            Glide.with(QuestActivity.this)
                                    .downloadOnly()
                                    .load(background_url)
                                    .submit();
                        }
                    }

                } else questViewModel.updateChapter(storyId, chapterId);
            }
        });

    }

    void loadMap(int storyId, int mapId, String pos) {
        MapHelper.prepareAndLoadMap(new ViewModelProvider(this), this, storyId, mapId, pos);
    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.sceneTitle)).setText(title);
    }

    public void setLoading(boolean flag) {
        if (flag)
            findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
    }

    @Override
    public void processTransfer(TransferModel transfer) {
        if (sceneController.getActualStage().getTypeStage() == 7) {
            summary.addMessage(new UserMessage(userModel, transfer.getText()));
        }
    }

    boolean isLoading = false;

    void showAd(int nextId) {
        AdRequest adRequest = new AdRequest.Builder().build();

        if (!isLoading) {
            isLoading = true;
            InterstitialAd.load(this, BuildConfig.QuestAdId, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    Timber.d("onAdLoaded");
                    mInterstitialAd = interstitialAd;

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Timber.d("The ad was dismissed.");
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Timber.d("The ad failed to show.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            Timber.d("The ad was shown.");
                        }
                    });
                    mInterstitialAd.show(QuestActivity.this);
                    //sceneController.loadStage(nextId);
                    isLoading = false;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Timber.d(loadAdError.getMessage());
                    mInterstitialAd = null;
                    isLoading = false;
                    //loadStage(nextId);
                }
            });
        }
    }

    private UserDataCompanion getActualDataCompanion() {
        StoryDataModel storyDataModel = questViewModel.getCachedData();
        return UserDataCompanion.of(userModel, storyDataModel);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void setStage(Stage stage, boolean processSummary) {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        try {
            switch (stage.getTypeStage()) {
                default:
                    StageModel stageModel = StageMapper.INSTANCE.model(stage, getActualDataCompanion(), this);
                    StageFragment stageFragment = StageFragment.createInstance(stageModel, sceneController);
                    mFragmentTransaction.replace(R.id.containerView, stageFragment);
                    setLoading(false);
                    break;
                case 4:
                    if (stage.getData().containsKey("map")) {
                        setTitle("Загрузка карты..");
                        int mapId = Integer.parseInt(stage.getData().get("map"));
                        if (stage.getData().containsKey("pos"))
                            loadMap(sceneController.getStoryId(), mapId, stage.getData().get("pos"));
                    }
                    break;
                case 5:
                    if (stage.getData().containsKey("chapter")) {
                        int chapter = Integer.parseInt(stage.getData().get("chapter"));
                        int stageId = Integer.parseInt(stage.getData().get("stage"));
                        loadChapter(sceneController.getStoryId(), chapter, stageId, true);
                    }
                    break;
                case 6:
                    if (stage.getData().containsKey("seller")) {
                        Intent intent = new Intent(this, SellerActivity.class);
                        intent.putExtra("seller", Integer.parseInt(stage.getData().get("seller")));
                        intent.putExtra("chapter", Integer.parseInt(stage.getData().get("chapter")));
                        intent.putExtra("stage", Integer.parseInt(stage.getData().get("stage")));
                        Timber.d("Start seller activity - %s", stage.getData().get("seller"));
                        startActivity(intent);
                    }
                    break;
            }
        } catch (Exception e) {
            Timber.d("Can not load stage, story: " + sceneController.getStoryId()
                    + " chapter: " + sceneController.getChapterId() + " stage: " + stage.getId());
            Timber.e(e);
            Toast.makeText(this, "Can not load stage, error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (processSummary && stage.getTypeStage() == 7 && stage.getTexts() != null)
            summary.addMessage(new UserMessage(stage.getTitle(), stage.getTexts().get(0).getText(), stage.getBackgroundUrl()));

        mFragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void playSound() {

    }

    public void setBackground(String backgroundUrl) {
        if (backgroundUrl != null)
            if (!background_url.equals(backgroundUrl)) {
                if (!backgroundUrl.contains("http")) {
                    background_url = "https://" + BuildConfig.URL + "/" + backgroundUrl;
                } else background_url = backgroundUrl;

                Glide.with(this)
                        .load(background_url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into((ImageView) switcher.getNextView());
                switcher.showNext();
            }
    }

    boolean isFirst = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.musicSetup:
                /*if(isMuted()){
                    musicImage.setImageDrawable(ResourcesCompat
                            .getDrawable(getResources(), R.drawable.ic_vol_on, getApplicationContext().getTheme()));
                    unmute();
                } else {
                    musicImage.setImageDrawable(ResourcesCompat
                            .getDrawable(getResources(), R.drawable.ic_vol_off, getApplicationContext().getTheme()));
                    mute();
                }*/
                break;
            case R.id.closeButton:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.exitButton:
                HashMap<String, List<String>> actions = new HashMap<>();
                actions.put("reset_current", new ArrayList<>());
                questViewModel.applyActions(actions);
                viewModel.getMember().observe(this, memberResult -> {
                    if (isFirst)
                        isFirst = false;
                    else {
                        Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                        intent.putExtra("section", "stories");
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.id.log:
                if (sceneController != null) {
                    Stage stage = sceneController.getActualStage();

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Story: ").append(sceneController.getStoryId()).append("\n");
                    stringBuilder.append("Chapter: ").append(sceneController.getChapterId()).append("\n");

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(stage);
                    stringBuilder.append(json);

                    Intent intent = new Intent(this, LogActivity.class);
                    intent.putExtra("text", stringBuilder.toString());
                    startActivity(intent);
                } else
                    Toast.makeText(getApplicationContext(), "Глава не загружена!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        tvTime.setText(_sdfWatchTime.format(new Date()));
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction() != null && intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    tvTime.setText(_sdfWatchTime.format(new Date()));
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        release();
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        System.out.println("Destroyed QuestActivity");
        summaryViewModel.putSummary(summary.getTitle(), summary);
        super.onDestroy();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }

    public void unmute() {
        if (multiExoPlayer != null)
            multiExoPlayer.unmute();
    }

    public void mute() {
        if (multiExoPlayer != null)
            multiExoPlayer.mute();
    }

    public boolean isMuted() {
        return multiExoPlayer.isMuted();
    }

    public void release() {
        if (multiExoPlayer != null)
            multiExoPlayer.release();
    }

}
