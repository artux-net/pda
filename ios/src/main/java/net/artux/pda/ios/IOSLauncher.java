package net.artux.pda.ios;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import net.artux.pda.flow.PdaFlowGame;

/** Standard libGDX RoboVM/MobiVM launcher shape. */
public class IOSLauncher extends IOSApplication.Delegate {

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
        return new IOSApplication(new PdaFlowGame(), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}
