package net.artux.pda.di;

import static okhttp3.Protocol.HTTP_2;

import android.os.LocaleList;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.artux.pda.BuildConfig;
import net.artux.pda.app.DataManager;
import net.artux.pda.common.PropertyFields;
import net.artux.pdanetwork.ApiClient;
import net.artux.pdanetwork.JSON;
import net.artux.pdanetwork.api.DefaultApi;
import net.artux.pdanetwork.model.StoryInfoLocale;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

@Module(includes = AppModule.class)
@InstallIn({SingletonComponent.class})
public class NetworkModule {

    @Provides
    @Singleton
    public OkHttpClient httpClient(DataManager dataManager) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original
                    .newBuilder()
                    .addHeader("Accept-Language", LocaleList.getDefault().toLanguageTags());

            if (dataManager.isAuthenticated()) {
                requestBuilder.addHeader("Authorization", dataManager.getAuthToken());
            }

            try {
                Timber.d("Request: %s", chain.request().toString());

                return chain.proceed(requestBuilder.build());
            } catch (Exception e) {
                Timber.w(e);
                return new Response.Builder()
                        .protocol(HTTP_2)
                        .request(requestBuilder.build())
                        .code(503)
                        .message(e.getMessage() == null ? "PDANET Unavailable" : e.getMessage())
                        .body(ResponseBody.create(MediaType.get("application/json"), "{}"))
                        .build();
            }

        });

        // Must be an application interceptor: it retries by calling chain.proceed()
        // multiple times in a loop, which OkHttp only allows for application
        // interceptors - a *network* interceptor is required to call proceed() exactly
        // once per exchange, so registering this via addNetworkInterceptor made every
        // single retry attempt throw "must call proceed() exactly once" and get reported
        // back as a synthetic 503 - the backoff/retry logic never actually worked.
        httpClient.addInterceptor(new ExponentialBackoffRetryInterceptor(4, 1000, 32000));


        httpClient.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);

        return httpClient.build();
    }

    @Provides
    @Singleton
    public Retrofit retrofit(OkHttpClient client, FirebaseRemoteConfig remoteConfig, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(remoteConfig.getString(PropertyFields.RESOURCE_URL))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public Map<String, Object> defaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put(PropertyFields.API_URL, BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API);
        defaults.put(PropertyFields.RESOURCE_URL, BuildConfig.PROTOCOL + "://" + BuildConfig.URL);
        defaults.put(PropertyFields.XP_CHAT_LIMIT, 26L);
        return defaults;
    }

    @Provides
    @Singleton
    public FirebaseRemoteConfig remoteConfig(Map<String, Object> defaults) {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings;
        if (BuildConfig.DEBUG)
            configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(0)
                    .build();
        else
            configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();

        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(defaults);

        remoteConfig.fetch().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                remoteConfig.activate();
                Timber.i("Обновлен Firebase - конфиг");
            } else
                Timber.e("Не удалось обновить Firebase - конфиг");
        });
        return remoteConfig;
    }

    /**
     * The backend serializes java.util.Locale fields (e.g. StoryInfo.locale) as a plain
     * string ("ru"), but the OpenAPI spec describes it as an object (from reflecting over
     * Locale's getters), so the generated StoryInfoLocale model expects BEGIN_OBJECT. That
     * mismatch
     * crashed the whole app with an IllegalStateException while parsing /stories. Since
     * StoryInfoLocale isn't used anywhere in the app, we just skip the string form instead
     * of failing the entire response.
     */
    private static final TypeAdapterFactory LENIENT_STORY_LOCALE_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != StoryInfoLocale.class) {
                return null;
            }
            TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.STRING) {
                        in.skipValue();
                        return null;
                    }
                    return delegate.read(in);
                }
            };
        }
    };

    @Provides
    @Singleton
    public ApiClient apiClient(OkHttpClient okHttpClient, FirebaseRemoteConfig remoteConfig) {
        ApiClient apiClient = new ApiClient();
        apiClient.configureFromOkclient(okHttpClient);

        // Retrofit picks the first converter factory that can handle a type, and
        // ApiClient's default adapterBuilder already carries its own (unfixable, generated)
        // Gson converter first - appending ours after it would never be reached. Replace the
        // whole builder instead, keeping the same base URL placeholder createDefaultAdapter()
        // used; it's overridden below regardless. Layer our factory on top of a fresh JSON()'s
        // Gson (via newBuilder()) rather than a bare GsonBuilder, so we keep its Date/
        // OffsetDateTime/LocalDate adapters instead of silently losing them.
        Gson lenientGson = new JSON().getGson().newBuilder()
                .registerTypeAdapterFactory(LENIENT_STORY_LOCALE_FACTORY)
                .create();
        apiClient.setAdapterBuilder(
                new Retrofit.Builder()
                        .baseUrl("https://app.artux.net/pdanetwork/")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create(lenientGson))
        );

        String baseUrl = remoteConfig.getString(PropertyFields.API_URL);
        if (BuildConfig.DEBUG)
            apiClient.getAdapterBuilder().baseUrl(BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API);
        else if (!baseUrl.equals(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_STRING)) {
            apiClient.getAdapterBuilder().baseUrl(baseUrl);
            Timber.i("BaseUrl was changed: %s", baseUrl);
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
                .registerTypeAdapter(Instant.class, (JsonSerializer<Object>)
                        (src, typeOfSrc, context) -> context.serialize(src.toString()))
                .create();
    }

}
