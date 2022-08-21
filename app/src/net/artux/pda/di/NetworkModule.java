package net.artux.pda.di;

import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.app.DataManager;
import net.artux.pda.services.PdaAPI;
import net.artux.pdanetwork.api.DefaultApi;

import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides
    public Retrofit getRetrofit(GsonConverterFactory factory, DataManager dataManager){
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("ui", Locale.getDefault().getLanguage())
                    .addHeader("Authorization", dataManager.getAuthToken());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.PROTOCOL+ "://" + BuildConfig.URL_API)
                .addConverterFactory(factory)
                .client(httpClient.build())
                .build();

        return retrofit;
    }

    @Provides
    public DefaultApi getDefaultApi(Retrofit retrofit) {
        return retrofit.create(DefaultApi.class);
    }

    @Provides
    public PdaAPI getPdaAPI(Retrofit retrofit){
        return retrofit.create(PdaAPI.class);
    }

    @Provides
    public Gson getGson() {
        return new Gson();
    }

    @Provides
    public GsonConverterFactory getGsonFactory(Gson gson){
        return GsonConverterFactory.create(gson);
    }


}
