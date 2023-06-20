package net.artux.pda.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.items.ItemsContainerModel;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DesktopStarter implements PlatformInterface {

    public static void main(String[] args) {
        new DesktopStarter().create();
    }

    private void create() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "PDA Test";
        config.width = 480;
        config.height = 800;

        ItemsContainerModel itemsContainerModel = getItems();

        /*GdxAdapter gdxAdapter = new GdxAdapter.Builder(this)
                .items()
                .map(new GameMap())
                .props(new Properties())
                .build();
        new LwjglApplication(new (), config);*/
    }

    private ItemsContainerModel getItems() {
        URL url = null;
        try {
            url = new URL("https://dev.artux.net/pdanetwork/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setAuthenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("max", "12345678".toCharArray());
                }
            });
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ItemsContainerModel();
    }

    @Override
    public void send(Map<String, String> data) {

    }

    @Override
    public void applyActions(Map<String, List<String>> actions) {

    }

    @Override
    public void restart() {

    }

    @Override
    public void openPDA() {

    }

    @Override
    public void rewardedVideoAd() {

    }

    @Override
    public void rewardedBannerAd() {

    }

    @Override
    public void debug(String msg) {

    }

    @Override
    public void toast(String msg) {

    }

    @Override
    public void error(String msg, Throwable t) {

    }
}