package software.artux.pdanetwork.Views.Quest;

import android.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.devilsoftware.pdanetwork.R;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import software.artux.pdanetwork.Models.Member;
import software.artux.pdanetwork.Models.profile.Data;
import software.artux.pdanetwork.Views.Quest.Models.Chapter;
import software.artux.pdanetwork.Views.Quest.Models.Stage;
import software.artux.pdanetwork.activities.QuestActivity;
import software.artux.pdanetwork.app.App;
import timber.log.Timber;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
        }
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    private void doActions(Stage stage){
        Log.d("SceneManager", new Gson().toJson(stage.getActions()));

        App.getRetrofitService().getPdaAPI().doActions(stage.getActions()).enqueue(new Callback<Boolean>() {
           @Override
           public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                boolean bool = response.body();
                if (bool){
                    Toast.makeText(App.getContext(), "FINE", Toast.LENGTH_SHORT).show();
                }
           }

           @Override
           public void onFailure(Call<Boolean> call, Throwable t) {

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
        }
        mFragmentTransaction.addToBackStack(null);
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
