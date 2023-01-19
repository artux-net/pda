package net.artux.pda.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;

import net.artux.pda.R;
import net.artux.pda.databinding.ActivityMainBinding;
import net.artux.pda.model.StatusModel;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.activities.hierarhy.MainContract;
import net.artux.pda.ui.activities.hierarhy.MainPresenter;
import net.artux.pda.ui.fragments.chat.DialogsFragment;
import net.artux.pda.ui.fragments.news.NewsFragment;
import net.artux.pda.ui.fragments.notes.NoteFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.stories.StoriesFragment;
import net.artux.pda.ui.viewmodels.UserViewModel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends FragmentActivity implements MainContract.View, View.OnClickListener {

    private ActivityMainBinding binding;
    private MainPresenter presenter;
    private BroadcastReceiver timeChangeReceiver;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yy")
            .withZone(ZoneId.systemDefault());
    private UserViewModel viewModel;

    public MainPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (viewModel == null)
            viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Appodeal.initialize(this, getString(R.string.appodeal_key), Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO, new ApdInitializationCallback() {
            @Override
            public void onInitializationFinished(List<ApdInitializationError> list) {
                if (list != null && list.size() > 0)
                    for (ApdInitializationError err : list)
                        Timber.tag("Add Error").e(err);
            }
        });

        presenter = new MainPresenter();
        presenter.attachView(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("section"))
                switch (intent.getStringExtra("section")) {
                    case "stories":
                        presenter.addFragment(new StoriesFragment(), false);
                        break;
                    case "profileModel":
                        presenter.addFragment(new UserProfileFragment(), false);
                        break;
                    default:
                        presenter.addFragment(new NewsFragment(), false);
                        break;
                }
            else
                presenter.addFragment(new NewsFragment(), false);
            if (intent.hasExtra("status")) {
                StatusModel statusModel = (StatusModel) intent.getSerializableExtra("status");
                Snackbar.make(binding.getRoot(), statusModel.getDescription(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", view -> {
                        })
                        .show();
                if (!statusModel.isSuccess())
                    Timber.e(statusModel.getDescription());
            }
        }

        viewModel.getMember().observe(this, memberResult -> {
            presenter.setAdditionalTitle("PDA #" + memberResult.getPdaId());
        });

        setListeners();
        Timber.i("Main activity created.");
        //startService(new Intent(this, NotificationService.class));
    }

    @Override
    public void setTitle(String title) {
        binding.titleView.setText(title);
    }

    @Override
    public void setAdditionalTitle(String title) {
        binding.rightTitleView.setText(title);
    }


    void setListeners() {
        binding.newsButton.setOnClickListener(this);
        binding.messagesButton.setOnClickListener(this);
        binding.profileButton.setOnClickListener(this);
        binding.notesButton.setOnClickListener(this);
        binding.mapButton.setOnClickListener(this);
    }

    @Override
    public void setFragment(BaseFragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction
                .replace(R.id.containerView, fragment)
                .commit();
        Timber.d("Set fragment: %s", fragment.getClass().getSimpleName());
    }

    @Override
    public void setAdditionalFragment(AdditionalBaseFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rightContainer, fragment)
                .commit();
        Timber.d("Set second fragment: %s", fragment.getClass().getSimpleName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BaseFragment fragment = (BaseFragment) getVisibleFragment();
        if (fragment != null) {
            presenter.backPressed(fragment);
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && !(fragment instanceof AdditionalBaseFragment))
                return fragment;
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.timeView.setText(timeFormatter.format(Instant.now()));
        timeChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    binding.timeView.setText(timeFormatter.format(Instant.now()));
            }
        };

        registerReceiver(timeChangeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        setLoadingState(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timeChangeReceiver != null)
            unregisterReceiver(timeChangeReceiver);
    }

    @Override
    public void setLoadingState(boolean loadingState) {
        runOnUiThread(() -> Glide.with(getApplicationContext())
                .asGif()
                .load(Uri.parse("file:///android_asset/loadCube.gif"))
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                            if (loadingState) resource.setLoopCount(1);
                        }
                        return false;
                    }
                })
                .into((ImageView) findViewById(R.id.loadingCube)));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.newsButton) {
            presenter.addFragment(new NewsFragment(), true);
        } else if (id == R.id.messagesButton) {
            presenter.addFragment(new DialogsFragment(), true);
        } else if (id == R.id.profileButton) {
            presenter.addFragment(new UserProfileFragment(), true);
        } else if (id == R.id.notesButton) {
            presenter.addFragment(new NoteFragment(), true);
        } else if (id == R.id.mapButton) {
            presenter.addFragment(new StoriesFragment(), true);
        }
    }

}

