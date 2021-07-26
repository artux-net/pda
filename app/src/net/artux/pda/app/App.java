package net.artux.pda.app;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.BiConsumer;

import timber.log.Timber;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.app.ActivityCompat.requestPermissions;


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
            createLogFile();
        } else {
            Timber.plant(new CrashReportingTree());
        }
        Timber.d("App started.");
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Timber.d("Ads initialization");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    initializationStatus.getAdapterStatusMap().forEach(new BiConsumer<String, AdapterStatus>() {
                        @Override
                        public void accept(String s, AdapterStatus adapterStatus) {
                            Timber.d(s + " : " + adapterStatus.getDescription() + " latency: " + adapterStatus.getLatency());
                        }
                    });
                }

            }
        });

    }

    private void createLogFile(){
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/logs";
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(fullPath, "pdaLogs.txt");

            if (!file.exists())
                file.createNewFile();
        }catch (IOException e){
            Log.println(Log.ERROR,"FileLogTree", "Error while create log file: ");
            e.printStackTrace();
        }
    }

    /** A tree which logs important information for crash reporting. */
    private class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
               // return;
            }
            if (tag!=null)
                message = tag + " : " + message;
            FirebaseCrashlytics.getInstance().log(message);
            System.out.println(message);
            if (t != null) {
                FirebaseCrashlytics.getInstance().recordException(t);
                t.printStackTrace();
            }


            try
            {
                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/logs";
                File file = new File(fullPath, "pdaLogs.txt");

                if (file.exists()) {
                    PrintStream printStream = new PrintStream(new FileOutputStream(file, true));
                    printStream.println(new Date() + " " + message);
                    if(t!=null)
                        t.printStackTrace(printStream);
                    printStream.close();
                }
            } catch (IOException e){}
        }
    }

    public static DataManager getDataManager() {
        return sDataManager;
    }

    public static RetrofitService getRetrofitService(){
        return mRetrofitService;
    }
}
