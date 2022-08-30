package net.artux.pda.di;

import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.app.DataManager;
import net.artux.pda.services.PdaAPI;
import net.artux.pdanetwork.ApiClient;
import net.artux.pdanetwork.api.DefaultApi;

import java.util.Locale;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn({SingletonComponent.class})
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit retrofit(GsonConverterFactory factory, DataManager dataManager) {
        //todo remove
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

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API)
                .addConverterFactory(factory)
                .client(httpClient.build())
                .build();
    }

    @Provides
    @Singleton
    public ApiClient apiClient(DataManager dataManager){
        String login = dataManager.getLogin();
        String pass = dataManager.getPass();
        return new ApiClient("basicAuth", login, pass);
    }

    @Provides
    @Singleton
    public DefaultApi getDefaultApi(ApiClient apiClient) {
        return apiClient.createService(DefaultApi.class);
    }

    @Provides
    @Singleton
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
