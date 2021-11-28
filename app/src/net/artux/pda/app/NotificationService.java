package net.artux.pda.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.fragments.chat.Dialog;
import net.artux.pda.ui.fragments.chat.MessageListener;
import net.artux.pdalib.Status;
import net.artux.pdalib.UserMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class NotificationService extends Service {

    WebSocket ws;
    Gson gson = new Gson();
    MessageListener listener = null;
    MyBinder binder = new MyBinder();
    ArrayList<Dialog> list = new ArrayList<>();

    public void onCreate() {
        super.onCreate();
        Timber.d( "MyService onCreate");
    }

    public void onDestroy() {
        super.onDestroy();
        Timber.d("MyService onDestroy");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d( "MyService onStartCommand");

        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder();
        builder.addHeader("t", App.getDataManager().getAuthToken());

        builder.url("wss://" + BuildConfig.URL_API + "dialogs/*");

        EchoWebSocketListener listener = new EchoWebSocketListener();
        //ws = client.newWebSocket(builder.build(), listener);

        client.dispatcher().executorService().shutdown();

        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void setListener(MessageListener listener){
        this.listener = listener;
    }

    public void updateDialog(UserMessage message){
        for (Dialog d : list) {
            if (d.id == message.cid)
                d.lastMessage = message.senderLogin + ": " + message.message;
        }
    }

    public void parseResponse(final String text){
        Type listType = new TypeToken<ArrayList<Dialog>>(){}.getType();

        try {
           /* UserMessage userMessage = App.getRetrofitService().getGson().fromJson(text,UserMessage.class);
            if (userMessage.cid == -1) throw new JsonSyntaxException("");
            if (listener!=null) listener.newMessage(userMessage);
            updateDialog(userMessage);
            if (!userMessage.senderLogin.equals(App.getDataManager().getMember().getLogin()))
                sendNotif(userMessage);
            Timber.d("Dialogs, new message: " + userMessage.toString());*/
        }catch (JsonSyntaxException e){
            try {
                ArrayList<Dialog> list = gson.fromJson(text, listType);
                Timber.d("Set dialogs");
                this.list = list; //dialogsAdapter.setDialogs(list);
            }catch (JsonSyntaxException e1){
                Timber.d("Unable to parse: " + text);
                Status status = gson.fromJson(text, Status.class);
                Toast.makeText(getApplicationContext(), status.getDescription(), Toast.LENGTH_LONG).show();
            }
        }

    }

    void sendNotif(UserMessage message) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(message.senderLogin)
                        .setContentText(message.message);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(message.cid, notification);
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            Timber.d(response.toString());
            //load false
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {

            parseResponse(text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Timber.d("WS - closing: %s", reason);
            //load false
            webSocket.close(NORMAL_CLOSURE_STATUS, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
           //load false
            Timber.e(t);
        }
    }

    public class MyBinder extends Binder {
       public NotificationService getService() {
            return NotificationService.this;
        }
    }
}