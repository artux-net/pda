package net.artux.pda.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.LogActivity;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.views.quest.SceneController;
import net.artux.pda.views.quest.models.Stage;
import net.artux.pdalib.profile.Story;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class QuestActivity extends AppCompatActivity implements View.OnClickListener {

    SceneController sceneController;
    Glide glide;

    TextView tvTime;
    ImageView musicImage;
    ImageView background;
    boolean mute;

    private String background_url = "";
    ImageSwitcher switcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);
        glide = Glide.get(this);

        tvTime = findViewById(R.id.sceneTime);
        musicImage = findViewById(R.id.musicSetup);
        musicImage.setOnClickListener(this);
        findViewById(R.id.closeButton).setOnClickListener(this);
        findViewById(R.id.log).setOnClickListener(this);
        switcher = findViewById(R.id.switcher);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));


        int[] keys = getIntent().getIntArrayExtra("keys");
        if (keys==null) {
            HashMap<String, String> temp = App.getDataManager().getMember().getData().getTemp();

            int storyId = getIntent().getIntExtra("story", -1);

            if (storyId < 0) {
                String currentStory = temp.get("currentStory");
                if (currentStory != null)
                    storyId = Integer.parseInt(currentStory);
                else storyId = 0;
            }

            int map = getIntent().getIntExtra("map", -1);
            if (map != -1) {
                sceneController = new SceneController(this, storyId, map, getIntent().getStringExtra("pos"));
            } else {
                boolean found = false;
                for (Story story : App.getDataManager().getMember().getData().getStories()) {
                    if (story.getStoryId() == storyId) {
                        found = true;
                        int chapter = getIntent().getIntExtra("chapter", story.getLastChapter());
                        int stage = getIntent().getIntExtra("stage", story.getLastStage());
                        System.out.println(storyId + " : " + chapter + " : " + stage);
                        sceneController = new SceneController(this, story.getStoryId(), chapter, stage);
                    }
                }
                if (!found)
                    sceneController = new SceneController(this, storyId, 0, 0);
            }
        }else
            sceneController = new SceneController(this, keys[0], keys[1], keys[2]);
    }

    public void setTitle(String title){
        ((TextView)findViewById(R.id.sceneTitle)).setText(title);
    }

    public void setBackground(String backgroundURL){
        if(!background_url.equals(backgroundURL)) {
            background_url = backgroundURL;

            Glide.with(this)
                    .load(backgroundURL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into((ImageView) switcher.getNextView());
            switcher.showNext();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.musicSetup:
                if(mute){
                    musicImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_vol_on));
                    mute = false;
                    sceneController.unmute();
                } else {
                    musicImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_vol_off));
                    mute = true;
                    sceneController.mute();
                }
                break;
            case R.id.closeButton:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.log:
                Stage stage = sceneController.getActualStage();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Story: ").append(sceneController.getStory()).append("\n");
                stringBuilder.append("Chapter: ").append(sceneController.getChapterId()).append("\n");

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(stage);
                stringBuilder.append(json);

                Intent intent = new Intent(this, LogActivity.class);
                intent.putExtra("text", stringBuilder.toString());
                startActivity(intent);

                break;
        }
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
        sceneController.release();
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    public SceneController getSceneController(){
        return sceneController;
    }

}
