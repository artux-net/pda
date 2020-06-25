package net.artux.pda.activities;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class CoreStarter extends AndroidApplication implements PlatformInterface {

    private GdxAdapter gdxAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        Map map = null;
        HashMap<String, String> data = (HashMap<String, String>) getIntent().getSerializableExtra("data");
        try {
            InputStream inputStream = getAssets().open("0.sm");
            try(Scanner s = new Scanner(inputStream)) {
                StringBuilder stringBuilder = new StringBuilder();
                while (s.hasNext())
                    stringBuilder.append(s.next());
                String result = stringBuilder.toString();
                System.out.println(result);
                map = new Gson().fromJson(result, Map.class);
            }
            if (data != null && data.containsKey("pos")) {
                map.setPlayerPos(data.get("pos"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        gdxAdapter = new GdxAdapter(this, map);
        initialize(gdxAdapter, config);
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
                }

            }
        });
    }
}
