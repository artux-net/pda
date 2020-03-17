package software.artux.pdanetwork.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devilsoftware.pdanetwork.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity{

    FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();


    TextView mTitleTextView;
    TextView mAdditionalTitleTextView;
    private TextView tvTime;
    public Fragment mainFragment;
    Fragment addFragment;

    MainActivityController mainActivityController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitleTextView = findViewById(R.id.titleView);
        mAdditionalTitleTextView = findViewById(R.id.addTitleView);
        tvTime = findViewById(R.id.tvTime);

        mainActivityController = new MainActivityController(this);
        mainActivityController.firstSetup();
        setOnClickListeners();
    }

    public void setupMainFragment(Fragment fragment){
        mainFragment = fragment;
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, fragment);
        mFragmentTransaction.commit();
    }

    public void setupAdditionalFragment(Fragment fragment){
        addFragment = fragment;
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.addСontainerView, fragment);
        mFragmentTransaction.commit();
    }

    public void setTitle(String title){
        mTitleTextView.setText(title);
    }

    public void setAdditionalTitle(String title){
        mAdditionalTitleTextView.setText(title);
    }

    void setOnClickListeners(){
        findViewById(R.id.news).setOnClickListener(mainActivityController);
        findViewById(R.id.messages).setOnClickListener(mainActivityController);
        findViewById(R.id.profile).setOnClickListener(mainActivityController);
        findViewById(R.id.settings).setOnClickListener(mainActivityController);
        findViewById(R.id.quest).setOnClickListener(mainActivityController);
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
                        if (resource instanceof GifDrawable) {
                            if(!loadingState) ((GifDrawable)resource).setLoopCount(1);
                        }
                        return false;
                    }
                })
                .into((ImageView) findViewById(R.id.loadingCube));
    }
}

