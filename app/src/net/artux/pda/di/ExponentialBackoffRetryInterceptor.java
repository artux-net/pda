package net.artux.pda.di;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ExponentialBackoffRetryInterceptor implements Interceptor {
    private final int maxRetries;
    private final long initialRetryDelayMillis;
    private final long maxRetryDelayMillis;

    public ExponentialBackoffRetryInterceptor(int maxRetries, long initialRetryDelayMillis, long maxRetryDelayMillis) {
        this.maxRetries = maxRetries;
        this.initialRetryDelayMillis = initialRetryDelayMillis;
        this.maxRetryDelayMillis = maxRetryDelayMillis;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = attemptRequest(chain, request);

        int tryCount = 0;
        long retryDelay = initialRetryDelayMillis;
        while (!response.isSuccessful() && tryCount < maxRetries) {
            tryCount++;
            try {
                TimeUnit.MILLISECONDS.sleep(retryDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Thread interrupted during retry delay", e);
            }
            response = attemptRequest(chain, request);

            // Экспоненциальное увеличение времени задержки с возможностью добавления случайного отклонения
            retryDelay = Math.min(maxRetryDelayMillis, retryDelay * 2);
        }

        if (!response.isSuccessful() && tryCount >= maxRetries) {
            throw new IOException("Request failed after " + maxRetries + " retries");
        }

        return response;
    }

    private Response attemptRequest(Chain chain, Request request) throws IOException {
        try {
            return chain.proceed(request);
        } catch (Exception e) {
            // NetworkModule's outer interceptor turns this into a synthetic
            // 503 response using only getMessage() (a Response can't carry
            // a Throwable/cause chain), so the real failure type has to be
            // in the message itself or it's lost - e.g. a SocketTimeoutException
            // or UnknownHostException would otherwise all look like the same
            // generic "Request attempt failed" in crash reports.
            throw new IOException("Request attempt failed: " + e, e);
        }
    }
}
