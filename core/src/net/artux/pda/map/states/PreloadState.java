package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.model.input.GameMap;

import java.util.LinkedList;
import java.util.List;


public class PreloadState extends State {

    private static final String protocol = "https";
    private static final String url = "files.artux.net/static/"; //todo remote config
    private static final String baseUrl = protocol + "://" + url;
    String cachePath = "cache/";
    int triesLimit = 3;

    public PreloadState(final GameStateManager gsm) {
        super(gsm);
    }

    List<String> remoteAssets = new LinkedList<>();

    long preloadTime;

    public void startLoad() {
        GameMap map = (GameMap) gsm.get("map");
        preloadTime = TimeUtils.millis();
        if (map != null) {
            Gdx.app.debug("Preload", "Checking cache for " + map.getTitle() + "{" + map.getId() + "} , load missed files.");
            remoteAssets.add(map.getTextureUri());
            remoteAssets.add(map.getBlurTextureUri());
            remoteAssets.add(map.getBoundsTextureUri());
            remoteAssets.add(map.getTilesTexture());

            for (String asset :
                    remoteAssets) {
                loadTexture(asset, 0);
            }

        } else
            gsm.getPlatformInterface().error("Can not start with null map", new NullPointerException());
    }

    public void loadTexture(final String path, final int tries) {
        final FileHandle file = Gdx.files.local(cachePath + path);
        if (!file.exists() && path != null && !path.equals("")) {
            final String url = baseUrl + path;
            Gdx.app.error("Preload", path + " missed, try to load from net.");
            Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
            request.setUrl(url);
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    if (httpResponse.getStatus().getStatusCode() == 200) {
                        final byte[] bytes = httpResponse.getResult();
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                file.writeBytes(bytes, false);
                                Gdx.app.error("Preload", path + " loaded, caching.");
                                checkForStartPlay();
                            }
                        });
                    }
                }

                @Override
                public void failed(Throwable t) {
                    if (tries > triesLimit) {
                        gsm.getPlatformInterface().error("Could not load " + url, t);
                    } else loadTexture(path, tries + 1);
                }

                @Override
                public void cancelled() {
                    if (tries > triesLimit) {
                        gsm.getPlatformInterface().error("Loading stopped " + url, new RuntimeException());
                    } else loadTexture(path, tries + 1);
                }
            });
        } else checkForStartPlay();
    }

    void checkForStartPlay() {
        GameMap map = (GameMap) gsm.get("map");
        if (map != null) {
            FileHandle file = Gdx.files.local(cachePath + map.getTextureUri());

            if (!file.exists())
                return;
            for (String path :
                    remoteAssets) {
                file = Gdx.files.local(cachePath + path);
                if (!file.exists() && path != null && !path.equals(""))
                    return;
            }
            try {
                if (!(gsm.peek() instanceof PlayState)) {
                    Gdx.app.log("Preload", "Ok, try to load Play State. Preload took " + (TimeUtils.millis() - preloadTime) + " ms.");
                    gsm.set(new PlayState(gsm));
                }
            } catch (Throwable e) {
                gsm.getPlatformInterface().error("Can not start PlayState", e);
            }
        }
    }


    @Override
    protected void handleInput() {

    }

    @Override
    protected void stop() {

    }


    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void resize(int w, int h) {

    }

    @Override
    public void dispose() {


    }

}
