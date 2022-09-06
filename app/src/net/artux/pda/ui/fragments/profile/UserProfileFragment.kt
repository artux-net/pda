package net.artux.pda.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import net.artux.pda.R
import net.artux.pda.databinding.FragmentProfileBinding
import net.artux.pda.ui.PdaAlertDialog
import net.artux.pda.ui.activities.hierarhy.BaseFragment
import net.artux.pda.ui.fragments.additional.AdditionalFragment
import net.artux.pda.ui.fragments.chat.ChatFragment
import net.artux.pda.ui.fragments.profile.adapters.GroupRelationsAdapter
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper
import net.artux.pda.ui.util.getViewModelFactory
import net.artux.pda.ui.viewmodels.ProfileViewModel
import net.artux.pda.utils.GroupHelper
import java.util.*

class UserProfileFragment : BaseFragment(), View.OnClickListener {

    private var binding: FragmentProfileBinding? = null
    private var groupRelationsAdapter =
        GroupRelationsAdapter()
    private var recyclerView: RecyclerView? = null

    private val profileViewModel: ProfileViewModel by viewModels { getViewModelFactory() }

    init {
        defaultAdditionalFragment = AdditionalFragment::class.java
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding!!.root
    }

    companion object {
        @JvmStatic
        fun of(uuid: UUID): UserProfileFragment {
            val profileFragment = UserProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable("pdaId", uuid)
            profileFragment.arguments = bundle
            return profileFragment;
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navigationPresenter != null) {
            navigationPresenter.setTitle(getString(R.string.profileModel))
            navigationPresenter.setLoadingState(true)
        }

        profileViewModel.profile.observe(viewLifecycleOwner) {
            val model = it

            val binding = this.binding!!
            ProfileHelper.setAvatar(binding.profileAvatar, model.avatar)
            binding.profileLogin.text = model.login
            binding.profileGroup.text = GroupHelper.getTitle(model.gang, context)
            binding.profileTime.text =
                getString(R.string.in_zone_time_p, ProfileHelper.getDays(model))
            binding.profileRang.text =
                getString(R.string.rang_p, ProfileHelper.getRang(model, view.context))
            binding.profileRating.text =
                getString(R.string.rating_p, model.xp.toString())

            binding.profileFriends.text =
                view.context.getString(R.string.friends, model.friends.toString())
            binding.profileFriends.setOnClickListener(this)
            binding.profileRequests.text =
                view.context.getString(R.string.subscribers, model.subs.toString())
            binding.profileRequests.setOnClickListener(this)


            groupRelationsAdapter.setRelations(model.relations)
            recyclerView = view.findViewById(R.id.list)
            recyclerView!!.adapter = groupRelationsAdapter

            recyclerView!!.visibility = View.VISIBLE
            view.findViewById<View>(R.id.viewMessage).visibility = View.GONE

            val friendButton: Button = binding.profileFriend
            val subsButton: Button = binding.requests
            val messageButton: Button = binding.writeMessage
            messageButton.setOnClickListener(this)
            if (viewModel.getId() != model.id) {
                friendButton.setText(R.string.add_friend)
                friendButton.text = getString(R.string.is_friend, model.name)
                friendButton.text = getString(R.string.is_sub,model.name)
                friendButton.text = getString(R.string.requested)
                friendButton.setOnClickListener { view1: View? ->
                    val pdaAlertDialog = PdaAlertDialog(
                        requireContext(),
                        view as ViewGroup?,
                        R.style.AlertDialogStyle
                    )
                    pdaAlertDialog.setTitle(R.string.add_friend_q)
                    pdaAlertDialog.setPositiveButton(
                        R.string.okay
                    ) { _, _ ->
                        viewModel.requestFriend(model.id)
                    }
                    pdaAlertDialog.setNegativeButton(
                        "No"
                    ) { _, _ -> }
                    pdaAlertDialog.show()
                }

                friendButton.visibility = View.VISIBLE
                messageButton.visibility = View.VISIBLE
                subsButton.visibility = View.GONE
            } else {
                friendButton.visibility = View.GONE
                messageButton.visibility = View.GONE
                subsButton.visibility = View.VISIBLE
            }
        }
        if (arguments != null)
            profileViewModel.updateProfile(requireArguments().getSerializable("pdaId") as UUID)
        else
            profileViewModel.updateProfile(viewModel.getId())
    }

    override fun onClick(p0: View?) {
        val friendsFragment = FriendsFragment()
        val bundle = Bundle()
        val result = profileViewModel.profile.value

        when (p0!!.id) {
            R.id.profile_friends -> {
                bundle.putInt("type", 0)
                bundle.putInt("pdaId", result!!.pdaId)
                friendsFragment.arguments = bundle
                navigationPresenter.addFragment(friendsFragment, true)
            }
            R.id.profile_requests -> {
                bundle.putInt("type", 1)
                bundle.putInt("pdaId", result!!.pdaId)
                friendsFragment.arguments = bundle
                navigationPresenter.addFragment(friendsFragment, true)
            }
            R.id.write_message -> {
                bundle.putInt("to", result!!.pdaId)
                val chatFragment = ChatFragment()
                chatFragment.arguments = bundle
                navigationPresenter.addFragment(chatFragment, true)
            }
        }
    }

    override fun onDestroyView() {
        recyclerView?.adapter = null
        recyclerView = null
        binding = null
        super.onDestroyView()
    }

}