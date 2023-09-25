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
import net.artux.pda.model.chat.ChatUpdate;
import net.artux.pda.model.chat.UserMessage;
import net.artux.pda.model.user.SimpleUserModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.adapters.ChatAdapter;
import net.artux.pda.ui.fragments.news.ArticleFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.stories.StoriesFragment;
import net.artux.pda.utils.ObjectWebSocketListener;

import java.util.Timer;
import java.util.TimerTask;

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
    private ObjectWebSocketListener<ChatUpdate> userMessageObjectWebSocketListener;
    private final Timer timer = new Timer();

    public static ChatFragment with(SimpleUserModel userModel) {
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
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putBoolean("group", true);
        chatFragment.setArguments(bundle1);
        return chatFragment;
    }


    public static ChatFragment withConversation(ConversationModel conversation) {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("conversation", conversation.getId());
        chatFragment.setArguments(bundle1);
        return chatFragment;
    }

    public static BaseFragment asRPChat() {
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putBoolean("rp", true);
        chatFragment.setArguments(bundle1);
        return chatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        net.artux.pda.databinding.FragmentChatBinding binding = FragmentChatBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!viewModel.isChatAllowed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.PDADialogStyle);
            builder.setMessage(R.string.chatLimit);
            builder.setNegativeButton(R.string.okay, (dialogInterface, i) -> navigationPresenter.addFragment(new StoriesFragment(), true));
            builder.setOnCancelListener(dialogInterface ->
                    navigationPresenter.addFragment(new StoriesFragment(), true));
            builder.show();
        }


        mRecyclerView = view.findViewById(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mChatAdapter = new ChatAdapter(this);
        mRecyclerView.setAdapter(mChatAdapter);

        mInputEditText = view.findViewById(R.id.inputMessage);

        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        userMessageObjectWebSocketListener = new ObjectWebSocketListener<>(ChatUpdate.class, gson, new ObjectWebSocketListener.OnUpdateListener<>() {
            @Override
            public void onOpen() {
                navigationPresenter.setLoadingState(false);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ws.send("");
                    }
                }, 1000 * 60 * 2);
            }

            @Override
            public void onMessage(ChatUpdate update) {
                if (!isDetached())
                    requireActivity().runOnUiThread(() -> {
                        mChatAdapter.update(update);
                        if (mChatAdapter.getItemCount() > 0)
                            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
                    });
            }

            @Override
            public void onClose() {
                navigationPresenter.setLoadingState(false);
                timer.purge();
                timer.cancel();
            }
        });

        Bundle args = getArguments();
        Request.Builder builder = new Request.Builder();
        String path = BuildConfig.WS_PROTOCOL + "://" + BuildConfig.URL_API;
        navigationPresenter.setLoadingState(true);
        if (args != null) {
            if (args.containsKey("rp")) {
                builder.url(path + "rp");
                navigationPresenter.setTitle("RP chat");
            } else if (args.containsKey("group")) {
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
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(null);
        if (ws != null)
            ws.close(1000, "Closed by user");
        super.onDestroy();
    }

    @Override
    public void onClick(UserMessage message) {
        if (navigationPresenter != null
                && message.getAuthor().getId() != null)
            navigationPresenter.addFragment(UserProfileFragment
                    .of(message.getAuthor().getId()), true);
    }

    @Override
    public void onLongClick(UserMessage message) {
        if (message.getAuthor().getId() == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.PDADialogStyle);
        builder.setTitle(getString(R.string.any_select_action));
        builder.setItems(getResources().getStringArray(R.array.message_actions), (dialogInterface, i) -> {
            switch (i) {
                default:
                    String login = "@" + message.getAuthor().getLogin() + ", ";
                    String input = mInputEditText.getText().toString();
                    if (!input.startsWith("@")) {
                        mInputEditText.getText().insert(0, login);
                        return;
                    }

                    String textToReplace = input;
                    for (int j = 0; j < input.length(); j++) {
                        if (Character.isSpaceChar(input.charAt(j))) {
                            textToReplace = input.substring(0, j);
                            break;
                        }
                    }
                    input = input.replaceFirst(textToReplace, "@" + message.getAuthor().getLogin() + ", ");
                    mInputEditText.setText(input);
                case 1:
                    navigationPresenter.addFragment(ChatFragment.with(message.getAuthor()), true);
                case 2:
                    navigationPresenter.addFragment(UserProfileFragment.of(message.getAuthor().getId()), true);
            }
        });
        builder.create().show();

    }

    @Override
    public void onLinkClick(String url) {
        navigationPresenter.addFragment(ArticleFragment.of("Переход по ссылке", url), true);
    }

}
