package net.artux.pda.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.app.App;
import net.artux.pda.app.DataManager;
import net.artux.pda.app.GsonInstantAdapter;

import org.joda.time.Instant;

import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RetrofitService {

    private PdaAPI mPdaAPI;
    private Gson gson;

    public void initRetrofit(final DataManager dataManager){
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
