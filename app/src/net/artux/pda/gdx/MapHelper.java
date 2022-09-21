package net.artux.pda.gdx;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.map.model.input.Map;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.viewmodels.UserViewModel;

public class MapHelper {

    public static void prepareAndLoadMap(ViewModelProvider viewModelProvider, AppCompatActivity context, int storyId, int mapId, String pos) {
        QuestViewModel questViewModel = viewModelProvider.get(QuestViewModel.class);
        UserViewModel userViewModel = viewModelProvider.get(UserViewModel.class);
        questViewModel.getMap(storyId, mapId).observe(context, new Observer<>() {
            @Override
            public void onChanged(Map map) {
                if (map != null) {
                    map.setPlayerPos(pos);
                    startMap(context, map, userViewModel.getFromCache(), questViewModel.getCachedData());
                    questViewModel.getMap().removeObserver(this);
                } else questViewModel.updateMap(storyId, mapId);
            }
        });
    }

    static void startMap(Activity context, Map map, UserModel userModel, StoryDataModel dataModel) {
        Intent intent = new Intent(context, MapEngine.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("map", map);
        intent.putExtra("data", dataModel);
        intent.putExtra("user", userModel);
        context.startActivityForResult(intent, 2);
        context.finish();
    }

}
