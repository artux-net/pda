package net.artux.engine.resource.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.Array;

import net.artux.engine.resource.types.NetTexture;
import net.artux.pda.common.PropertyFields;

import java.util.Properties;

public class NetTextureAssetLoader extends AsynchronousAssetLoader<NetTexture, NetTextureAssetLoader.TextureParameter> {

    private final static String TAG = "NETLoader";

    private final String baseUrl;
    private final String fileCachePath = "cache/";

    public NetTextureAssetLoader(Properties properties) {
        super(new LocalFileHandleResolver());
        baseUrl = properties.getProperty(PropertyFields.RESOURCE_URL);
    }

    @Override
    public void loadAsync(AssetManager manager, String path, FileHandle file, TextureParameter parameter) {
        synchronized (file) {
            if (!file.exists() && path != null && !path.equals("")) {
                final String url;
                if (!path.contains("http"))
                    url = baseUrl + path;
                else
                    url = path;
                Gdx.app.error("Preload", path + " missed, try to load from net.");
                Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
                request.setTimeOut(20 * 1000);
                request.setUrl(url);

                Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        synchronized (file) {
                            if (httpResponse.getStatus().getStatusCode() == 200) {
                                final byte[] bytes = httpResponse.getResult();
                                file.writeBytes(bytes, false);
                                Gdx.app.debug(TAG, path + " loaded, caching.");
                            }
                            file.notify();
                        }

                    }

                    @Override
                    public void failed(Throwable t) {
                        synchronized (file) {
                            Gdx.app.error(TAG, "Could not load " + url, t);
                            file.notify();
                        }
                    }

                    @Override
                    public void cancelled() {
                        synchronized (file) {
                            file.notify();
                        }
                    }
                });

                try {
                    file.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!file.exists())
                    Gdx.app.error(TAG, "Loading failed " + url, new RuntimeException());
            }
        }

        info.filename = path;
        if (parameter == null || parameter.textureData == null) {
            Pixmap.Format format = null;
            boolean genMipMaps = false;
            info.texture = null;

            if (parameter != null) {
                format = parameter.format;
                genMipMaps = parameter.genMipMaps;
                info.texture = parameter.texture;
            }

            info.data = TextureData.Factory.loadFromFile(file, format, genMipMaps);
        } else {
            info.data = parameter.textureData;
            info.texture = parameter.texture;
        }
        if (!info.data.isPrepared()) info.data.prepare();
    }

    @Override
    public NetTexture loadSync(AssetManager manager, String fileName, FileHandle file, TextureParameter parameter) {

        if (info == null) return null;
        Texture texture = info.texture;
        if (texture != null) {
            texture.load(info.data);
        } else {
            texture = new Texture(info.data);
        }
        if (parameter != null) {
            texture.setFilter(parameter.minFilter, parameter.magFilter);
            texture.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return new NetTexture(texture);

    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TextureParameter parameter) {
        return null;
    }

    static public class TextureLoaderInfo {
        String filename;
        TextureData data;
        Texture texture;
    }

    TextureLoaderInfo info = new TextureLoaderInfo();

    static public class TextureParameter extends AssetLoaderParameters<NetTexture> {
        /**
         * the format of the final Texture. Uses the source images format if null
         **/
        public Pixmap.Format format = null;
        /**
         * whether to generate mipmaps
         **/
        public boolean genMipMaps = false;
        /**
         * The texture to put the {@link TextureData} in, optional.
         **/
        public Texture texture = null;
        /**
         * TextureData for textures created on the fly, optional. When set, all format and genMipMaps are ignored
         */
        public TextureData textureData = null;
        public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
        public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;
        public Texture.TextureWrap wrapU = Texture.TextureWrap.ClampToEdge;
        public Texture.TextureWrap wrapV = Texture.TextureWrap.ClampToEdge;
    }
}
