package net.artux.pda.Views.Quest;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import net.artux.pda.Models.Member;
import net.artux.pda.R;
import net.artux.pda.Views.Quest.Models.Chapter;
import net.artux.pda.Views.Quest.Models.Stage;
import net.artux.pda.activities.CoreStarter;
import net.artux.pda.activities.QuestActivity;
import net.artux.pda.app.App;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class SceneController implements Serializable {

    List<Stage> stages;
    QuestActivity questActivity;
    FragmentTransaction mFragmentTransaction;

    public SceneController(QuestActivity questActivity) throws Exception{
        int story = App.getDataManager().getStoryId();
        App.getRetrofitService().getPdaAPI().getQuest(story, App.getDataManager().getChapter(0)).enqueue(new Callback<Chapter>() {
            @Override
            public void onResponse(Call<Chapter> call, Response<Chapter> response) {
                Chapter chapter  = response.body();
                if(chapter!=null){
                    stages = chapter.getStages();
                    loadLastStage();
                }
            }

            @Override
            public void onFailure(Call<Chapter> call, Throwable t) {
                t.printStackTrace();
            }
        });
        this.questActivity = questActivity;
        
    }

    public void loadLastStage(){
        mFragmentTransaction = questActivity.getFragmentManager().beginTransaction();
        int id = getStageId();
        Stage actualStage = getStage(id);
        if(id == 0){
            doActions(actualStage);
        }
        QuestFragment questFragment;
        switch (actualStage.getTypeStage()){
            case 0:
                questFragment = new QuestFragment();
                questFragment.setStage(actualStage);
                mFragmentTransaction.replace(R.id.containerView, questFragment);
                break;
            case 1:
                questFragment = new QuestFragment();
                questFragment.setStage(actualStage);
                mFragmentTransaction.replace(R.id.containerView, questFragment);
                break;
            case 2:
                questActivity.startActivity(new Intent(questActivity, CoreStarter.class));
                break;
        }
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    private void doActions(Stage stage){
        App.getRetrofitService().getPdaAPI().doActions(stage.getActions()).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member = response.body();
                if (member!=null){
                    App.getDataManager().setMember(member);
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {

            }
        });
    }

    private void setProgress(int stageId){

    }

    void loadStage(int id){
        mFragmentTransaction = questActivity.getFragmentManager().beginTransaction();
        Stage actualStage = getStage(id);
        setProgress(id);
        doActions(actualStage);
        QuestFragment questFragment;
        switch (actualStage.getTypeStage()){
            case 0:
                questFragment = new QuestFragment();
                questFragment.setStage(actualStage);
                mFragmentTransaction.replace(R.id.containerView, questFragment);
                break;
            case 1:
                questFragment = new QuestFragment();
                questFragment.setStage(actualStage);
                mFragmentTransaction.replace(R.id.containerView, questFragment);
                break;
            case 2:
                questActivity.startActivity(new Intent(questActivity, CoreStarter.class));
                break;
        }
        mFragmentTransaction.commit();
    }

    private int getStageId(){
        return 0;
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
