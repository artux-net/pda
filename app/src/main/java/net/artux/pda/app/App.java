package net.artux.pda.app;

import android.app.Application;
import android.content.Context;

import net.artux.pda.R;


public class App extends Application {

    static RetrofitService mRetrofitService = new RetrofitService();
    static DataManager sDataManager;
    static Context mContext;
    public static int[] avatars = new int[]{
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

    public static String URL = "192.168.1.106:8080";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        sDataManager = new DataManager();
        mRetrofitService.initRetrofit(URL , sDataManager);
    }

    public void setURL(String url){
        URL = url;
    }

    public static DataManager getDataManager() {
        return sDataManager;
    }

    public static Context getContext() {
        return mContext;
    }

    public static RetrofitService getRetrofitService(){
        return mRetrofitService;
    }
}
