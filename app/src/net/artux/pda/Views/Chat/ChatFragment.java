package net.artux.pda.Views.Chat;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.artux.pda.Models.LimitedArrayList;
import net.artux.pda.Models.Member;
import net.artux.pda.Models.UserMessage;
import net.artux.pda.R;
import net.artux.pda.app.App;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatFragment extends Fragment implements View.OnClickListener{

    View mainView;

    RecyclerView mRecyclerView;
    ChatAdapter mChatAdapter;

    TextInputEditText mInputEditText;
    Button sendButton;

    WebSocket ws;
    Gson mGson;

    Member user = App.getDataManager().getMember();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(mainView==null){
            mainView = inflater.inflate(R.layout.fragment_chat, container,false);

            mRecyclerView = mainView.findViewById(R.id.recycleView);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(manager);
            mChatAdapter = new ChatAdapter(getActivity());
            mRecyclerView.setAdapter(mChatAdapter);

            mInputEditText = mainView.findViewById(R.id.inputMessage);
            sendButton = mainView.findViewById(R.id.sendButton);
            sendButton.setOnClickListener(this);

            OkHttpClient client = new OkHttpClient();
            mGson = new Gson();
            Request request;

            switch (getArguments().getInt("chatMode",0)){
                default:
                    request = new Request.Builder().url("ws://" + App.URL + "/chat"
                            + "?t=" + App.getDataManager().getAuthToken())
                            .build();
                    break;
                case 1:
                    request = new Request.Builder().url("ws://" + App.URL + "/dialog"
                            + "?t=" + App.getDataManager().getAuthToken()
                            + "&toPdaId=" + getArguments().getInt("toPdaId"))
                            .build();
                    break;
                case 2:
                    request = new Request.Builder().url("ws://" + App.URL + "/groupChat"
                            + "?t=" + App.getDataManager().getAuthToken()
                            + "&group=" + getArguments().getInt("group"))
                            .build();
                    break;

            }

            //TODO: many params from arguments

            EchoWebSocketListener listener = new EchoWebSocketListener();
            ws = client.newWebSocket(request, listener);


            client.dispatcher().executorService().shutdown();
        }

        return mainView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendButton:
                if(!mInputEditText.getText().toString().equals("")) {
                    UserMessage userMessage = new UserMessage();
                    userMessage.senderLogin = user.getLogin();
                    userMessage.message = mInputEditText.getText().toString();
                    userMessage.avatarId = Integer.parseInt(user.getAvatarId());
                    userMessage.groupId = user.getGroup();
                    userMessage.pdaId = user.getPdaId();

                    SimpleDateFormat dateFormatGmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));

                    userMessage.time = dateFormatGmt.format(new Date());

                    if (ws.send(mGson.toJson(userMessage))) {
                        mInputEditText.setText("");
                    }
                }
                break;
        }
    }

    void updateAdapter(final String text){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                System.out.println(text);

                Type listType = new TypeToken<LimitedArrayList<UserMessage>>(){}.getType();

                try {
                    LimitedArrayList<UserMessage> list = mGson.fromJson(text, listType);
                    mChatAdapter.setItems(list);
                }catch (JsonSyntaxException e){
                    mChatAdapter.addMessage(mGson.fromJson(text,UserMessage.class));
                    mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
                }
            }
        });
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            updateAdapter(text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        }
    }

}
