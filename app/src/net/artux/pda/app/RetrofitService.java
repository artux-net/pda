package net.artux.pda.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.PdaAPI;
import net.artux.pda.ui.activities.hierarhy.MainContract;

import org.joda.time.Instant;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RetrofitService {

    private PdaAPI mPdaAPI;
    private Gson gson;
    MainContract.View view;

    void initRetrofit(final DataManager dataManager){
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.writeTimeout(2, TimeUnit.MINUTES);
//        httpClient.callTimeout(2, TimeUnit.MINUTES);
        httpClient.readTimeout(2, TimeUnit.MINUTES);
        httpClient.connectTimeout(65, TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("t", dataManager.getAuthToken())
                    .addHeader("ui", Locale.getDefault().getLanguage());
            Timber.d("RetrofitService: Request with token - %s", dataManager.getAuthToken());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        httpClient.eventListener(new EventListener() {
            @Override
            public void callStart(Call call) {
                super.callStart(call);
                if (view!=null)
                    view.setLoadingState(true);
            }

            @Override
            public void callEnd(Call call) {
                super.callEnd(call);
                if (view!=null)
                    view.setLoadingState(false);
            }
        });


        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(Instant.class, new GsonInstantAdapter())
                .setDateFormat("MMM dd, yyyy, hh:mm:ss a").create();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://" + BuildConfig.URL_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        mPdaAPI = mRetrofit.create(PdaAPI.class);

    }

    public void attachView(MainContract.View view){
        this.view = view;
    }

    public PdaAPI getPdaAPI(){
        return mPdaAPI;
    }

    public Gson getGson() {
        return gson;
    }
}
