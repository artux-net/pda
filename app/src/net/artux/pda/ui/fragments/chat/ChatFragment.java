package net.artux.pda.ui.fragments.chat;

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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.PDAApplication;
import net.artux.pda.databinding.FragmentChatBinding;
import net.artux.pda.model.UserMessage;
import net.artux.pda.ui.PdaAlertDialog;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.adapters.ChatAdapter;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import timber.log.Timber;

public class ChatFragment extends BaseFragment implements View.OnClickListener, ChatAdapter.MessageClickListener {

    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;

    private TextInputEditText mInputEditText;

    private WebSocket ws;
    private Gson gson = new Gson();

    private FragmentChatBinding binding;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        mRecyclerView = view.findViewById(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mChatAdapter = new ChatAdapter(this);
        mRecyclerView.setAdapter(mChatAdapter);

        mInputEditText = view.findViewById(R.id.inputMessage);

        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder();
        builder.addHeader("Authorization",((PDAApplication)getActivity().getApplication()).getDataManager().getAuthToken());
        Bundle args = getArguments();
        String path = BuildConfig.WS_PROTOCOL + "://" + BuildConfig.URL_API + "pdanetwork/";
        navigationPresenter.setLoadingState(true);
        if (args != null) {
            if (args.containsKey("group")) {
                builder.url(path + "groups");
                navigationPresenter.setTitle("Group chat");
            } else {
                if (args.containsKey("c")) {
                    builder.url(path + "dialog"
                            + "?c=" + getArguments().getInt("c"));
                    System.out.println(getArguments().getInt("c"));
                    navigationPresenter.setTitle("Chat");
                }
                else if (args.containsKey("to")) {
                    builder.url(path + "dialog"
                            + "?to=" + getArguments().getInt("to"));
                    navigationPresenter.setTitle("Dialog with #" + getArguments().getInt("to"));
                }
                else {
                    builder.url(path + "chat");
                    navigationPresenter.setTitle("Chat");
                }
            }
        } else {
            builder.url(path + "chat");
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
                        Timber.e("Could not send: %s", ws.request().toString());
                        Timber.e("Text: %s", mInputEditText.getText().toString());
                        Toast.makeText(getContext(), "Unable to send message", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    private void updateAdapter(String text){
        try {
            if (getActivity()!=null)
                getActivity().runOnUiThread(() -> {
                    Type listType = new TypeToken<ArrayList<UserMessage>>(){}.getType();

                    try {
                        UserMessage userMessage = new Gson().fromJson(text,UserMessage.class);
                        mChatAdapter.addMessage(userMessage);
                        mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()-1);
                    }catch (JsonSyntaxException e){
                        mChatAdapter.clearItems();
                        ArrayList<UserMessage> list = gson.fromJson(text, listType);
                        mChatAdapter.setItems(list);
                    }
                });
        }catch (Exception ignored){
            ignored.fillInStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
        ws.close(1000, "Closed by user");
    }

    @Override
    public void onClick(UserMessage message) {
        UserProfileFragment profileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("pdaId", message.getPdaId());
        profileFragment.setArguments(bundle);
        if (navigationPresenter!=null)
            navigationPresenter.addFragment(profileFragment, true);
    }

    @Override
    public void onLongClick(UserMessage message) {
        PdaAlertDialog builder = new PdaAlertDialog(requireContext(), binding.getRoot());
        builder.addButton("Перейти к диалогу", view12 -> {
            ChatFragment chatFragment1 = new ChatFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putLong("to", message.getPdaId());
            chatFragment1.setArguments(bundle1);
            navigationPresenter.addFragment(chatFragment1, true);
        });
        builder.addButton("Посмотреть профиль", view1 -> {
            UserProfileFragment profileFragment = new UserProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("pdaId", message.getPdaId());
            profileFragment.setArguments(bundle);
            if (navigationPresenter!=null)
                navigationPresenter.addFragment(profileFragment, true);
        });
        builder.show();
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Timber.d(webSocket.request().toString());
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
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
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
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
           if (getActivity()!=null)
               getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (navigationPresenter!=null)
                        navigationPresenter.setLoadingState(false);
                }
            });

            Timber.e(t);
        }
    }

}
