package net.artux.pda.app;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;

import timber.log.Timber;


public class App extends Application {

    static RetrofitService mRetrofitService = new RetrofitService();
    static DataManager sDataManager;
    public static int[] avatars = {
            R.drawable.a1,
            R.drawable.a2,
            R.drawable.a3,
            R.drawable.a4,
            R.drawable.a5,
            R.drawable.a6,
            R.drawable.a7,
            R.drawable.a8,
            R.drawable.a9,
            R.drawable.a10,
            R.drawable.a11,
            R.drawable.a12,
            R.drawable.a13,
            R.drawable.a14,
            R.drawable.a15,
            R.drawable.a16,
            R.drawable.a17,
            R.drawable.a18,
            R.drawable.a19,
            R.drawable.a20,
            R.drawable.a21,
            R.drawable.a22,
            R.drawable.a23,
            R.drawable.a24,
            R.drawable.a25,
            R.drawable.a26,
            R.drawable.a27,
            R.drawable.a28,
            R.drawable.a29,
            R.drawable.a30,
            R.drawable.a0
    };

    public static int[] group_avatars = {
            R.drawable.g0,
            R.drawable.g1,
            R.drawable.g2,
            R.drawable.g3,
            R.drawable.g4,
            R.drawable.g5,
            R.drawable.g6,
            R.drawable.g6,//8
            R.drawable.g6//9
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sDataManager = new DataManager(getApplicationContext());
        mRetrofitService.initRetrofit(sDataManager);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        Timber.d("App started.");
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (tag!=null)
                message = tag + " : " + message;
            FirebaseCrashlytics.getInstance().log(message);
            System.out.println(message);
            if (t != null) {
                FirebaseCrashlytics.getInstance().recordException(t);
                t.printStackTrace();
            }
        }
    }

    public static DataManager getDataManager() {
        return sDataManager;
    }

    public static RetrofitService getRetrofitService(){
        return mRetrofitService;
    }
}
