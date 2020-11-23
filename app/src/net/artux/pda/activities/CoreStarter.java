package net.artux.pda.activities;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;

import net.artux.pda.app.App;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;

import java.util.HashMap;

public class CoreStarter extends AndroidApplication implements PlatformInterface {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        Map map = new Gson().fromJson(getIntent().getStringExtra("map"), Map.class);

        GdxAdapter gdxAdapter = new GdxAdapter(CoreStarter.this, map, App.getDataManager().getMember());
        initialize(gdxAdapter, config);
    }

    @Override
    protected void onResume() {
        System.out.println("Resume");
        super.onResume();
    }

    @Override
    public void send(final HashMap<String, String> data) {
        runOnUiThread(() -> {
            if (data!=null) {
                if(data.containsKey("chapter")){
                    Intent intent = new Intent(this, QuestActivity.class);
                    intent.putExtra("chapter", Integer.parseInt(data.get("chapter")));
                    intent.putExtra("stage", Integer.parseInt(data.get("stage")));
                    startActivity(intent);
                    finish();
                }else if (data.containsKey("map")){
                    Intent intent = new Intent(this, QuestActivity.class);
                    intent.putExtra("map", Integer.parseInt(data.get("map")));
                    intent.putExtra("pos", data.get("pos"));
                    startActivity(intent);
                    finish();
                }else if (data.containsKey("openPda")){
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }

            }
        });
    }
}
