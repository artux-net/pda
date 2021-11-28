package net.artux.pda.ui.fragments.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import net.artux.pda.R
import net.artux.pda.app.App
import net.artux.pda.databinding.FragmentProfileBinding
import net.artux.pda.ui.PdaAlertDialog
import net.artux.pda.ui.activities.hierarhy.BaseFragment
import net.artux.pda.ui.fragments.additional.AdditionalFragment
import net.artux.pda.ui.fragments.chat.ChatFragment
import net.artux.pda.ui.fragments.profile.adapters.GroupsAdapter
import net.artux.pda.ui.util.getViewModelFactory
import net.artux.pda.viewmodels.ProfileViewModel
import net.artux.pdalib.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileFragment : BaseFragment(), View.OnClickListener {

    private var binding: FragmentProfileBinding? = null
    private var groupsAdapter = GroupsAdapter()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navigationPresenter != null) {
            navigationPresenter.setTitle(getString(R.string.profile))
            navigationPresenter.setLoadingState(true)
        }

        if (arguments != null)
            profileViewModel.updateProfile(requireArguments().getInt("pdaId", viewModel.getId()))
        else
            profileViewModel.updateProfile(viewModel.getId())

        profileViewModel.profile.observe(viewLifecycleOwner) {
            if (it != null) {
                val binding = this.binding!!
                ProfileHelper.setAvatar(binding.profileAvatar, it.avatar)
                binding.profileLogin.text = it.login
                binding.profileGroup.text =
                    getString(
                        R.string.group_p, ProfileHelper.getGroup(
                            it,
                            context
                        )
                    )
                binding.profileLocation.text =
                    getString(R.string.location_p, it.location)
                binding.profileTime.text =
                    getString(R.string.in_zone_time_p, ProfileHelper.getDays(it))
                binding.profileRang.text =
                    getString(R.string.rang_p, ProfileHelper.getRang(it, view.context))
                binding.profileRating.text =
                    getString(R.string.rating_p, it.xp.toString())

                binding.profileFriends.text =
                    view.context.getString(R.string.friends, it.friends.toString())
                binding.profileFriends.setOnClickListener(this)
                binding.profileRequests.text =
                    view.context.getString(R.string.subscribers, it.subs.toString())
                binding.profileRequests.setOnClickListener(this)


                groupsAdapter.setRelations(it.relations)
                recyclerView = view.findViewById(R.id.list)
                recyclerView!!.adapter = groupsAdapter

                recyclerView!!.visibility = View.VISIBLE
                view.findViewById<View>(R.id.viewMessage).visibility = View.GONE

                val friendButton: Button = binding.profileFriend
                val subsButton: Button = binding.requests
                val messageButton: Button = binding.writeMessage
                messageButton.setOnClickListener(this)
                if (viewModel.getId() != it.pdaId) {
                    when (it.friendStatus) {
                        0 -> {
                            friendButton.setText(R.string.add_friend)
                            friendButton.setOnClickListener { view1: View? ->
                                val pdaAlertDialog = PdaAlertDialog(
                                    context,
                                    view as ViewGroup?,
                                    R.style.AlertDialogStyle
                                )
                                pdaAlertDialog.setTitle(R.string.add_friend_q)
                                pdaAlertDialog.setPositiveButton(
                                    R.string.okay
                                ) { _, _ ->
                                    App.getRetrofitService().pdaAPI.requestFriend(it.pdaId)
                                        .enqueue(object : Callback<Status?> {
                                            override fun onResponse(
                                                call: Call<Status?>,
                                                response: Response<Status?>
                                            ) {
                                                val status = response.body()
                                                if (status != null) Toast.makeText(
                                                    context,
                                                    status.description,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                profileViewModel.updateProfile(it.pdaId)
                                            }

                                            override fun onFailure(
                                                call: Call<Status?>,
                                                throwable: Throwable
                                            ) {
                                            }
                                        })
                                }
                                pdaAlertDialog.setNegativeButton(
                                    "No"
                                ) { _, _ -> }
                                pdaAlertDialog.show()
                            }
                        }
                        1 -> {
                            friendButton.text = getString(R.string.is_friend, it.name)
                            friendButton.setOnClickListener { view: View? ->
                                val pdaAlertDialog =
                                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                                pdaAlertDialog.setTitle(R.string.remove_friend_q)
                                pdaAlertDialog.setPositiveButton(
                                    R.string.okay
                                ) { dialogInterface, _ ->
                                    App.getRetrofitService().pdaAPI.requestFriend(it.pdaId)
                                        .enqueue(object : Callback<Status?> {
                                            override fun onResponse(
                                                call: Call<Status?>,
                                                response: Response<Status?>
                                            ) {
                                                val status = response.body()
                                                if (status != null) Toast.makeText(
                                                    context,
                                                    status.description,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                profileViewModel.updateProfile(it.pdaId)
                                            }

                                            override fun onFailure(
                                                call: Call<Status?>,
                                                throwable: Throwable
                                            ) {
                                            }
                                        })
                                }
                                pdaAlertDialog.setNegativeButton(
                                    "No"
                                ) { _, _ -> }
                                pdaAlertDialog.show()
                            }
                        }
                        2 -> {
                            friendButton.text = getString(R.string.is_sub, it.name)
                            friendButton.setOnClickListener { view: View? ->
                                val pdaAlertDialog =
                                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                                pdaAlertDialog.setTitle(R.string.add_friend_q)
                                pdaAlertDialog.setPositiveButton(
                                    R.string.okay
                                ) { dialogInterface, _ ->
                                    App.getRetrofitService().pdaAPI.requestFriend(it.pdaId)
                                        .enqueue(object : Callback<Status?> {
                                            override fun onResponse(
                                                call: Call<Status?>,
                                                response: Response<Status?>
                                            ) {
                                                val status = response.body()
                                                if (status != null) Toast.makeText(
                                                    context,
                                                    status.description,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                profileViewModel.updateProfile(it.pdaId)
                                            }

                                            override fun onFailure(
                                                call: Call<Status?>,
                                                throwable: Throwable
                                            ) {
                                            }
                                        })
                                }
                                pdaAlertDialog.setNegativeButton(
                                    "No"
                                ) { _, _ -> }
                                pdaAlertDialog.show()
                            }
                        }
                        3 -> {
                            friendButton.text = getString(R.string.requested)
                            friendButton.setOnClickListener { view: View? ->
                                val pdaAlertDialog =
                                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                                pdaAlertDialog.setTitle(R.string.cancel_friend_q)
                                pdaAlertDialog.setPositiveButton(
                                    R.string.okay
                                ) { dialogInterface, _ ->
                                    App.getRetrofitService().pdaAPI.requestFriend(it.pdaId)
                                        .enqueue(object : Callback<Status?> {
                                            override fun onResponse(
                                                call: Call<Status?>,
                                                response: Response<Status?>
                                            ) {
                                                val status = response.body()
                                                if (status != null) Toast.makeText(
                                                    context,
                                                    status.description,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                profileViewModel.updateProfile(it.pdaId)
                                            }

                                            override fun onFailure(
                                                call: Call<Status?>,
                                                throwable: Throwable
                                            ) {
                                            }
                                        })
                                }
                                pdaAlertDialog.setNegativeButton(
                                    "No"
                                ) { _, _ -> }
                                pdaAlertDialog.show()
                            }
                        }
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
        }
    }

    override fun onClick(p0: View?) {
        val friendsFragment = FriendsFragment()
        val bundle = Bundle()
        val profile = profileViewModel.profile.value

        when (p0!!.id) {
            R.id.profile_friends -> {
                bundle.putInt("type", 0)
                friendsFragment.arguments = bundle
                navigationPresenter.addFragment(friendsFragment, true)
            }
            R.id.profile_requests -> {
                bundle.putInt("type", 1)
                friendsFragment.arguments = bundle
                navigationPresenter.addFragment(friendsFragment, true)
            }
            R.id.write_message -> {
                bundle.putInt("to", profile!!.pdaId)
                val chatFragment = ChatFragment()
                chatFragment.arguments = bundle
                navigationPresenter.addFragment(chatFragment, true)
            }
        }
    }

    override fun receiveData(data: Bundle?) {
        super.receiveData(data)
        if (data?.containsKey("reset") == true) {
            profileViewModel.updateProfile(viewModel.getId())
        }
    }

    override fun onDestroyView() {
        recyclerView!!.adapter = null
        recyclerView = null
        binding = null
        super.onDestroyView()
    }

}