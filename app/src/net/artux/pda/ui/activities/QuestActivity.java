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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentNotificationBinding;
import net.artux.pda.gdx.CoreFragment;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.ui.fragments.quest.SellerFragment;
import net.artux.pda.ui.fragments.quest.StageFragment;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.viewmodels.SellerViewModel;
import net.artux.pda.ui.viewmodels.UserViewModel;
import net.artux.pda.utils.MultiExoPlayer;
import net.artux.pda.utils.URLHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;


@AndroidEntryPoint
public class QuestActivity extends FragmentActivity implements View.OnClickListener, AndroidFragmentApplication.Callbacks {

    private TextView tvTime;
    private ImageSwitcher switcher;
    private ImageView musicImage;
    @Inject
    protected Gson gson;

    private String currentBackground = "";

    private BroadcastReceiver timeChangeReceiver;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());
    private MultiExoPlayer multiExoPlayer;


    private CoreFragment coreFragment;

    private QuestViewModel questViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        ViewModelProvider provider = new ViewModelProvider(this);
        questViewModel = provider.get(QuestViewModel.class);

        questViewModel.getChapter().observe(this, chapter -> {
            if (chapter != null) {
                multiExoPlayer = new MultiExoPlayer(QuestActivity.this, chapter.getMusic());
                //preload images
                for (Stage stage : chapter.getStages()) {
                    if (stage.getBackgroundUrl() != null && !stage.getBackgroundUrl().isEmpty()) {
                        String background_url = URLHelper.getResourceURL(stage.getBackgroundUrl());

                        Glide.with(QuestActivity.this)
                                .downloadOnly()
                                .load(background_url)
                                .submit();
                    }
                }
            }
        });

        questViewModel.getStage().observe(this, stageModel -> {
            FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (coreFragment != null && coreFragment.isAdded()) {
                mFragmentTransaction.hide(coreFragment);
                mFragmentTransaction.setMaxLifecycle(coreFragment, Lifecycle.State.STARTED);
            }

            StageFragment stageFragment = StageFragment.createInstance(stageModel);
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof StageFragment) {
                    mFragmentTransaction.remove(fragment);
                }
            }

            mFragmentTransaction
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .add(R.id.containerView, stageFragment, "stage")
                    .addToBackStack("stage")
                    .commit();

            setTitle(stageModel.getTitle());
            findViewById(R.id.navbar).setVisibility(View.VISIBLE);
        });

        questViewModel.getMap().observe(this, map -> {
            findViewById(R.id.navbar).setVisibility(View.GONE);
            FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (coreFragment == null)
                coreFragment = new CoreFragment();
            Bundle args = new Bundle();
            args.putSerializable("map", map);
            args.putSerializable("data", questViewModel.getStoryData().getValue());
            args.putSerializable("user", provider.get(UserViewModel.class).getFromCache());
            args.putSerializable("story", provider.get(QuestViewModel.class).getCurrentStory());
            args.putSerializable("items", provider.get(SellerViewModel.class).getItems());

            coreFragment.setArguments(args);
            if (coreFragment.isAdded()) {
                if (coreFragment.isHidden()) {
                    mFragmentTransaction.setMaxLifecycle(coreFragment, Lifecycle.State.RESUMED);
                    mFragmentTransaction.show(coreFragment);
                } else {
                    coreFragment.onResume();
                }
            }
            mFragmentTransaction
                    .replace(R.id.containerView, coreFragment)
                    .addToBackStack("core");

            mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            mFragmentTransaction.commit();
        });

        questViewModel.getLoadingState().observe(this, flag -> {
            if (flag)
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
            else
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
        });

        questViewModel.getStatus().observe(this, statusModel -> {
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

        questViewModel.getData().observe(this, data -> {
            if (data.containsKey("seller")) {
                SellerFragment sellerFragment = SellerFragment.newInstance(Integer.parseInt(data.get("seller")));

                findViewById(R.id.navbar).setVisibility(View.GONE);
                FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                mFragmentTransaction
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.containerView, sellerFragment, "seller")
                        .addToBackStack("seller")
                        .commit();
                Timber.d("Start seller activity - %s", data.get("seller"));
            } else if (data.containsKey("exit")) {
                Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                intent.putExtra("section", "stories");
                startActivity(intent);
                finish();
            }
        });

        questViewModel.getNotification().observe(this, notificationModel -> {
            if (notificationModel != null) {
                FragmentNotificationBinding binding = FragmentNotificationBinding.inflate(getLayoutInflater());
                switch (notificationModel.getType()) {
                    case ALERT:
                        binding.notificationTitle.setText(R.string.notification_alert);
                        break;
                    case MESSAGE:
                        binding.notificationTitle.setText(R.string.notification_message);
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

        questViewModel.getBackground().observe(this, this::setBackground);

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
            questViewModel.beginWithStage(storyId, chapter, stage, sync);
            Timber.i("Quest started with %d,%d,%d", storyId, chapter, stage);
        } else
            questViewModel.beginWithStage(keys[0], keys[1], keys[2], true);

    }

    public void setTitle(String title) {
        ((TextView) findViewById(R.id.sceneTitle)).setText(title);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void setBackground(String nextBackground) {
        if (!currentBackground.equals(nextBackground)) {
            currentBackground = URLHelper.getResourceURL(nextBackground);

            Glide.with(this)
                    .load(currentBackground)
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

    @Override
    public void exit() {

    }
}
