package net.artux.pda.ui.util;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.WebSocket;
import timber.log.Timber;

public class ObjectWebSocketListener<T> extends okhttp3.WebSocketListener {

    private final OnUpdateListener<T> onUpdateListener;
    private final Gson gson;
    private final Class<T> clazz;

    private final Type listType;

    public ObjectWebSocketListener(Class<T> clazz, Gson gson, OnUpdateListener<T> onUpdateListener) {
        this.gson = gson;
        this.onUpdateListener = onUpdateListener;
        this.clazz = clazz;
        listType = new TypeToken<List<T>>() {
        }.getType();
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
            JsonElement element = JsonParser.parseString(text);
            if (element.isJsonArray()) {
                List<T> list = gson.fromJson(text, listType);
                onUpdateListener.onList(list);
            } else {
                T t = gson.fromJson(text, clazz);
                onUpdateListener.onMessage(t);
            }
        } catch (JsonSyntaxException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Timber.d("WebSocket closing with code %i: %s", code, reason);
        int NORMAL_CLOSURE_STATUS = 1000;
        webSocket.close(NORMAL_CLOSURE_STATUS, reason);
        onUpdateListener.onClose();
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, okhttp3.Response response) {
        Timber.e(t, "WS - closing because throwable: %s", t.getMessage());
        onUpdateListener.onClose();
    }

    public interface OnUpdateListener<T> {
        void onOpen();

        void onMessage(T t);

        void onList(List<T> list);

        void onClose();
    }

}
