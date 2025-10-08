package net.artux.pda.utils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import okhttp3.WebSocket;
import timber.log.Timber;

public class ObjectWebSocketListener<T> extends okhttp3.WebSocketListener {

    private final OnUpdateListener<T> onUpdateListener;
    private final Gson gson;
    private final Class<T> clazz;

    public ObjectWebSocketListener(Class<T> clazz, Gson gson, OnUpdateListener<T> onUpdateListener) {
        this.gson = gson;
        this.onUpdateListener = onUpdateListener;
        this.clazz = clazz;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, okhttp3.Response response) {
        Timber.d("WebSocket opened with response %s", response.toString());
        onUpdateListener.onOpen();
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Timber.d("WebSocket got message %s", text);

        try {
            T t = gson.fromJson(text, clazz);
            onUpdateListener.onMessage(t);
        } catch (JsonSyntaxException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Timber.d("WebSocket closing with code %d: %s", code, reason);
        int NORMAL_CLOSURE_STATUS = 1000;
        webSocket.close(NORMAL_CLOSURE_STATUS, reason);
        onUpdateListener.onClose();
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
        Timber.d(t, "WS - closing because throwable: %s", t.getMessage());
        onUpdateListener.onClose();
    }

    public interface OnUpdateListener<T> {
        void onOpen();

        void onMessage(T t);

        void onClose();
    }

}
