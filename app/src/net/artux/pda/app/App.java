package net.artux.pda.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.di.AppComponent;
import net.artux.pda.di.ContextModule;
import net.artux.pda.di.DaggerAppComponent;
import net.artux.pda.repositories.QuestRepository;
import net.artux.pda.repositories.SummaryRepository;
import net.artux.pda.repositories.UserRepository;
import net.artux.pda.services.PdaAPI;
import net.artux.pda.ui.util.GsonProvider;
import net.artux.pdanetwork.api.DefaultApi;

import org.jetbrains.annotations.NotNull;

import dagger.Module;
import timber.log.Timber;


@Module
public class App extends Application {

    private AppComponent daggerAppComponent;
    static Gson gson;

    static DataManager sDataManager;

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

        sDataManager = new DataManager(getApplicationContext());
        //mRetrofitService.getRetrofit(sDataManager);
        daggerAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        gson = GsonProvider.getInstance();

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

    @NotNull
    public UserRepository getUserRepository() {
        return daggerAppComponent.userRepository();
    }

    @NotNull
    public QuestRepository getQuestRepository() {
        return daggerAppComponent.questRepository();
    }

    public SummaryRepository getSummaryRepository() {
        return daggerAppComponent.summaryRepository();
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

    public static DataManager getDataManager() {
        return sDataManager;
    }

    public DefaultApi getDefaultApi() {
        return daggerAppComponent.defaultApi();
    }

    public PdaAPI getOldApi() {
        return daggerAppComponent.oldApi();
    }
}
