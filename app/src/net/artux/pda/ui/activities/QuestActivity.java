package net.artux.pda.ui.activities;

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
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.ui.fragments.quest.SceneController;
import net.artux.pda.ui.fragments.quest.models.Stage;
import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Story;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class QuestActivity extends AppCompatActivity implements View.OnClickListener {

    private SceneController sceneController;

    private TextView tvTime;
    private ImageView musicImage;

    private String background_url = "";
    private ImageSwitcher switcher;

    private BroadcastReceiver _broadcastReceiver;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        tvTime = findViewById(R.id.sceneTime);
        musicImage = findViewById(R.id.musicSetup);
        musicImage.setOnClickListener(this);
        findViewById(R.id.closeButton).setOnClickListener(this);
        findViewById(R.id.exitButton).setOnClickListener(this);
        findViewById(R.id.log).setOnClickListener(this);
        switcher = findViewById(R.id.switcher);
        switcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        switcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        App.getRetrofitService().getPdaAPI().loginUser().enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member = response.body();
                if (member!= null){
                    startLoading(member);
                }else{
                    Toast.makeText(getApplicationContext(), "Null member, try again", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Timber.e(t);
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void startLoading(Member member){
        int[] keys = getIntent().getIntArrayExtra("keys");
        // keys for loading specific stage
        if (keys==null) {
            HashMap<String, String> temp = member.getData().getTemp();

            int storyId = getIntent().getIntExtra("story", -1);

            if (storyId < 0) {
                String currentStory = temp.get("currentStory");
                if (currentStory != null)
                    storyId = Integer.parseInt(currentStory);
                else storyId = 0;
            }

            int map = getIntent().getIntExtra("map", -1);
            if (map != -1) {
                // load map
                sceneController = new SceneController(this, storyId, map, getIntent().getStringExtra("pos"));
            } else {
                boolean found = false;
                for (Story story : App.getDataManager().getMember().getData().getStories()) {
                    if (story.getStoryId() == storyId) {
                        found = true;
                        int chapter = getIntent().getIntExtra("chapter", story.getLastChapter());
                        int stage = getIntent().getIntExtra("stage", story.getLastStage());
                        sceneController = new SceneController(this, story.getStoryId(), chapter, stage);
                    }
                }
                if (!found)
                    sceneController = new SceneController(this, storyId, 1, 0);
            }
        }else
            sceneController = new SceneController(this, keys[0], keys[1], keys[2]);
    }

    public void setTitle(String title){
        ((TextView)findViewById(R.id.sceneTitle)).setText(title);
    }

    public void setLoading(boolean flag){
        if (flag)
            findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
    }


    public void setBackground(String backgroundURL){
        if(!background_url.equals(backgroundURL)) {
            if (!backgroundURL.contains("http")){
                background_url = "https://" + BuildConfig.URL + "/" + backgroundURL;
            }else background_url = backgroundURL;

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
        switch (v.getId()){
            case R.id.musicSetup:
                if(sceneController.isMuted()){
                    musicImage.setImageDrawable(ResourcesCompat
                            .getDrawable(getResources(), R.drawable.ic_vol_on, getApplicationContext().getTheme()));
                    sceneController.unmute();
                } else {
                    musicImage.setImageDrawable(ResourcesCompat
                            .getDrawable(getResources(), R.drawable.ic_vol_off, getApplicationContext().getTheme()));
                    sceneController.mute();
                }
                break;
            case R.id.closeButton:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.exitButton:
                HashMap<String, List<String>> action = new HashMap<>();
                action.put("reset_current", new ArrayList<>());
                App.getRetrofitService().getPdaAPI().synchronize(action).enqueue(new Callback<Member>() {
                    @Override
                    public void onResponse(Call<Member> call, Response<Member> response) {
                        Member member = response.body();
                        if (member!=null) {
                            App.getDataManager().setMember(member);
                            Intent intent = new Intent(QuestActivity.this, MainActivity.class);
                            intent.putExtra("section", "stories");
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Member> call, Throwable t) {
                        Timber.e(t);
                    }
                });
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
        if (sceneController!=null)
            sceneController.release();
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        System.out.println("Destroyed QuestActivity");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }

    public SceneController getSceneController(){
        return sceneController;
    }

}
