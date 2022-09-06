package net.artux.pda.gdx;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import net.artux.pda.map.model.input.Map;
import net.artux.pda.ui.viewmodels.QuestViewModel;

public class MapHelper {

    public static void prepareAndLoadMap(QuestViewModel questViewModel, AppCompatActivity context, int storyId, int mapId, String pos) {
        questViewModel.getMap(storyId, mapId).observe(context, new Observer<Map>() {
            @Override
            public void onChanged(Map map) {
                if (map != null) {
                    map.setPlayerPos(pos);
                    startMap(context, map);
                    questViewModel.getMap().removeObserver(this);
                } else questViewModel.updateMap(storyId, mapId);
            }
        });
    }

    public static void prepareAndLoadMap(QuestViewModel questViewModel, MapEngine context, int storyId, int mapId, String pos) {
        questViewModel.getMap(storyId, mapId).observe(context, new Observer<Map>() {
            @Override
            public void onChanged(Map map) {
                if (map != null) {
                    map.setPlayerPos(pos);
                    startMap(context, map);
                    questViewModel.getMap().removeObserver(this);
                } else questViewModel.updateMap(storyId, mapId);
            }
        });
    }

    static void startMap(Activity context, Map map) {
        Intent intent = new Intent(context, MapEngine.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("map", map);
        context.startActivityForResult(intent, 2);
        context.finish();
    }

}
