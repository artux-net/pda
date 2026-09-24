package net.artux.pda.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import net.artux.pda.flow.PdaFlowGame;
import net.artux.pda.flow.network.FlowApiClient;
import net.artux.pda.ios.net.SniSSLSocketFactory;

import javax.net.ssl.HttpsURLConnection;

/** Standard libGDX RoboVM/MobiVM launcher shape. */
public class IOSLauncher extends IOSApplication.Delegate {

    // Kept for the app's lifetime - the notification center holds the blocks, but losing the
    // Java-side tokens would make them impossible to remove.
    @SuppressWarnings("unused")
    private NSObject[] keyboardObservers;

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        // Info.plist.xml's UISupportedInterfaceOrientations is what actually locks the
        // device orientation; these just tell libGDX's own input/orientation handling
        // which axis to expect, matching every Android activity's sensorLandscape.
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        // Full registration -> login -> story selection -> stage dialogue -> map flow,
        // against the real backend (see net.artux.pda.flow's own docs for the architecture).
        // MockDataFactory.createApplication() (mock.MockDataFactory) is still here, unused,
        // as a reference/fallback for jumping straight to the map with fabricated data.
        // Without SNI every HTTPS request to artux.net fails its handshake - see
        // SniSSLSocketFactory. The default HttpsURLConnection factory covers Gdx.net (map
        // textures via NetTextureAssetLoader); FlowApiClient's OkHttp gets it explicitly.
        SniSSLSocketFactory tls = SniSSLSocketFactory.createDefault();
        HttpsURLConnection.setDefaultSSLSocketFactory(tls);
        FlowApiClient api = new FlowApiClient(FlowApiClient.DEFAULT_BASE_URL, tls, tls.trustManager());
        PdaFlowGame game = new PdaFlowGame(api);
        observeKeyboard(game);
        return new IOSApplication(game, config);
    }

    /**
     * libGDX has no keyboard-height API, so hand iOS's own keyboard frame to the flow, which
     * lifts its screens clear of it (see BaseFlowScreen.updateKeyboardShift). Heights are in
     * points - the same units as the flow's ScreenViewport.
     */
    private void observeKeyboard(PdaFlowGame game) {
        keyboardObservers = new NSObject[]{
                UIWindow.Notifications.observeKeyboardWillShow(animation -> {
                    float height = (float) animation.getEndFrame().getHeight();
                    Gdx.app.postRunnable(() -> game.onKeyboardHeightChanged(height));
                }),
                UIWindow.Notifications.observeKeyboardWillHide(animation ->
                        Gdx.app.postRunnable(() -> game.onKeyboardHeightChanged(0f)))
        };
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}
