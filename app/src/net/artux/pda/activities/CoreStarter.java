package net.artux.pda.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Position;
import net.artux.pda.map.platform.PlatformInterface;

import java.nio.file.FileSystems;
import java.util.LinkedList;

public class CoreStarter extends AndroidApplication implements PlatformInterface {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        Position position = new Position();
        position.x = 330;
        position.y = 960;
        Map map = new Map("Кордон","maps/map_escape.png",
                "maps/bounds.png", position, new LinkedList<Point>());
        initialize(new GdxAdapter(this, map), config);

    }

    @Override
    public void send(final String[] params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), params[0], Toast.LENGTH_SHORT).show();

            }
        });
    }
}
