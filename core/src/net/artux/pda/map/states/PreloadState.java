package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.artux.pda.map.model.Map;


public class PreloadState extends State {

    private static final String protocol = "https";
    private static final String url = "api.artux.net/";
    private static final String baseUrl = protocol + "://" + url;
    String cachePath = "cache/";
    int triesLimit = 3;

    public PreloadState(final GameStateManager gsm) {
        super(gsm);
    }

    public void startLoad(Batch batch){
        Map map = (Map) gsm.get("map");
        if (map != null) {
            loadTexture(batch, map.getTextureUri(), 0);
            loadTexture(batch, map.getBlurTextureUri(), 0);
            loadTexture(batch, map.getBoundsTextureUri(), 0);
        }
        else gsm.getPlatformInterface().error("Can not start with null map", new NullPointerException());
    }

    public void loadTexture(final Batch batch, final String path, final int tries){
        final FileHandle file = Gdx.files.local(cachePath+path);
        if (!file.exists() && path!=null && !path.equals("")){
            final String url = baseUrl + path;
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
                                checkForStartPlay(batch);
                            }
                        });
                    }
                }

                @Override
                public void failed(Throwable t) {
                    if (tries > triesLimit){
                        gsm.getPlatformInterface().error("Could not load " + url, t);
                    }else loadTexture(batch, path, tries+1);
                }

                @Override
                public void cancelled() {
                    if (tries > triesLimit){
                        gsm.getPlatformInterface().error("Loading stopped " + url, new RuntimeException());
                    }else loadTexture(batch, path, tries+1);
                }
            });
        }else checkForStartPlay(batch);
    }

    void checkForStartPlay(Batch batch){
        Map map = (Map) gsm.get("map");
        if (map != null) {
            FileHandle file = Gdx.files.local(cachePath+map.getTextureUri());

            if (!file.exists())
                return;
            String path = map.getBlurTextureUri();
            file = Gdx.files.local(cachePath+path);
            if (!file.exists() && path != null && !path.equals(""))
                return;
            path = map.getBoundsTextureUri();
            file = Gdx.files.local(cachePath+path);
            if (!file.exists() && path != null && !path.equals(""))
                return;
            try {
                if (!(gsm.peek() instanceof PlayState))
                    gsm.set(new PlayState(gsm, batch));
            }catch (Throwable e){
                Gdx.app.error("Preload", "Can not start PlayState", e);
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
    public void render(SpriteBatch batch) {


    }

    @Override
    public void resize(int w, int h) {

    }

    @Override
    public void dispose() {


    }

}
