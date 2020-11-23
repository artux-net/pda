package net.artux.pda.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.PdaAPI;

import org.joda.time.Instant;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private PdaAPI mPdaAPI;
    private Gson gson;

    void initRetrofit(String url, final DataManager dataManager){
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.readTimeout(1, TimeUnit.MINUTES);
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("t", dataManager.getAuthToken())
                    .addHeader("ui", Locale.getDefault().getLanguage());
            System.out.println(dataManager.getAuthToken());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new GsonInstantAdapter())
                .setDateFormat("MMM dd, yyyy, hh:mm:ss a").create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://" + url)
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
