package net.artux.pda.Views.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.artux.pda.BuildConfig;
import net.artux.pda.Models.LimitedArrayList;
import net.artux.pda.Models.Member;
import net.artux.pda.Models.UserMessage;
import net.artux.pda.R;
import net.artux.pda.app.App;

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

    private View mainView;

    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;

    private TextInputEditText mInputEditText;

    private WebSocket ws;
    private Gson mGson;

    private Member user = App.getDataManager().getMember();

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
            Button sendButton = mainView.findViewById(R.id.sendButton);
            sendButton.setOnClickListener(this);

            OkHttpClient client = new OkHttpClient();
            mGson = new Gson();
            Request request;
            Bundle args= getArguments();

            if (args!=null){
                if (args.containsKey("group")){
                    request = new Request.Builder().url("ws://" + BuildConfig.URL + "/groupChat")
                            .addHeader("t", App.getDataManager().getAuthToken())
                            .build();
                }else{
                    int type = args.getInt("type",0);
                    if (type == 0)
                        request = new Request.Builder().url("ws://" + BuildConfig.URL + "/dialog"
                                + "?to=" + getArguments().getInt("to"))
                                .addHeader("t", App.getDataManager().getAuthToken())
                                .build();
                    else if (type == 1)
                        request = new Request.Builder().url("ws://" + BuildConfig.URL + "/dialog"
                                + "?c=" + getArguments().getInt("c"))
                                .addHeader("t", App.getDataManager().getAuthToken())
                                .build();
                    else
                        request = new Request.Builder().url("ws://" + BuildConfig.URL + "/chat")
                                .addHeader("t", App.getDataManager().getAuthToken())
                                .build();
                }
            }else{
                request = new Request.Builder().url("ws://" + BuildConfig.URL + "/chat")
                        .addHeader("t", App.getDataManager().getAuthToken())
                        .build();
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

    private void updateAdapter(final String text){
        getActivity().runOnUiThread(() -> {
            System.out.println(text);

            Type listType = new TypeToken<LimitedArrayList<UserMessage>>(){}.getType();

            try {
                LimitedArrayList<UserMessage> list = mGson.fromJson(text, listType);
                mChatAdapter.setItems(list);
            }catch (JsonSyntaxException e){
                mChatAdapter.addMessage(mGson.fromJson(text,UserMessage.class));
                mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
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
