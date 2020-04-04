package net.artux.pda.app;

import net.artux.pda.PdaAPI;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private Retrofit mRetrofit;

    private PdaAPI mPdaAPI;

    public void initRetrofit(String url, final DataManager dataManager){
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.readTimeout(1, TimeUnit.MINUTES);
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("t", dataManager.getAuthToken())
                        .addHeader("ui", Locale.getDefault().getLanguage());

                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        });

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://" + url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        mPdaAPI = mRetrofit.create(PdaAPI.class);


    }

    public PdaAPI getPdaAPI(){
        return mPdaAPI;
    }



}
