package net.artux.pda.ui.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentDialogsBinding;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.chat.adapters.DialogsAdapter;
import net.artux.pda.ui.viewmodels.ConversationsViewModel;
import net.artux.pdanetwork.model.ConversationDTO;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DialogsFragment extends BaseFragment implements DialogsAdapter.OnClickListener {
    private DialogsAdapter dialogsAdapter;
    private FragmentListBinding listBinding;
    private FragmentDialogsBinding binding;
    private ConversationsViewModel conversationsViewModel;

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

        if (conversationsViewModel == null)
            conversationsViewModel = new ViewModelProvider(requireActivity()).get(ConversationsViewModel.class);


        dialogsAdapter = new DialogsAdapter(this);
        listBinding.list.setAdapter(dialogsAdapter);

        navigationPresenter.setLoadingState(true);
        navigationPresenter.setTitle("Chat");

        binding.commonChatBtn.setOnClickListener(view1 ->
                navigationPresenter.addFragment(ChatFragment.asCommonChat(), true));

        binding.groupChatBtn.setOnClickListener(view1 ->
                navigationPresenter.addFragment(ChatFragment.asGroupChat(), true));

        binding.rpChatBtn.setOnClickListener(view1 ->
                navigationPresenter.addFragment(ChatFragment.asRPChat(), true));

//        binding.conversationChatBtn.setOnClickListener(view1 -> {
//            ConversationDTO conversation = new ConversationDTO();
//            conversation.setId(UUID.fromString("51040783-b5c4-4388-aa3d-808cbd3f5950"));
//            conversation.setTitle("Беседа с Максимом");
//            navigationPresenter.addFragment(ChatFragment.asConversationChat(conversation), true);
//        });

        conversationsViewModel.getConversations().observe(getViewLifecycleOwner(), conversationDTOList -> {
            if (conversationDTOList.size() > 0) {
                listBinding.list.setVisibility(View.VISIBLE);
                listBinding.viewMessage.setVisibility(View.GONE);
                dialogsAdapter.setDialogs(conversationDTOList);
            } else {
                listBinding.list.setVisibility(View.GONE);
                listBinding.viewMessage.setVisibility(View.VISIBLE);
            }
        });
        conversationsViewModel.update();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listBinding = null;
    }

    @Override
    public void onDestroyView() {
        listBinding.list.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onClick(ConversationDTO model) {
        navigationPresenter.addFragment(ChatFragment.asConversationChat(model), true);
    }
}
