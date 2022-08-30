package net.artux.pda.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.repositories.QuestRepository;
import net.artux.pda.repositories.SummaryRepository;
import net.artux.pda.repositories.UserRepository;
import net.artux.pda.services.PdaAPI;
import net.artux.pdanetwork.api.DefaultApi;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class PDAApplication extends Application {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected UserRepository userRepository;
    @Inject
    protected SummaryRepository summaryRepository;
    @Inject
    protected QuestRepository questRepository;

    @Inject
    protected DefaultApi defaultApi;
    @Inject
    protected PdaAPI pdaAPI;


    public static int[] group_avatars = {
            R.drawable.g0,
            R.drawable.g1,
            R.drawable.g2,
            R.drawable.g3,
            R.drawable.g4,
            R.drawable.g5,
            R.drawable.g6,
            R.drawable.g7,
            R.drawable.g8
    };

    public static StringBuilder logBuilder = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();

        /*userRepository = new UserRepository(mRetrofitService.getDefaultApi(), profileCache, memberCache);
        questRepository = new QuestRepository(mRetrofitService.getPdaAPI(), mRetrofitService.getDefaultApi(),
                new Cache<>(StoryData.class, getApplicationContext(), gson),
                chapterCache, mapCache);
        summaryRepository = new SummaryRepository(summaryCache);*/

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        else
            Timber.plant(new CrashReportingTree());

        Timber.i("App started.");
        MobileAds.initialize(this, initializationStatus -> {
            Timber.d("Ads initialization");
            initializationStatus.getAdapterStatusMap().forEach((s, adapterStatus) ->
                    Timber.d(s + " : " + adapterStatus.getDescription() + " latency: " + adapterStatus.getLatency()));
        });

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public QuestRepository getQuestRepository() {
        return questRepository;
    }

    public SummaryRepository getSummaryRepository() {
        return summaryRepository;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
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

    public DataManager getDataManager() {
        return dataManager;
    }

    public DefaultApi getDefaultApi() {
        return defaultApi;
    }

    public PdaAPI getOldApi() {
        return pdaAPI;
    }
}
