package net.artux.pda.di;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import net.artux.pda.BuildConfig;
import net.artux.pda.app.DataManager;
import net.artux.pda.services.PdaAPI;
import net.artux.pdanetwork.ApiClient;
import net.artux.pdanetwork.api.DefaultApi;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module(includes = AppModule.class)
@InstallIn({SingletonComponent.class})
public class NetworkModule {

    private static final String CONFIG_BASEURL = "baseUrl";

    @Provides
    @Singleton
    public Retrofit retrofit(GsonConverterFactory factory) {
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("ui", Locale.getDefault().getLanguage());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.PROTOCOL + "://" + BuildConfig.URL)
                .addConverterFactory(factory)
                .client(httpClient.build())
                .build();
    }

    @Provides
    @Singleton
    public FirebaseRemoteConfig remoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        Map<String, Object> defaults =
                Map.of(CONFIG_BASEURL, BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API);

        remoteConfig.setDefaultsAsync(defaults);
        remoteConfig.fetch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                remoteConfig.activate();
                Timber.i("Remote config fetched.");
            } else
                Timber.e("Unable to fetch remote config.");
        });
        return remoteConfig;
    }

    @Provides
    @Singleton
    public ApiClient apiClient(FirebaseRemoteConfig remoteConfig, DataManager dataManager) {
        String login = dataManager.getLogin();
        String pass = dataManager.getPass();

        ApiClient apiClient = new ApiClient("basicAuth", login, pass);
        String baseUrl = remoteConfig.getString(CONFIG_BASEURL);
        if (!baseUrl.equals(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_STRING)) {
            apiClient.getAdapterBuilder().baseUrl(baseUrl);
            Timber.d("BaseUrl was changed: %s", baseUrl);
        } else
            Timber.d("Default server url was used.");
        return apiClient;
    }

    @Provides
    @Singleton
    public DefaultApi getDefaultApi(ApiClient apiClient) {
        return apiClient.createService(DefaultApi.class);
    }

    @Provides
    @Singleton
    public PdaAPI getPdaAPI(Retrofit retrofit) {
        return retrofit.create(PdaAPI.class);
    }

    @Provides
    public Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>)
                        (json, typeOfT, context) -> Instant.parse(json.getAsJsonPrimitive().getAsString()))
                .create();
    }

    @Provides
    public GsonConverterFactory getGsonFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }


}
