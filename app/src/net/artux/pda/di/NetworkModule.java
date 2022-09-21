package net.artux.pda.di;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.artux.pda.BuildConfig;
import net.artux.pda.api.PdaAPI;
import net.artux.pda.app.DataManager;
import net.artux.pdanetwork.ApiClient;
import net.artux.pdanetwork.api.DefaultApi;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
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

    @Provides
    @Singleton
    public OkHttpClient httpClient(DataManager dataManager) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original
                    .newBuilder()
                    .addHeader("locale", Locale.getDefault().getLanguage());

            if (dataManager.isAuthenticated()) {
                requestBuilder.addHeader("Authorization", dataManager.getAuthToken());
            }

            return chain.proceed(requestBuilder.build());
        });

        return httpClient.build();
    }

    @Provides
    @Singleton
    public Retrofit retrofit(OkHttpClient client, FirebaseRemoteConfig remoteConfig, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(remoteConfig.getString(RemoteValue.RESOURCE_URL))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public Map<String, Object> defaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(RemoteValue.BASEURL, BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API);
        defaults.put(RemoteValue.RESOURCE_URL, BuildConfig.PROTOCOL + "://" + BuildConfig.URL);
        return defaults;
    }

    @Provides
    @Singleton
    public FirebaseRemoteConfig remoteConfig(Map<String, Object> defaults) {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
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
    public ApiClient apiClient(OkHttpClient okHttpClient, FirebaseRemoteConfig remoteConfig) {
        ApiClient apiClient = new ApiClient();
        apiClient.configureFromOkclient(okHttpClient);

        String baseUrl = remoteConfig.getString(RemoteValue.BASEURL);
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
                        (json, typeOfT, context) -> {
                            if (json.toString().isEmpty() || json.toString().equals("{}"))
                                return null;
                            if (json instanceof JsonObject) {
                                JsonObject obj = json.getAsJsonObject();
                                return Instant.parse(obj.toString());
                            } else {
                                JsonPrimitive primitive = json.getAsJsonPrimitive();
                                return Instant.parse(primitive.getAsString());
                            }
                        })
                .registerTypeAdapter(Instant.class, new JsonSerializer<>() {
                    @Override
                    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
                        return context.serialize(src.toString());
                    }
                })
                .create();
    }

}
