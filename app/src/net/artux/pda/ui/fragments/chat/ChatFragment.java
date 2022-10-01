package net.artux.pda.ui.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.DataManager;
import net.artux.pda.databinding.FragmentChatBinding;
import net.artux.pda.model.ConversationModel;
import net.artux.pda.model.UserMessage;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.adapters.ChatAdapter;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.stories.StoriesFragment;
import net.artux.pda.ui.util.ObjectWebSocketListener;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import timber.log.Timber;

@AndroidEntryPoint
public class ChatFragment extends BaseFragment implements View.OnClickListener, ChatAdapter.MessageClickListener {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Gson gson;
    @Inject
    protected OkHttpClient client;

    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private TextInputEditText mInputEditText;
    private WebSocket ws;
    private FragmentChatBinding binding;
    private ObjectWebSocketListener<UserMessage> userMessageObjectWebSocketListener;

    static ChatFragment with(UserModel userModel) {
        ChatFragment chatFragment1 = new ChatFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("user", userModel.getId());
        chatFragment1.setArguments(bundle1);
        return chatFragment1;
    }

    public static ChatFragment asCommonChat() {
        return new ChatFragment();
    }

    public static ChatFragment asGroupChat() {
        ChatFragment chatFragment1 = new ChatFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putBoolean("group", true);
        chatFragment1.setArguments(bundle1);
        return new ChatFragment();
    }


    public static ChatFragment withConversation(ConversationModel conversation) {
        ChatFragment chatFragment1 = new ChatFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("conversation", conversation.getId());
        chatFragment1.setArguments(bundle1);
        return new ChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!viewModel.isChatAllowed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
            builder.setMessage("Чтобы получить доступ к чату, нужно пройти один из сюжетов.");
            builder.setNegativeButton(R.string.okay, (dialogInterface, i) -> navigationPresenter.addFragment(new StoriesFragment(), true));
            builder.setOnCancelListener(dialogInterface ->
                    navigationPresenter.addFragment(new StoriesFragment(), true));
            builder.show();
        }

        userMessageObjectWebSocketListener = new ObjectWebSocketListener<>(UserMessage.class, gson, new ObjectWebSocketListener.OnUpdateListener<>() {
            @Override
            public void onOpen() {
                navigationPresenter.setLoadingState(false);
            }

            @Override
            public void onMessage(UserMessage userMessage) {
                requireActivity().runOnUiThread(() -> {
                    mChatAdapter.addMessage(userMessage);
                    mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
                });

            }

            @Override
            public void onList(List<UserMessage> list) {
                requireActivity().runOnUiThread(() -> mChatAdapter.setItems(list));
            }

            @Override
            public void onClose() {
                navigationPresenter.setLoadingState(false);
            }
        });

        mRecyclerView = view.findViewById(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mChatAdapter = new ChatAdapter(this);
        mRecyclerView.setAdapter(mChatAdapter);

        mInputEditText = view.findViewById(R.id.inputMessage);

        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);


        Bundle args = getArguments();
        Request.Builder builder = new Request.Builder();
        String path = BuildConfig.WS_PROTOCOL + "://" + BuildConfig.URL_API;
        navigationPresenter.setLoadingState(true);
        if (args != null) {
            if (args.containsKey("group")) {
                builder.url(path + "groups");
                navigationPresenter.setTitle("Group chat");
            } else {
                if (args.containsKey("c")) {
                    builder.url(path + "dialog"
                            + "?c=" + getArguments().getSerializable("c"));
                    System.out.println(getArguments().getInt("c"));
                    navigationPresenter.setTitle("Chat");
                } else if (args.containsKey("to")) {
                    builder.url(path + "dialog"
                            + "?to=" + getArguments().getSerializable("to"));
                    navigationPresenter.setTitle("Dialog with #" + getArguments().getInt("to"));
                } else {
                    builder.url(path + "chat");
                    navigationPresenter.setTitle("Chat");
                }
            }
        } else {
            builder.url(path + "chat");
            navigationPresenter.setTitle("Chat");
        }

        ws = client.newWebSocket(builder.build(), userMessageObjectWebSocketListener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendButton) {
            if (mInputEditText.getText() != null &&
                    !mInputEditText.getText().toString().equals("")) {
                if (ws.send(mInputEditText.getText().toString())) {
                    mInputEditText.setText("");
                } else {
                    Timber.e("Could not send: %s", ws.request().toString());
                    Timber.e("Text: %s", mInputEditText.getText().toString());
                    Toast.makeText(getContext(), "Unable to send message", Toast.LENGTH_SHORT).show();
                }
            }
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
        if (navigationPresenter != null)
            navigationPresenter.addFragment(UserProfileFragment
                    .of(message.getAuthor().getId()), true);
    }

    @Override
    public void onLongClick(UserMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
        builder.setTitle("Выберете действие");
        //todo
        builder.setItems(new String[]{"Перейти к диалогу", "Посмотреть профиль"}, (dialogInterface, i) -> {
            switch (i){
                default:
                    navigationPresenter.addFragment(ChatFragment.with(message.getAuthor()), true);
                case 1:
                    navigationPresenter.addFragment(UserProfileFragment.of(message.getAuthor().getId()), true);
            }
        });
        builder.create().show();
    }

}
