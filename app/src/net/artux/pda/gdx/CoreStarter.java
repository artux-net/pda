package net.artux.pda.gdx;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.app.App;
import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.quest.SellerActivity;
import net.artux.pdalib.arena.Connection;

import java.util.HashMap;

import timber.log.Timber;

public class CoreStarter extends AndroidApplication implements PlatformInterface {

    Gson gson = new Gson();
    GdxAdapter gdxAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        Timber.d("Core start, isArena: " + getIntent().getBooleanExtra("arena", false));
        if(getIntent().getBooleanExtra("arena", false)){
            Connection connection = new Connection("192.168.1.104:8080", App.getDataManager().getAuthToken(), "1");
            //gdxAdapter = new GdxAdapter(this,  App.getDataManager().getMember(), connection);
        }else{
            Map map = gson.fromJson(getIntent().getStringExtra("map"), Map.class);
            gdxAdapter = new GdxAdapter(this);
            gdxAdapter.put("map", map);
            gdxAdapter.put("member", App.getDataManager().getMember());
            if (map == null || App.getDataManager().getMember()==null){

                Timber.wtf("Nulls");

            }
        }
        initialize(gdxAdapter, config);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
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
                Timber.d("Got command: %s", data.toString());
                Intent intent = null;
                if(data.containsKey("chapter")){
                    intent = new Intent(this, QuestActivity.class);
                    intent.putExtra("chapter", Integer.parseInt(data.get("chapter")));
                    intent.putExtra("stage", Integer.parseInt(data.get("stage")));
                    Timber.d("Start QuestActivity - %s - %s", data.get("chapter"), data.get("stage"));
                }else if (data.containsKey("seller")){
                    intent = new Intent(this, SellerActivity.class);
                    intent.putExtra("seller", Integer.parseInt(data.get("seller")));
                    intent.putExtra("map", Integer.parseInt(data.get("map")));
                    intent.putExtra("pos", data.get("pos"));
                    Timber.d("Start seller activity - %s", data.get("seller"));
                }else if (data.containsKey("map")){
                    intent = new Intent(this, QuestActivity.class);
                    intent.putExtra("map", Integer.parseInt(data.get("map")));
                    intent.putExtra("pos", data.get("pos"));
                    Timber.d("Start map - %s, position: %s", data.get("map"), data.get("pos"));
                }else if (data.containsKey("openPda")){
                    Timber.d("Start MainActivity");
                    intent = new Intent(this, MainActivity.class);
                }

                if (intent != null){
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Timber.d("Destroyed CoreStarter");
        gdxAdapter = null;
        super.onDestroy();
    }
}
