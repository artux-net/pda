package net.artux.pda.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.PdaAPI;
import net.artux.pda.ui.activities.hierarhy.MainContract;

import org.joda.time.Instant;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dagger.Component;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RetrofitService {

    private PdaAPI mPdaAPI;
    private Gson gson;

    void initRetrofit(final DataManager dataManager){
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("t", dataManager.getAuthToken())
                    .addHeader("ui", Locale.getDefault().getLanguage())
                    .addHeader("Authorization", App.getDataManager().getAuthToken());
            Timber.d("RetrofitService: Request with token - %s", dataManager.getAuthToken());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });



        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(Instant.class, new GsonInstantAdapter())
                .setDateFormat("MMM dd, yyyy, hh:mm:ss a").create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.PROTOCOL+ "://" + BuildConfig.URL_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        mPdaAPI = mRetrofit.create(PdaAPI.class);

    }

    public PdaAPI getPdaAPI(){
        return mPdaAPI;
    }

    public Gson getGson() {
        return gson;
    }
}
