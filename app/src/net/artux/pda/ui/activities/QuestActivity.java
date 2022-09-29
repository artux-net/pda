package net.artux.pda.ui.activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.databinding.FragmentNotificationBinding;
import net.artux.pda.gdx.MapEngine;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.ui.fragments.quest.SellerActivity;
import net.artux.pda.ui.fragments.quest.StageFragment;
import net.artux.pda.ui.viewmodels.StoryViewModel;
import net.artux.pda.ui.viewmodels.UserViewModel;
import net.artux.pda.utils.MultiExoPlayer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;


@AndroidEntryPoint
public class QuestActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTime;
    private ImageSwitcher switcher;
    private ImageView musicImage;
    @Inject
    protected Gson gson;

    private String background_url = "";

    private BroadcastReceiver timeChangeReceiver;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());
    private MultiExoPlayer multiExoPlayer;

    private StoryViewModel storyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        ViewModelProvider provider = new ViewModelProvider(this);
        storyViewModel = provider.get(StoryViewModel.class);

        storyViewModel.getChapter().observe(this, chapter -> {
            if (chapter != null) {
                multiExoPlayer = new MultiExoPlayer(QuestActivity.this, chapter.getMusic());
                //preload images
                for (Stage stage : chapter.getStages()) {
                    if (stage.getBackgroundUrl() != null && !stage.getBackgroundUrl().equals("")) {
                        String background_url;
                        if (!stage.getBackgroundUrl().contains("http")) {
                            //todo remote url
                            background_url = "https://" + BuildConfig.URL + "/" + stage.getBackgroundUrl();
                        } else
                            background_url = stage.getBackgroundUrl();

                        Glide.with(QuestActivity.this)
                                .downloadOnly()
                                .load(background_url)
                                .submit();
                    }
                }
            }
        });

        storyViewModel.getStage().observe(this, stageModel -> {
            setTitle(stageModel.getTitle());
            FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            StageFragment stageFragment = StageFragment.createInstance(stageModel);
            mFragmentTransaction.replace(R.id.containerView, stageFragment);
            mFragmentTransaction.commitAllowingStateLoss();
        });

        storyViewModel.getMap().observe(this, map -> {
            Intent intent = new Intent(QuestActivity.this, MapEngine.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("map", map);
            intent.putExtra("data", storyViewModel.getStoryData().getValue());
            intent.putExtra("user", provider.get(UserViewModel.class).getFromCache());
            QuestActivity.this.startActivity(intent);
            QuestActivity.this.finish();
        });

        storyViewModel.getLoadingState().observe(this, flag -> {
            if (flag)
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
            else
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
        });

        storyViewModel.getStatus().observe(this, statusModel -> {
            if (!statusModel.isSuccess()) {
                Timber.e(statusModel.getDescription());
                Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                intent.putExtra("status", statusModel);
                startActivity(intent);
            } else
                Toast
                        .makeText(QuestActivity.this,
                                "Can not load stage, error: " + statusModel.getDescription(),
                                Toast.LENGTH_LONG)
                        .show();
        });

        storyViewModel.getData().observe(this, data -> {
            if (data.containsKey("seller")) {
                Intent intent = new Intent(QuestActivity.this, SellerActivity.class);
                intent.putExtra("seller", Integer.parseInt(data.get("seller")));
                intent.putExtra("chapter", Integer.parseInt(data.get("chapter")));
                intent.putExtra("stage", Integer.parseInt(data.get("stage")));
                Timber.d("Start seller activity - %s", data.get("seller"));
                startActivity(intent);
            } else if (data.containsKey("exit")) {
                Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                intent.putExtra("section", "stories");
                startActivity(intent);
                finish();
            }
        });

        storyViewModel.getNotification().observe(this, notificationModel -> {
            if (notificationModel != null) {
                FragmentNotificationBinding binding = FragmentNotificationBinding.inflate(getLayoutInflater());
                switch (notificationModel.getType()) {
                    case ALERT:
                        binding.notificationTitle.setText("Уведомление");
                        break;
                    case MESSAGE:
                        binding.notificationTitle.setText("Сообщение");//todo locale
                        break;
                }
                if (notificationModel.getTitle() != null)
                    binding.notificationTitle.setText(notificationModel.getTitle());

                binding.notificationContent.setText(notificationModel.getMessage());

                AlertDialog.Builder builder = new AlertDialog.Builder(QuestActivity.this);
                builder.setView(binding.getRoot());
                AlertDialog dialog = builder.create();
                Window window = dialog.getWindow();
                window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                dialog.show();
            }
        });

        storyViewModel.getBackground().observe(this, this::setBackground);

        tvTime = findViewById(R.id.sceneTime);
        musicImage = findViewById(R.id.musicSetup);
        musicImage.setOnClickListener(this);
        findViewById(R.id.closeButton).setOnClickListener(this);
        findViewById(R.id.exitButton).setOnClickListener(this);
        findViewById(R.id.log).setOnClickListener(this);
        switcher = findViewById(R.id.switcher);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        startLoading();
    }

    private void startLoading() {
        int[] keys = getIntent().getIntArrayExtra("keys");
        // keys for loading specific stage
        if (keys == null) {
            int storyId = getIntent().getIntExtra("storyId", -1);
            if (storyId < 0) {
                // если нет номера истории в намерении
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("section", "stories");
                startActivity(intent);
                finish();
            }
            boolean sync = !getIntent().getBooleanExtra("current", false);
            int chapter = getIntent().getIntExtra("chapterId", 1);
            int stage = getIntent().getIntExtra("stageId", 0);

            // загрузка последней стадии или намеренной
            storyViewModel.beginWithStage(storyId, chapter, stage, sync);
        } else
            storyViewModel.beginWithStage(keys[0], keys[1], keys[2], true);
    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.sceneTitle)).setText(title);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void setBackground(String backgroundUrl) {
        if (backgroundUrl != null)
            if (!background_url.equals(backgroundUrl)) {
                if (!backgroundUrl.contains("http")) {
                    //todo remote config
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.musicSetup) {
            if (isMuted()) {
                musicImage.setImageDrawable(ResourcesCompat
                        .getDrawable(getResources(), R.drawable.ic_vol_on, getApplicationContext().getTheme()));
                unmute();
            } else {
                musicImage.setImageDrawable(ResourcesCompat
                        .getDrawable(getResources(), R.drawable.ic_vol_off, getApplicationContext().getTheme()));
                mute();
            }
        } else if (id == R.id.closeButton) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else if (id == R.id.exitButton)
            storyViewModel.exitStory();
        else if (id == R.id.log) {
            StageModel stage = storyViewModel.getStage().getValue();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();

            String logStage = "Story: " + storyViewModel.getCurrentStoryId() + "\n" +
                    "Chapter: " + storyViewModel.getCurrentChapterId() + "\n" +
                    gsonBuilder.create().toJson(stage);

            Intent intent = new Intent(this, LogActivity.class);
            intent.putExtra("text", logStage);
            startActivity(intent);
        }
    }

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

        registerReceiver(timeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        release();
        super.onStop();
        if (timeChangeReceiver != null)
            unregisterReceiver(timeChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        System.out.println("Destroyed QuestActivity");
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
