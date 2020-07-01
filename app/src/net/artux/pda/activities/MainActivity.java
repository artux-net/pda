package net.artux.pda.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.artux.pda.R;
import net.artux.pda.Views.Additional.AdditionalFragment;
import net.artux.pda.Views.Additional.InfoFragment;
import net.artux.pda.Views.Chat.DialogsFragment;
import net.artux.pda.Views.News.NewsFragment;
import net.artux.pda.Views.Profile.ProfileFragment;
import net.artux.pda.Views.Quest.StoriesFragment;
import net.artux.pda.app.App;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends FragmentActivity implements MainContract.View, View.OnClickListener {

    FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();


    TextView mTitleTextView;
    TextView mAdditionalTitleTextView;
    private TextView tvTime;
    public Fragment mainFragment;

    MainPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitleTextView = findViewById(R.id.titleView);
        mAdditionalTitleTextView = findViewById(R.id.addTitleView);
        tvTime = findViewById(R.id.tvTime);

        presenter = new MainPresenter();
        presenter.attachView(this);

        setFragment(new NewsFragment());
        setAdditionalFragment(new InfoFragment());
        if(App.getDataManager().getMember()!=null){
            setAdditionalTitle("PDA #" + App.getDataManager().getMember().getPdaId());
        }

        setOnClickListeners();
    }



    @Override
    public void setTitle(String title){
        mTitleTextView.setText(title);
    }

    @Override
    public void setAdditionalTitle(String title){
        mAdditionalTitleTextView.setText(title);
    }

    void setOnClickListeners(){
        findViewById(R.id.news).setOnClickListener(this);
        findViewById(R.id.messages).setOnClickListener(this);
        findViewById(R.id.profile).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);
        findViewById(R.id.quest).setOnClickListener(this);
    }

    @Override
    public void setFragment(BaseFragment fragment) {
        fragment.attachPresenter(presenter);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerView,fragment)
                .commit();
    }

    @Override
    public void setAdditionalFragment(AdditionalBaseFragment fragment) {
        fragment.attachPresenter(presenter);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.addСontainerView,fragment)
                .commit();
    }

    @Override
    public void attachPresenter(MainContract.Presenter presenter) {

    }

    BroadcastReceiver _broadcastReceiver;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm dd/MM/yy");

    @Override
    public void onStart() {
        super.onStart();
        tvTime.setText(_sdfWatchTime.format(new Date()));
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    tvTime.setText(_sdfWatchTime.format(new Date()));
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        setLoadingState(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    public void setLoadingState(final boolean loadingState){
        Glide.with(this)
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
                            if(!loadingState) resource.setLoopCount(1);
                        }
                        return false;
                    }
                })
                .into((ImageView) findViewById(R.id.loadingCube));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.news:
                setFragment(new NewsFragment());
                break;
            case R.id.messages:
                setFragment(new DialogsFragment());
                break;
            case R.id.profile:
                setFragment(new ProfileFragment());
                setAdditionalFragment(new AdditionalFragment());
                break;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.quest:
                setFragment(new StoriesFragment());
                break;
        }
    }
}

