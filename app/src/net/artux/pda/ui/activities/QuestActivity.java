package net.artux.pda.ui.activities;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

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
import net.artux.pda.gdx.MapHelper;
import net.artux.pda.repositories.Result;
import net.artux.pda.ui.fragments.quest.Quest0Scene;
import net.artux.pda.ui.fragments.quest.Quest1Scene;
import net.artux.pda.ui.fragments.quest.QuestController;
import net.artux.pda.ui.fragments.quest.SellerActivity;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pda.ui.fragments.quest.models.Transfer;
import net.artux.pda.utils.MultiExoPlayer;
import net.artux.pda.viewmodels.MemberViewModel;
import net.artux.pda.ui.fragments.quest.SceneQuestController;
import net.artux.pda.ui.fragments.quest.models.Stage;
import net.artux.pda.viewmodels.QuestViewModel;
import net.artux.pda.viewmodels.SummaryViewModel;
import net.artux.pdalib.Member;
import net.artux.pdalib.Summary;
import net.artux.pdalib.UserMessage;
import net.artux.pdalib.profile.Story;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


public class QuestActivity extends AppCompatActivity implements View.OnClickListener, StageListener {

    private QuestController sceneController;

    private TextView tvTime;
    private ImageSwitcher switcher;

    private String background_url = "";

    private BroadcastReceiver _broadcastReceiver;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private MemberViewModel viewModel;
    private QuestViewModel questViewModel;

    private InterstitialAd mInterstitialAd;
    private MultiExoPlayer multiExoPlayer;

    private SummaryViewModel summaryViewModel;
    private Summary summary;
    private Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        if (viewModel == null)
            viewModel = getViewModelFactory(this).create(MemberViewModel.class);
        if (questViewModel == null)
            questViewModel = getViewModelFactory(this).create(QuestViewModel.class);
        if (summaryViewModel == null)
            summaryViewModel = getViewModelFactory(this).create(SummaryViewModel.class);

        tvTime = findViewById(R.id.sceneTime);
        ImageView musicImage = findViewById(R.id.musicSetup);
        musicImage.setOnClickListener(this);
        findViewById(R.id.closeButton).setOnClickListener(this);
        findViewById(R.id.exitButton).setOnClickListener(this);
        findViewById(R.id.log).setOnClickListener(this);
        switcher = findViewById(R.id.switcher);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        viewModel.getMember().observe(this, new Observer<Result<Member>>() {
            @Override
            public void onChanged(Result<Member> memberResult) {
                if (memberResult instanceof Result.Success) {
                    member = ((Result.Success<Member>) memberResult).getData();
                    startLoading(member);
                    viewModel.getMember().removeObserver(this);
                } else viewModel.updateMember();
            }
        });
        summary = summaryViewModel.getCachedSummary(Summary.getCurrentId()).getValue();
        if (summary == null)
            summary = new Summary();
    }

    public void sync(Stage stage, int id) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("story:" + sceneController.getStoryId() + ":" + sceneController.getChapterId() + ":" + id);

        HashMap<String, List<String>> actions = stage.getActions();
        if (actions == null)
            actions = new HashMap<>();

        actions.put("set", arrayList);
        viewModel.syncMember(actions);

        Timber.d("Synced, story: " + sceneController.getStoryId() + ", chapter: " + sceneController.getChapterId() + ", stage: " + id);
    }

    private void startLoading(Member member) {
        int[] keys = getIntent().getIntArrayExtra("keys");
        // keys for loading specific stage
        if (keys == null) {
            HashMap<String, String> temp = member.getData().getTemp();

            int storyId = getIntent().getIntExtra("story", -1);

            if (storyId < 0) {
                // если нет номера истории в намерении
                String currentStory = temp.get("currentStory");
                if (currentStory != null)
                    storyId = Integer.parseInt(currentStory);
                else {
                    Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                    intent.putExtra("section", "stories");
                    startActivity(intent);
                    finish();
                }
            }

            boolean found = false;
            for (Story story : member.getData().getStories()) {
                if (story.getStoryId() == storyId) {
                    found = true;
                    int chapter = getIntent().getIntExtra("chapter", story.getLastChapter());
                    int stage = getIntent().getIntExtra("stage", story.getLastStage());
                    // загрузка последней стадии или намеренной
                    loadChapter(story.getStoryId(), chapter, stage, getIntent().hasExtra("chapter") && getIntent().hasExtra("stage"));
                }
            }
            if (!found)
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
                    multiExoPlayer = new MultiExoPlayer(QuestActivity.this, chapter.getMusics());
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
        MapHelper.prepareAndLoadMap(questViewModel, this, storyId, mapId, pos);
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
    public void processTransfer(Transfer transfer) {
        if (sceneController.getActualStage().getTypeStage() == 7) {
            summary.addMessage(new UserMessage(member, transfer.text));
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
                    Quest0Scene quest0Scene = new Quest0Scene();
                    quest0Scene.setStage(stage);
                    quest0Scene.setController(sceneController);
                    mFragmentTransaction.replace(R.id.containerView, quest0Scene);
                    setLoading(false);
                    break;
                case 1:
                    Quest1Scene quest1Scene = new Quest1Scene();
                    quest1Scene.setStage(stage);
                    quest1Scene.setController(sceneController);
                    mFragmentTransaction.replace(R.id.containerView, quest1Scene);
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

        if (processSummary && stage.getTypeStage() == 7 && stage.getText() != null)
            summary.addMessage(new UserMessage(stage.getTitle(), stage.getText().get(0).text, stage.getBackgroundUrl()));

        mFragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void playSound() {

    }

    public void setBackground(String backgroundUrl) {
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
                HashMap<String, List<String>> action = new HashMap<>();
                action.put("reset_current", new ArrayList<>());
                viewModel.syncMember(action);
                viewModel.getMember().observe(this, memberResult -> {
                    if (isFirst)
                        isFirst = false;
                    else if (memberResult instanceof Result.Success) {
                        Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                        intent.putExtra("section", "stories");
                        startActivity(intent);
                        finish();
                    } else viewModel.syncMember(action);
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
