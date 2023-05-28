package net.artux.pda.ui.fragments.profile

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.databinding.FragmentListBinding
import net.artux.pda.databinding.FragmentProfileBinding
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.ui.activities.hierarhy.BaseFragment
import net.artux.pda.ui.fragments.additional.AdditionalFragment
import net.artux.pda.ui.fragments.chat.ChatFragment
import net.artux.pda.ui.fragments.profile.adapters.GroupRelationsAdapter
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper
import net.artux.pda.ui.viewmodels.ProfileViewModel
import net.artux.pda.utils.GroupHelper
import java.util.*

@AndroidEntryPoint
class UserProfileFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var listBinding: FragmentListBinding
    private lateinit var groupRelationsAdapter: GroupRelationsAdapter

    private lateinit var profileViewModel: ProfileViewModel

    init {
        defaultAdditionalFragment = AdditionalFragment::class.java
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        listBinding = binding.listContainer
        return binding.root
    }

    companion object {
        @JvmStatic
        fun of(uuid: UUID): UserProfileFragment {
            val profileFragment = UserProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable("pdaId", uuid)
            profileFragment.arguments = bundle
            return profileFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        groupRelationsAdapter = GroupRelationsAdapter()
        if (navigationPresenter != null) {
            navigationPresenter.setTitle(getString(R.string.profileModel))
            navigationPresenter.setLoadingState(true)
        }

        profileViewModel.profile.observe(viewLifecycleOwner) {
            val model = it

            ProfileHelper.setAvatar(binding.profileAvatar, model.avatar)
            binding.profileNickname.text = "${model.name} ${model.nickname}"
            binding.profileLogin.text = "${model.login} #${model.pdaId}"
            binding.profileRole.text = "${model.role}"
            val onlineStatus = if(model.isOnline())
                getString(R.string.online)
            else
                getString(R.string.offline)

            binding.profileStatus.text = onlineStatus
            binding.profileGroup.text = GroupHelper.getTitle(model.gang, context)
            binding.profileTime.text =
                getString(R.string.in_zone_time_p, ProfileHelper.getDays(model))
            binding.profileRang.text =
                getString(R.string.rang_p, ProfileHelper.getRangTitleByXp(model.xp, view.context))
            binding.profileRating.text =
                getString(R.string.xp_p, model.xp.toString())

            binding.profilePosition.text =
                getString(R.string.position_p, model.ratingPosition.toString())

            val currentRang = StoryDataModel.getRang(model.xp)
            val nextRang = currentRang.nextRang

            binding.currentRangTextView.text =
                ProfileHelper.getRangTitleById(currentRang.id, view.context)
            binding.xpProgressBar.min = currentRang.xp
            if (nextRang != null) {
                binding.nextRangTextView.text =
                    ProfileHelper.getRangTitleById(nextRang.id, view.context)
                binding.xpProgressBar.max = nextRang.xp
                binding.xpProgressBar.progress = model.xp
            }

            binding.profileFriends.text =
                view.context.getString(R.string.friends, model.friends.toString())
            binding.profileFriends.setOnClickListener(this)
            binding.profileRequests.text =
                view.context.getString(R.string.subscribers, model.subs.toString())
            binding.profileRequests.setOnClickListener(this)

            binding.profileRequests.visibility = View.GONE
            binding.profileFriends.visibility = View.GONE


            groupRelationsAdapter.setRelations(model.relations)
            val recyclerView = listBinding.list
            recyclerView.adapter = groupRelationsAdapter

            recyclerView.visibility = View.VISIBLE
            view.findViewById<View>(R.id.viewMessage).visibility = View.GONE

            binding.profileFriend.visibility = View.GONE
            binding.requests.visibility = View.GONE
            binding.writeMessage.visibility = View.GONE

            /*val friendButton: Button = binding.profileFriend
            val subsButton: Button = binding.requests
            val messageButton: Button = binding.writeMessage
            messageButton.setOnClickListener(this)
            if (viewModel.getId() != model.id) {
                friendButton.setText(R.string.add_friend)
                friendButton.text = getString(R.string.is_friend, model.name)
                friendButton.text = getString(R.string.is_sub, model.name)
                friendButton.text = getString(R.string.requested)
                friendButton.setOnClickListener {
                    val pdaAlertDialog = AlertDialog.Builder(
                        requireContext(),
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
            }*/
        }
        if (arguments != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                profileViewModel.updateProfile(
                    requireArguments().getSerializable(
                        "pdaId",
                        UUID::class.java
                    )
                )
            } else
                profileViewModel.updateProfile(requireArguments().getSerializable("pdaId") as UUID)
        else
            profileViewModel.updateProfile(viewModel.getId())
    }

    override fun onClick(p0: View?) {

        val bundle = Bundle()
        val result = profileViewModel.profile.value
        when (p0!!.id) {
            R.id.profile_friends -> {
                navigationPresenter.addFragment(
                    FriendsFragment.of(
                        result,
                        FriendsFragment.ListType.FRIENDS
                    ), true
                )
            }
            R.id.profile_requests -> {
                navigationPresenter.addFragment(
                    FriendsFragment.of(
                        result,
                        FriendsFragment.ListType.SUBS
                    ), true
                )
            }
            R.id.write_message -> {
                bundle.putInt("to", result!!.pdaId)
                val chatFragment = ChatFragment()
                chatFragment.arguments = bundle
                navigationPresenter.addFragment(chatFragment, true)
            }
        }
    }

}