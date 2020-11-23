package net.artux.pda.views.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.activities.BaseFragment;
import net.artux.pda.app.App;
import net.artux.pda.views.additional.InfoFragment;
import net.artux.pdalib.LimitedArrayList;
import net.artux.pdalib.UserMessage;

import java.lang.reflect.Type;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;

    private TextInputEditText mInputEditText;

    private WebSocket ws;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navigationPresenter.addAdditionalFragment(new InfoFragment());

        mRecyclerView = view.findViewById(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mChatAdapter = new ChatAdapter(getActivity(), navigationPresenter);
        mRecyclerView.setAdapter(mChatAdapter);

        mInputEditText = view.findViewById(R.id.inputMessage);
        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        OkHttpClient client = new OkHttpClient();
        gson = new Gson();
        Request.Builder builder = new Request.Builder();
        builder.addHeader("t", App.getDataManager().getAuthToken());
        Bundle args = getArguments();

        if (args != null) {
            if (args.containsKey("group")) {
                builder.url("ws://" + BuildConfig.URL + "/groupChat/*/*");
                navigationPresenter.setTitle("Group chat");
            } else {
                if (args.containsKey("c")) {
                    builder.url("ws://" + BuildConfig.URL + "/dialog"
                            + "?c=" + getArguments().getInt("c"));
                    navigationPresenter.setTitle("Chat");
                }
                else if (args.containsKey("to")) {
                    builder.url("ws://" + BuildConfig.URL + "/dialog"
                            + "?to=" + getArguments().getInt("to"));
                    navigationPresenter.setTitle("Dialog with #" + getArguments().getInt("to"));
                }
                else {
                    builder.url("ws://" + BuildConfig.URL + "/chat/*");
                    navigationPresenter.setTitle("Chat");
                }
            }
        } else {
            builder.url("ws://" + BuildConfig.URL + "/chat/*");
            navigationPresenter.setTitle("Chat");
        }

        EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(builder.build(), listener);

        client.dispatcher().executorService().shutdown();
}

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendButton) {
            if (mInputEditText.getText()!=null &&
                    !mInputEditText.getText().toString().equals("")) {
                if (ws.send(mInputEditText.getText().toString())) {
                    mInputEditText.setText("");
                }else {
                    System.out.println(ws.request().toString());
                    Toast.makeText(getContext(), "Unable to send message", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateAdapter(final String text){
        if (getActivity()!=null)
        getActivity().runOnUiThread(() -> {
            System.out.println(text);

            Type listType = new TypeToken<LimitedArrayList<UserMessage>>(){}.getType();

            try {
                LimitedArrayList<UserMessage> list = gson.fromJson(text, listType);
                mChatAdapter.setItems(list);
            }catch (JsonSyntaxException e){
                mChatAdapter.addMessage(App.getRetrofitService().getGson().fromJson(text,UserMessage.class));
                mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ws.close(1000, "Closed by user");
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            System.out.println(response.toString());
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            updateAdapter(text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            System.out.println("WS - closing: " + reason);
            webSocket.close(NORMAL_CLOSURE_STATUS, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        }
    }

}
