package net.artux.pda.ui.activities;

import static net.artux.pda.ui.util.AndroidHelper.hideNavBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
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
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.rewarded.RewardedAd;

import net.artux.pda.R;
import net.artux.pda.gdx.CoreFragment;
import net.artux.pda.gdx.InterstitialAdListener;
import net.artux.pda.gdx.VideoAdListener;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.Stage;
import net.artux.pda.model.quest.StageModel;
import net.artux.pda.repositories.QuestSoundManager;
import net.artux.pda.ui.fragments.quest.SellerFragment;
import net.artux.pda.ui.fragments.quest.StageRootFragment;
import net.artux.pda.ui.viewmodels.CommandViewModel;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.viewmodels.SellerViewModel;
import net.artux.pda.ui.viewmodels.UserViewModel;
import net.artux.pda.ui.viewmodels.event.ScreenDestination;
import net.artux.pda.utils.AdType;
import net.artux.pda.utils.URLHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;


@AndroidEntryPoint
public class QuestActivity extends FragmentActivity implements AndroidFragmentApplication.Callbacks {

    @Inject
    protected Gson gson;
    @Inject
    protected QuestSoundManager soundManager;
    protected QuestViewModel questViewModel;
    protected CommandViewModel commandViewModel;

    private ImageSwitcher switcher;
    private CoreFragment coreFragment;
    private StageRootFragment stageRootFragment;

    private String currentBackground = "";
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        stageRootFragment = new StageRootFragment();
        coreFragment = new CoreFragment();

        ViewModelProvider provider = new ViewModelProvider(this);
        questViewModel = provider.get(QuestViewModel.class);
        commandViewModel = provider.get(CommandViewModel.class);

        questViewModel.getChapter().observe(this, chapter -> {
            if (chapter == null)
                return;

            //preload images
            for (Stage stage : chapter.getStages().values()) {
                if (stage.getBackground() != null && !stage.getBackground().isEmpty()) {
                    String background_url = URLHelper.getResourceURL(stage.getBackground());

                    Glide.with(QuestActivity.this)
                            .downloadOnly()
                            .load(background_url)
                            .submit();
                }
            }
        });

        questViewModel.getStage().observe(this, this::setStage);
        questViewModel.getMap().observe(this, map -> startMap(provider, map));
        questViewModel.getLoadingState().observe(this, flag ->
                findViewById(R.id.loadingProgressBar).setVisibility(flag ? View.VISIBLE : View.GONE));

        questViewModel.getStatus().observe(this, statusModel -> {
            Toast.makeText(this, "Ошибка квеста: " + statusModel.getDescription(), Toast.LENGTH_LONG).show();
            if (!statusModel.getSuccess())
                Timber.e(statusModel.getDescription());
        });

        questViewModel.getNotification().observe(this, notificationModel -> {
            if (notificationModel == null)
                return;

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.PDANotificationStyle);
            switch (notificationModel.getType()) {
                case ALERT:
                    builder.setIcon(R.drawable.ic_alert);
                    break;
                case MESSAGE:
                    builder.setIcon(R.drawable.ic_message);
                    break;
            }
            builder.setTitle(notificationModel.getTitle());
            builder.setMessage(notificationModel.getMessage());
            AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            window.setGravity(Gravity.START);
            dialog.show();
        });


        commandViewModel.getSellerEvent().observe(this, data -> {
            int sellerId = data.getPayload();
            SellerFragment sellerFragment = SellerFragment.newInstance(sellerId);

            FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
            mFragmentTransaction
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .add(R.id.containerView, sellerFragment, "seller")
                    .addToBackStack("seller")
                    .commit();
            Timber.d("Start seller activity - %s", sellerId);
        });

        commandViewModel.getExitEvent().observe(this, data -> {
            if (data.getDestination() == ScreenDestination.NONE)
                return;
            Intent intent = new Intent(QuestActivity.this, MainActivity.class);
            intent.putExtra("section", data.getDestination());
            startActivity(intent);
            finish();
        });

        commandViewModel.getAdEvent().observe(this, data -> {
            if (data == AdType.VIDEO) {
                RewardedAd rewardedAd = new RewardedAd(this);
                rewardedAd.setAdUnitId(getString(R.string.quest_ads_video_id));

                final AdRequest adRequest = new AdRequest.Builder().build();
                rewardedAd.setRewardedAdEventListener(new VideoAdListener(commandViewModel, rewardedAd));
                rewardedAd.loadAd(adRequest);
            } else {
                InterstitialAd interstitialAd = new InterstitialAd(this);
                interstitialAd.setAdUnitId(getString(R.string.quest_ads_usual_id));

                final AdRequest adRequest = new AdRequest.Builder().build();
                interstitialAd.setInterstitialAdEventListener(
                        new InterstitialAdListener(commandViewModel, interstitialAd));
                interstitialAd.loadAd(adRequest);
            }
        });

        questViewModel.getBackground().observe(this, this::setBackground);

        switcher = findViewById(R.id.switcher);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        hideNavBar(getWindow());

        Timber.i("QuestActivity created");
        startLoading();
    }

    private boolean isMapActive() {
        return currentFragment == coreFragment;
    }

    private void setStage(StageModel stageModel) {
        soundManager.resume();
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (coreFragment.isAdded()) {
            mFragmentTransaction.hide(coreFragment);
            mFragmentTransaction.setMaxLifecycle(coreFragment, Lifecycle.State.STARTED);
        }

        if (!stageRootFragment.isAdded())
            mFragmentTransaction.add(R.id.containerView, stageRootFragment, "root");

        mFragmentTransaction
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .show(stageRootFragment)
                .commitNow();
        stageRootFragment.setStage(stageModel);

        currentFragment = stageRootFragment;
    }

    private void startMap(ViewModelProvider provider, GameMap map) {
        soundManager.pause();
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putSerializable("map", map);
        args.putSerializable("data", questViewModel.getStoryData().getValue());
        args.putSerializable("user", provider.get(UserViewModel.class).getFromCache());
        args.putSerializable("story", provider.get(QuestViewModel.class).getCurrentStory());
        args.putSerializable("items", provider.get(SellerViewModel.class).getItems());
        args.putBoolean("updated", true);

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
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.containerView, coreFragment)
                .commitNow();

        currentFragment = coreFragment;
    }

    private void startLoading() {
        int[] keys = getIntent().getIntArrayExtra("keys");
        // keys for loading specific stage
        if (keys == null) {
            int storyId = getIntent().getIntExtra("storyId", -1);
            if (storyId < 0) {
                // если нет номера истории в намерении
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("section", ScreenDestination.STORIES);
                startActivity(intent);
                finish();
                Timber.i("Story ID not specified, closing QuestActivity");
                return;
            }
            boolean sync = !getIntent().getBooleanExtra("current", false);
            int chapter = getIntent().getIntExtra("chapterId", 1);
            int stage = getIntent().getIntExtra("stageId", 0);

            // загрузка последней стадии или намеренной
            questViewModel.beginWithStage(storyId, chapter, stage, sync);
            Timber.i("Story started from args %d, %d, %d", storyId, chapter, stage);
        } else {
            questViewModel.beginWithStage(keys[0], keys[1], keys[2], true);
            Timber.i("Story started from keys %d, %d, %d", keys[0], keys[1], keys[2]);
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void setBackground(String nextBackground) {
        if (nextBackground == null)
            return;

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
    protected void onPause() {
        super.onPause();
        soundManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.resume();
    }

    @Override
    protected void onDestroy() {
        Timber.i("QuestActivity destroyed");
        soundManager.stop();
        super.onDestroy();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void exit() {

    }
}
