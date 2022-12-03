package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.core.CoreComponent;
import net.artux.pda.model.map.GameMap;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;


public class PreloadState extends State {

    private final String baseUrl;
    private final int triesLimit = 3;
    private final String fileCachePath = "cache/";
    private final List<String> remoteAssets;
    private long preloadTime;
    private final CoreComponent statesComponent;

    @Inject
    public PreloadState(final GameStateManager gsm, DataRepository dataRepository, CoreComponent statesComponent) {
        super(gsm, dataRepository);
        baseUrl = dataRepository.getProperties().getProperty(PropertyFields.RESOURCE_URL);
        remoteAssets = new LinkedList<>();
        this.statesComponent = statesComponent;
    }

    public void resume() {
        GameMap map = dataRepository.getGameMap();
        preloadTime = TimeUtils.millis();
        if (map != null) {
            Gdx.app.debug("Preload", "Checking cache for " + map.getTitle() + "{" + map.getId() + "} , load missed files.");
            remoteAssets.add(map.getTexture());
            remoteAssets.add(map.getBlurTexture());
            remoteAssets.add(map.getBoundsTexture());
            remoteAssets.add(map.getTilesTexture());

            for (String asset :
                    remoteAssets) {
                loadTexture(asset, 0);
            }

        } else
            dataRepository.getPlatformInterface().error("Can not start with null map", new NullPointerException());
    }

    public void loadTexture(final String path, final int tries) {
        final FileHandle file = Gdx.files.local(fileCachePath + path);
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
                        Gdx.app.postRunnable(() -> {
                            file.writeBytes(bytes, false);
                            Gdx.app.error("Preload", path + " loaded, caching.");
                            checkForStartPlay();
                        });
                    }
                }

                @Override
                public void failed(Throwable t) {
                    if (tries > triesLimit) {
                        dataRepository.getPlatformInterface().error("Could not load " + url, t);
                    } else loadTexture(path, tries + 1);
                }

                @Override
                public void cancelled() {
                    if (tries > triesLimit) {
                        dataRepository.getPlatformInterface().error("Loading stopped " + url, new RuntimeException());
                    } else loadTexture(path, tries + 1);
                }
            });
        } else checkForStartPlay();
    }

    boolean started = false;

    void checkForStartPlay() {
        GameMap map = dataRepository.getGameMap();
        if (map != null) {
            FileHandle file = Gdx.files.local(fileCachePath + map.getTexture());

            if (!file.exists())
                return;
            for (String path :
                    remoteAssets) {
                file = Gdx.files.local(fileCachePath + path);
                if (!file.exists() && path != null && !path.equals(""))
                    return;
            }
            try {
                if (!started) {
                    started = true;
                    Gdx.app.log("Preload", "Ok, try to load Play State. Preload took " + (TimeUtils.millis() - preloadTime) + " ms.");
                    gsm.set(statesComponent.getPlayState());
                }
            } catch (Throwable e) {
                dataRepository.getPlatformInterface().error("Can not start PlayState", e);
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
