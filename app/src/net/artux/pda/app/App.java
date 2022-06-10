package net.artux.pda.app;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.map.model.Map;
import net.artux.pda.repositories.Cache;
import net.artux.pda.repositories.QuestRepository;
import net.artux.pda.repositories.SummaryRepository;
import net.artux.pda.repositories.UserRepository;
import net.artux.pda.services.RetrofitService;
import net.artux.pda.ui.fragments.quest.models.Chapter;
import net.artux.pdalib.Member;
import net.artux.pdalib.Profile;
import net.artux.pdalib.Summary;
import net.artux.pdalib.profile.items.GsonProvider;

import org.jetbrains.annotations.NotNull;

import dagger.Module;
import timber.log.Timber;


@Module
public class App extends Application {

    static RetrofitService mRetrofitService = new RetrofitService();
    static Gson gson;
    static Cache<Profile> profileCache;
    static Cache<Member> memberCache;
    static Cache<Chapter> chapterCache;
    static Cache<Map> mapCache;
    static Cache<Summary> summaryCache;
    static UserRepository userRepository;
    static QuestRepository questRepository;
    static SummaryRepository summaryRepository;
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
        mRetrofitService.initRetrofit(sDataManager);
        gson = GsonProvider.getInstance();

        profileCache = new Cache<>(Profile.class, getApplicationContext(), gson);
        memberCache = new Cache<>(Member.class, getApplicationContext(), gson);
        chapterCache = new Cache<>(Chapter.class, getApplicationContext(), gson);
        mapCache = new Cache<>(Map.class, getApplicationContext(), gson);
        summaryCache = new Cache<>(Summary.class, getApplicationContext(), gson);

        userRepository = new UserRepository(mRetrofitService.getPdaAPI(), profileCache, memberCache);
        questRepository = new QuestRepository(mRetrofitService.getPdaAPI(), chapterCache, mapCache);
        summaryRepository = new SummaryRepository(summaryCache);

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        else
            Timber.plant(new CrashReportingTree());

        Timber.d("App started.");
        MobileAds.initialize(this, initializationStatus -> {
            Timber.d("Ads initialization");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                initializationStatus.getAdapterStatusMap().forEach((s, adapterStatus) ->
                        Timber.d(s + " : " + adapterStatus.getDescription() + " latency: " + adapterStatus.getLatency()));
            }

        });

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @NotNull
    public UserRepository getUserRepository() {
        return userRepository;
    }

    @NotNull
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
                logBuilder.append(t.toString()).append("\n");
                FirebaseCrashlytics.getInstance().recordException(t);
                t.printStackTrace();
            }
        }
    }

    public static DataManager getDataManager() {
        return sDataManager;
    }

    public static RetrofitService getRetrofitService() {
        return mRetrofitService;
    }
}
