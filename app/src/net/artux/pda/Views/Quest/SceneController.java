package net.artux.pda.Views.Quest;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import net.artux.pda.Models.Member;
import net.artux.pda.R;
import net.artux.pda.Views.Quest.Models.Chapter;
import net.artux.pda.Views.Quest.Models.Stage;
import net.artux.pda.activities.CoreStarter;
import net.artux.pda.activities.QuestActivity;
import net.artux.pda.app.App;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SceneController implements Serializable {

    private List<Stage> stages;
    private QuestActivity questActivity;
    private int story;
    private int chapter;

    public SceneController(QuestActivity questActivity, int story, int chapter, int stageId) {
        this.story = story;
        this.chapter = chapter;
        this.questActivity = questActivity;
        App.getRetrofitService().getPdaAPI().getQuest(story, chapter).enqueue(new Callback<Chapter>() {
            @Override
            public void onResponse(Call<Chapter> call, Response<Chapter> response) {
                Chapter chapter  = response.body();
                if(chapter!=null){
                    stages = chapter.getStages();
                    loadStage(stageId);
                }
            }

            @Override
            public void onFailure(Call<Chapter> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void synchronize(Stage stage, int id){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("story:"+story+":"+chapter+":"+id);
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
                    Log.d("Quest", " false to set progress");
                    t.printStackTrace();
                }
            });
            Log.d("Quest", " try to set progress");
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
                    Log.d("Quest", " false to set progress");
                    t.printStackTrace();
                }
            });
        }
    }

    private void setProgress(int stageId){

    }

    void loadStage(int id){
        FragmentTransaction mFragmentTransaction = questActivity.getSupportFragmentManager().beginTransaction();
        Stage actualStage = getStage(id);
        synchronize(actualStage, id);
        switch (actualStage.getTypeStage()){
            default:
                QuestFragment questFragment = new QuestFragment();
                questFragment.setStage(actualStage);
                mFragmentTransaction.replace(R.id.containerView, questFragment);
                break;
            case 4:
                Intent intent = new Intent(questActivity, CoreStarter.class);
                intent.putExtra("data",actualStage.getData());
                questActivity.startActivity(intent);
                break;
        }
        mFragmentTransaction.commit();
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

}
