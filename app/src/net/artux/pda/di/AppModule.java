package net.artux.pda.di;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.artux.pda.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import timber.log.Timber;

@Module
@InstallIn({SingletonComponent.class})
public class AppModule {

    @Provides
    @Singleton
    public Timber.Tree tree(){
        if (BuildConfig.DEBUG)
            return new Timber.DebugTree();
        else
            return new CrashReportingTree();
    }

    private static class CrashReportingTree extends Timber.Tree {

        public static StringBuilder logBuilder = new StringBuilder();

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (tag != null)
                message = tag + " : " + message;
            logBuilder.append(message).append("\n");
            FirebaseCrashlytics.getInstance().log(message);
            if (t != null && !BuildConfig.DEBUG) {
                logBuilder.append(t).append("\n");
                FirebaseCrashlytics.getInstance().recordException(t);
                t.printStackTrace();
            }
        }
    }

}
