package net.artux.pda.ui.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.databinding.FragmentDialogsBinding
import net.artux.pda.databinding.FragmentListBinding
import net.artux.pda.ui.activities.hierarhy.BaseFragment
import net.artux.pda.ui.fragments.chat.adapters.DialogsAdapter
import net.artux.pda.ui.viewmodels.ConversationsViewModel
import net.artux.pdanetwork.model.ConversationDTO

@AndroidEntryPoint
class DialogsFragment : BaseFragment(), DialogsAdapter.OnClickListener {
    private lateinit var dialogsAdapter: DialogsAdapter
    private lateinit var listBinding: FragmentListBinding
    private lateinit var binding: FragmentDialogsBinding
    private lateinit var conversationsViewModel: ConversationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogsBinding.inflate(inflater)
        listBinding = binding.listContainer
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conversationsViewModel = ViewModelProvider(requireActivity())[ConversationsViewModel::class.java]

        dialogsAdapter = DialogsAdapter(this)
        listBinding.list.adapter = dialogsAdapter

        if (navigationPresenter !== null) {
            navigationPresenter.setTitle(resources.getString(R.string.chat))
            navigationPresenter.setLoadingState(true)
        }

        binding.commonChatBtn.setOnClickListener {
            navigationPresenter.addFragment(ChatFragment.asCommonChat(), true)
        }

        binding.groupChatBtn.setOnClickListener {
            navigationPresenter.addFragment(ChatFragment.asGroupChat(), true)
        }

        binding.rpChatBtn.setOnClickListener {
            navigationPresenter.addFragment(ChatFragment.asRPChat(), true)
        }

        conversationsViewModel.conversations.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                listBinding.list.visibility = View.VISIBLE
                listBinding.viewMessage.visibility = View.GONE
                dialogsAdapter.setDialogs(it)
            } else {
                listBinding.list.visibility = View.GONE
                listBinding.viewMessage.visibility = View.VISIBLE
            }
        }
        conversationsViewModel.update()
    }

    override fun onClick(model: ConversationDTO?) {
        navigationPresenter.addFragment(ChatFragment.asConversationChat(model), true)
    }

    override fun onLongClick(model: ConversationDTO?): Boolean {
        val builder = AlertDialog.Builder(requireContext(), R.style.PDADialogStyle)
        builder.setTitle(getString(R.string.delete_chat, model?.title))
        builder.setPositiveButton(R.string.yes) { dialog, _ ->
            model?.id?.let { conversationsViewModel.delete(it) }
        }
        builder.setNegativeButton(R.string.no) { _, _ -> }

        builder.create().show()

        return true
    }
}