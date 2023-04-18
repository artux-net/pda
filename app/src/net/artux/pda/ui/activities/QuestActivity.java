package net.artux.pda.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentNotificationBinding;
import net.artux.pda.gdx.CoreFragment;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StageType;
import net.artux.pda.ui.fragments.quest.SellerFragment;
import net.artux.pda.ui.fragments.quest.StageFragment;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.viewmodels.SellerViewModel;
import net.artux.pda.ui.viewmodels.UserViewModel;
import net.artux.pda.utils.MultiExoPlayer;
import net.artux.pda.utils.URLHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;


@AndroidEntryPoint
public class QuestActivity extends FragmentActivity implements AndroidFragmentApplication.Callbacks {

    private ImageSwitcher switcher;

    @Inject
    protected Gson gson;
    private String currentBackground = "";
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
                    if (stage.getBackground() != null && !stage.getBackground().isEmpty()) {
                        String background_url = URLHelper.getResourceURL(stage.getBackground());

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
                    .commitNow();

            if (stageModel.getType() != StageType.CHAPTER_OVER)
                setTitle(stageModel.getTitle());
            else
                setTitle("");
        });

        questViewModel.getMap().observe(this, map -> {
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
                    .replace(R.id.containerView, coreFragment);

            mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            mFragmentTransaction.commitNow();
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

        switcher = findViewById(R.id.switcher);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        hideNavBar();

        startLoading();
    }

    void hideNavBar() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                });
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
