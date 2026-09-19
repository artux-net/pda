package net.artux.pda.ios.mock;

import com.badlogic.gdx.ApplicationLogger;

/**
 * Plain System.out/err logger. GdxAdapter.Builder needs an ApplicationLogger before
 * Gdx.app exists to hand one to (unlike Android's GDXTimberLogger, wired the same way
 * in CoreFragment), so this can't just delegate to Gdx.app.getApplicationLogger().
 */
public class ConsoleApplicationLogger implements ApplicationLogger {

    @Override
    public void log(String tag, String message) {
        System.out.println("[" + tag + "] " + message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        System.out.println("[" + tag + "] " + message);
        exception.printStackTrace(System.out);
    }

    @Override
    public void error(String tag, String message) {
        System.err.println("[" + tag + "] " + message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        System.err.println("[" + tag + "] " + message);
        exception.printStackTrace(System.err);
    }

    @Override
    public void debug(String tag, String message) {
        System.out.println("[" + tag + "] " + message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        System.out.println("[" + tag + "] " + message);
        exception.printStackTrace(System.out);
    }
}
