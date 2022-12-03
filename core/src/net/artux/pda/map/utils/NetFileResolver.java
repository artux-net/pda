package net.artux.pda.map.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

import net.artux.pda.common.PropertyFields;

import java.util.Properties;

public class NetFileResolver implements FileHandleResolver {

    private final static String TAG = "NET_FILE_RESOLVER";

    private final Properties properties;
    private final String baseUrl;
    private final String fileCachePath = "cache/";

    public NetFileResolver(Properties properties) {
        this.properties = properties;
        baseUrl = properties.getProperty(PropertyFields.RESOURCE_URL);
    }

    @Override
    public FileHandle resolve(String path) {
        final FileHandle file = Gdx.files.local(path);
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
                        file.writeBytes(bytes, false);
                        Gdx.app.error(TAG, path + " loaded, caching.");
                    }
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.error(TAG, "Could not load " + url, t);
                }

                @Override
                public void cancelled() {
                    Gdx.app.error(TAG, "Loading stopped " + url, new RuntimeException());
                }
            });
        }
        return file;
    }
}
