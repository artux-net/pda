package net.artux.pda.ui.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.databinding.FragmentDialogsBinding;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.ConversationModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.adapters.DialogsAdapter;
import net.artux.pda.ui.util.ObjectWebSocketListener;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.Request;
import okhttp3.WebSocket;

@AndroidEntryPoint
public class DialogsFragment extends BaseFragment implements DialogsAdapter.OnClickListener {

    @Inject
    protected Gson gson;
    private DialogsAdapter dialogsAdapter;
    private FragmentListBinding listBinding;
    private FragmentDialogsBinding binding;
    private WebSocket ws;
    private ObjectWebSocketListener<ConversationModel> listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDialogsBinding.inflate(inflater);
        listBinding = binding.listContainer;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter != null)
            navigationPresenter.setTitle(getResources().getString(R.string.chat));

        listener = new ObjectWebSocketListener<>(ConversationModel.class, gson, new ObjectWebSocketListener.OnUpdateListener<>() {
            @Override
            public void onOpen() {
                navigationPresenter.setLoadingState(false);
            }

            @Override
            public void onMessage(ConversationModel conversationModel) {

            }

            @Override
            public void onClose() {
                navigationPresenter.setLoadingState(false);
            }
        });

        dialogsAdapter = new DialogsAdapter(this);
        listBinding.list.setAdapter(dialogsAdapter);

        navigationPresenter.setLoadingState(true);

        Request request = new Request.Builder()
                .url(BuildConfig.WS_PROTOCOL + "://" + BuildConfig.URL_API + "dialogs ")
                .build();

        navigationPresenter.setTitle("Chat");

        //ws = client.newWebSocket(request, listener);


        binding.commonChatBtn.setOnClickListener(view1 ->
                navigationPresenter.addFragment(ChatFragment.asCommonChat(), true));

        binding.groupChatBtn.setOnClickListener(view1 ->
                navigationPresenter.addFragment(ChatFragment.asGroupChat(), true));

        binding.addChatBtn.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
            builder.setTitle(R.string.any_select_action);
            builder.setItems(getResources().getStringArray(R.array.dialogs_actions), (dialogInterface, i) -> {
                //todo
            });
            builder.show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listBinding = null;
        listener = null;
    }

    @Override
    public void onDestroyView() {
        listBinding.list.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onClick(ConversationModel model) {
        navigationPresenter.addFragment(ChatFragment.withConversation(model), true);
    }
}
