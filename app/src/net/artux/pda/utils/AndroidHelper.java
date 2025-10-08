package net.artux.pda.utils;

import android.view.View;
import android.view.Window;

public class AndroidHelper {

    public static void hideNavBar(Window window) {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        window.getDecorView().setSystemUiVisibility(flags);

        final View decorView = window.getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(flags);
            }
        });
    }

}
