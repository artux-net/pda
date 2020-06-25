package net.artux.pda.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import net.artux.pda.Models.profile.Story;
import net.artux.pda.R;
import net.artux.pda.Views.Quest.SceneController;
import net.artux.pda.app.App;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class QuestActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    SceneController sceneController;
    Glide glide;
    MediaPlayer mediaPlayer;

    TextView tvTime;
    ImageView musicImage;
    ImageView background;
    boolean mute;

    private String background_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);
        glide = Glide.get(this);

        tvTime = findViewById(R.id.sceneTime);
        musicImage = findViewById(R.id.musicSetup);
        musicImage.setOnClickListener(this);
        background = findViewById(R.id.background);

        HashMap<String, String> temp = App.getDataManager().getMember().getData().getTemp();
        String currentStory = temp.get("currentStory");
        if(currentStory!=null){
            for (Story story : App.getDataManager().getMember().getData().getStories()){
                if (story.getId()==Integer.parseInt(currentStory)){
                    System.out.println("Story: " + story.getId());
                    System.out.println("Chapter: " + story.getLastChapter());
                    System.out.println("Stage: " + story.getLastStage());
                    int chapter = getIntent().getIntExtra("chapter", story.getLastChapter());
                    int stage = getIntent().getIntExtra("stage", story.getLastStage());
                    sceneController = new SceneController( this, story.getId(), chapter, stage);
                }
            }
        } else {
            System.out.println("null");
            sceneController = new SceneController( this, getIntent().getIntExtra("story",0), 0, 0);
        }
    }

    public void setTitle(String title){
        ((TextView)findViewById(R.id.sceneTitle)).setText(title);
    }

    public void setBackground(String backgroundURL){
        if(!background_url.equals(backgroundURL)) {
            background_url = backgroundURL;

            Glide.with(this)
                    .asBitmap()
                    .load(backgroundURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new Target<Bitmap>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {

                        }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ImageViewAnimatedChange(QuestActivity.this, background, resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void getSize(@NonNull SizeReadyCallback cb) {

                        }

                        @Override
                        public void removeCallback(@NonNull SizeReadyCallback cb) {

                        }

                        @Override
                        public void setRequest(@Nullable Request request) {

                        }

                        @Nullable
                        @Override
                        public Request getRequest() {
                            return null;
                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {

                        }

                        @Override
                        public void onDestroy() {

                        }
                    });
        }
    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    public void setMusic(String url){
        releaseMediaPlayer();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.musicSetup:
                if(mute){
                    musicImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_vol_on));
                    mute = false;
                    mediaPlayer.start();
                } else {
                    musicImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_vol_off));
                    mute = true;
                    mediaPlayer.pause();
                }
                break;
            case R.id.closeButton:
                finish();
                break;
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }


    BroadcastReceiver _broadcastReceiver;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

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
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    public SceneController getSceneController(){
        return sceneController;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(!mute){
            mp.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}
