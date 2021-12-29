package net.artux.pda.ui.fragments.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.app.NotificationService;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.ui.activities.MainActivity;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.adapters.DialogsAdapter;
import net.artux.pdalib.Status;
import net.artux.pdalib.UserMessage;
import net.artux.pdalib.profile.items.GsonProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import timber.log.Timber;

public class DialogsFragment extends BaseFragment implements MessageListener {

    private DialogsAdapter dialogsAdapter;
    private FragmentListBinding binding;
    private WebSocket ws;
    private final Gson gson = GsonProvider.getInstance();
    private EchoWebSocketListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null)
            navigationPresenter.setTitle(getResources().getString(R.string.chat));

        dialogsAdapter = new DialogsAdapter((MainActivity) getActivity(), navigationPresenter);
        Type listType = new TypeToken<List<Dialog>>(){}.getType();
        List<Dialog> dialogs = gson.fromJson(App.getDataManager().getString("dialogs"), listType);
        if(dialogs!=null){
            binding.list.setVisibility(View.VISIBLE);
            binding.viewMessage.setVisibility(View.GONE);
            dialogsAdapter.setDialogs(dialogs);
        }

        binding.list.setAdapter(dialogsAdapter);

        /*Intent intent = new Intent(getActivity(), NotificationService.class).putExtra("t", App.getDataManager().getAuthToken());
        getActivity().startService(intent);*/

        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder();
        builder.addHeader("Authorization", App.getDataManager().getAuthToken());
        navigationPresenter.setLoadingState(true);

        builder.url(BuildConfig.WS_PROTOCOL +"://" + BuildConfig.URL_API + "dialogs ");
        navigationPresenter.setTitle("Chat");

        listener = new EchoWebSocketListener();
        ws = client.newWebSocket(builder.build(), listener);

        client.dispatcher().executorService().shutdown();

       /* Intent serviceIntent = new Intent(getActivity(), NotificationService.class);
        ServiceConnection sConn = new ServiceConnection() {

            public void onServiceConnected(ComponentName name, IBinder binder) {
                Timber.d( "MainActivity onServiceConnected");
                *//*myService = ((NotificationService.MyBinder) binder).getService();
                myService.setListener(DialogsFragment.this);*//*
                //bound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                Timber.d( "MainActivity onServiceDisconnected");
                //bound = false;
            }
        };*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ws.close(1000, null);

        binding = null;
        listener = null;
        ws.cancel();
    }

    @Override
    public void onDestroyView() {
        binding.list.setAdapter(null);
        super.onDestroyView();
    }

    private void updateAdapter(String text){
        if (getActivity()!=null)
            getActivity().runOnUiThread(() -> {
                Type listType = new TypeToken<ArrayList<Dialog>>(){}.getType();

                try {
                    UserMessage userMessage = App.getRetrofitService().getGson().fromJson(text,UserMessage.class);
                    if (userMessage.cid == -1) throw new JsonSyntaxException("");
                    dialogsAdapter.updateDialog(userMessage);
                    Timber.d("Dialogs, new message: " + userMessage.toString());
                }catch (JsonSyntaxException e){
                    try {
                        ArrayList<Dialog> list = gson.fromJson(text, listType);
                        if (list!=null) {
                            binding.list.setVisibility(View.VISIBLE);
                            binding.viewMessage.setVisibility(View.GONE);
                            Timber.d("Set dialogs");
                            dialogsAdapter.setDialogs(list);
                            App.getDataManager().setString("dialogs",gson.toJson(list));
                        }
                    }catch (JsonSyntaxException e1){
                        Timber.d("Unable to parse: " + text);
                        Status status = gson.fromJson(text, Status.class);
                        Toast.makeText(getActivity(), status.getDescription(), Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    @Override
    public void newMessage(UserMessage message) {

    }

    @Override
    public void setDialogs(List<Dialog> message) {

    }

    @Override
    public void newStatus(Status status) {

    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            Timber.d(response.toString());
            if (getActivity()!=null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (navigationPresenter!=null)
                            navigationPresenter.setLoadingState(false);
                    }
                });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            updateAdapter(text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Timber.d("WS - closing: %s", reason);
            if (getActivity()!=null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (navigationPresenter!=null)
                            navigationPresenter.setLoadingState(false);
                    }
                });
            webSocket.close(NORMAL_CLOSURE_STATUS, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            if (getActivity()!=null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (navigationPresenter!=null)
                            navigationPresenter.setLoadingState(false);
                    }
                });
            Timber.d("WS - closing because throwable: %s", t.getMessage());
        }
    }

}
