package net.artux.pda.ui.fragments.quest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.gdx.CoreStarter;
import net.artux.pda.map.model.Map;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pda.ui.fragments.quest.models.Stage;
import net.artux.pda.utils.MultiExoPlayer;
import net.artux.pdalib.Member;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SceneController implements Serializable {

    private Chapter localChapter;
    private List<Stage> stages;
    private Stage actualStage;
    private final QuestActivity questActivity;
    private int story;
    private int chapterId;

    private MultiExoPlayer multiExoPlayer;

    public SceneController(QuestActivity questActivity, int story, int chapterId, int stageId) {
        this.questActivity = questActivity;
        loadChapter(story, chapterId, stageId);
    }

    private void loadChapter(int story, int chapterId, int stageId){
        this.story = story;
        this.chapterId = chapterId;
        System.out.println(story + " - " + chapterId + " - " + stageId);
        App.getRetrofitService().getPdaAPI().getQuest(story, chapterId).enqueue(new Callback<Chapter>() {
            @Override
            public void onResponse(Call<Chapter> call, Response<Chapter> response) {
                Chapter chapter  = response.body();
                if(chapter!=null){
                    if (multiExoPlayer!=null)
                        multiExoPlayer.release();
                    multiExoPlayer = new MultiExoPlayer(questActivity, chapter.getMusics());
                    localChapter = chapter;
                    stages = chapter.getStages();
                    loadStage(stageId);
                }else {
                    Toast.makeText(questActivity, "chapter " + chapterId + "  is null, reset", Toast.LENGTH_LONG).show();
                    loadMap(story,0, "500:1000");
                }
            }

            @Override
            public void onFailure(Call<Chapter> call, Throwable t) {
                Toast.makeText(questActivity, "Chapter " + chapterId+"("+stageId+")" + " load err, story " + story + " " + t.getMessage(), Toast.LENGTH_LONG).show();
                Timber.e(t);
            }
        });
    }

    public SceneController(QuestActivity questActivity, int story, int map, String pos){
        this.questActivity = questActivity;
        this.story = story;
        loadMap(story, map, pos);
    }

    private void synchronize(Stage stage, int id){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("story:"+story+":"+ chapterId +":"+id);
        if(stage.getActions()!=null) {
            stage.getActions().put("set", arrayList);
            App.getRetrofitService().getPdaAPI().synchronize(stage.getActions()).enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    Member member = response.body();
                    if (member != null) {
                        App.getDataManager().setMember(member);
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Timber.d( "Quest: false to set progress");
                    Timber.e(t);
                }
            });
        }else {
            HashMap<String, List<String>> actions = new HashMap<>();
            actions.put("set", arrayList);
            App.getRetrofitService().getPdaAPI().synchronize(actions).enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    Member member = response.body();
                    if (member != null) {
                        App.getDataManager().setMember(member);
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Timber.d( "Quest: false to set progress");
                    Timber.e(t);
                }
            });
        }
    }

    void loadStage(int id){
        FragmentTransaction mFragmentTransaction = questActivity.getSupportFragmentManager().beginTransaction();
        actualStage = getStage(id);
        synchronize(actualStage, id);
        try {
            switch (actualStage.getTypeStage()) {
                default:
                    Quest0Scene quest0Scene = new Quest0Scene();
                    quest0Scene.setStage(actualStage);
                    mFragmentTransaction.replace(R.id.containerView, quest0Scene);
                    break;
                case 1:
                    Quest1Scene quest1Scene = new Quest1Scene();
                    quest1Scene.setStage(actualStage);
                    mFragmentTransaction.replace(R.id.containerView, quest1Scene);
                    break;
                case 4:
                    if (actualStage.getData().containsKey("map")) {
                        int mapId = Integer.parseInt(actualStage.getData().get("map"));
                        if (actualStage.getData().containsKey("pos"))
                            loadMap(story, mapId, actualStage.getData().get("pos"));
                    }
                    break;
                case 5:
                    if (actualStage.getData().containsKey("chapter")) {
                        int chapter = Integer.parseInt(actualStage.getData().get("chapter"));
                        int stage = Integer.parseInt(actualStage.getData().get("stage"));
                        loadChapter(story, chapter, stage);
                    }
                    break;
                case 6:
                    if(actualStage.getData().containsKey("seller")) {
                        Intent intent = new Intent(questActivity, SellerActivity.class);
                        intent.putExtra("seller", Integer.parseInt(actualStage.getData().get("seller")));
                        intent.putExtra("chapter", Integer.parseInt(actualStage.getData().get("chapter")));
                        intent.putExtra("stage", Integer.parseInt(actualStage.getData().get("stage")));
                        Timber.d("Start seller activity - %s", actualStage.getData().get("seller"));
                        questActivity.startActivity(intent);
                    }
                    break;
            }
            multiExoPlayer.setSound(actualStage.getMusics());
        }catch (Exception e){
            Timber.e(e);
            Toast.makeText(questActivity,"Can not load stage, story: " + story
                    + " chapter: " + chapterId + " stage: " + actualStage.getId(), Toast.LENGTH_LONG).show();
        }
        mFragmentTransaction.commitAllowingStateLoss();
    }

    void loadMap(int story, int map, String pos){
        App.getRetrofitService().getPdaAPI().getMap(story, map).enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                Map map = response.body();
                if (map != null) {
                    map.setPlayerPos(pos);
                    loadImages(map);
                }else {
                    Toast.makeText(questActivity, "Unable to load the map. Map is null.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map> call, Throwable throwable) {
                Toast.makeText(questActivity, "Map " + map + "(" + pos + ")" + " load err, story "
                        + story + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }

    int num = 0;
    int all = 3;

    private void loadImages(Map map){
        boolean main = loadImage(map.getTextureUri(), new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                num++;
                map.setTexture(saveBitmap(resource, map.getTextureUri()));
                if (num>=all)
                    s(map);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Toast.makeText(questActivity, "Unable to load the main texture.", Toast.LENGTH_LONG).show();
            }
        });
        boolean bounds = loadImage(map.getBoundsTextureUri(), new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                num++;
                map.setBoundsTexture(saveBitmap(resource, map.getBoundsTextureUri()));
                if (num>=all)
                    s(map);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Toast.makeText(questActivity, "Unable to load bounds.", Toast.LENGTH_LONG).show();
                num++;
                if (num>=all)
                    s(map);
            }
        });
        boolean blur = loadImage(map.getBlurTextureUri(), new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                num++;
                map.setBlurTexture(saveBitmap(resource, map.getBlurTextureUri()));
                if (num>=all)
                    s(map);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Toast.makeText(questActivity, "Unable to load blur.", Toast.LENGTH_LONG).show();
                num++;
                if (num>=all)
                    s(map);
            }
        });
        if (!main)
            Toast.makeText(questActivity, "Main texture is null. Stop.", Toast.LENGTH_LONG).show();
        if (!bounds)
            Toast.makeText(questActivity, "Bounds are null.", Toast.LENGTH_LONG).show();
    }

    private boolean loadImage(String uri, CustomTarget<Bitmap> target){
        if (uri!=null) {
            if (!uri.contains("http")) {
                String url = "https://" + BuildConfig.URL + "/" + uri;
                if (!questActivity.isDestroyed())
                    Glide.with(questActivity)
                            .asBitmap()
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(target);
                return true;
            }else{
                if (!questActivity.isDestroyed())
                    Glide.with(questActivity)
                            .asBitmap()
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(target);
                return true;
            }
        }
        num++;
        return false;
    }

    void s(Map map){
        Intent intent = new Intent(questActivity, CoreStarter.class);
        intent.putExtra("map", new Gson().toJson(map));
        questActivity.startActivity(intent);
        questActivity.finish();
    }

    private String saveBitmap(Bitmap bitmap, String name) {
        name = name.replace("/", "-");
        for (int i=0; i<questActivity.getFilesDir().list().length; i++)
            if (questActivity.getFilesDir().list()[i].equals(name)) {
                Timber.d("Already exists!");
                return questActivity.getFilesDir().getAbsolutePath() + "/" + name;
            }
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            FileOutputStream fo = questActivity.openFileOutput(name, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
            Timber.d("Saved!");
        } catch (Exception e) {
            Timber.e(e);
            Timber.d("Not saved!");
            Toast.makeText(questActivity, "Не удалось сохранить:" + name
                    + ", так как " + e.getLocalizedMessage(), Toast.LENGTH_LONG ).show();
        }
        Timber.d(questActivity.getFilesDir().getAbsolutePath() + "/" + name);
        return questActivity.getFilesDir().getAbsolutePath() + "/" + name;
    }

    private Stage getStage(int id){
        Iterator<Stage> iterator = stages.iterator();
        Stage stage = null;
        while (iterator.hasNext()){
            stage = iterator.next();
            if(stage.getId()==id) break;
        }
        return stage;
    }

    public void unmute(){
        if (multiExoPlayer != null)
            multiExoPlayer.unmute();
    }

    public void mute(){
        if (multiExoPlayer != null)
            multiExoPlayer.mute();
    }

    public boolean isMuted(){
        return multiExoPlayer.isMuted();
    }

    public void release(){
        if (multiExoPlayer != null)
            multiExoPlayer.release();
    }

    public Stage getActualStage() {
        return actualStage;
    }

    public int getStory() {
        return story;
    }

    public int getChapterId() {
        return chapterId;
    }

    public Chapter getLocalChapter() {
        return localChapter;
    }
}
