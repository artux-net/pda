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
import net.artux.pda.models.Status
import net.artux.pda.repositories.util.Result
import net.artux.pda.ui.PdaAlertDialog
import net.artux.pda.ui.activities.hierarhy.BaseFragment
import net.artux.pda.ui.fragments.additional.AdditionalFragment
import net.artux.pda.ui.fragments.chat.ChatFragment
import net.artux.pda.ui.fragments.profile.adapters.GroupRelationsAdapter
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper
import net.artux.pda.ui.util.getViewModelFactory
import net.artux.pda.ui.viewmodels.ProfileViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navigationPresenter != null) {
            navigationPresenter.setTitle(getString(R.string.profileModel))
            navigationPresenter.setLoadingState(true)
        }

        profileViewModel.profile.observe(viewLifecycleOwner) {
            if (it is Result.Success) {
                val model = it.data

                val binding = this.binding!!
                ProfileHelper.setAvatar(binding.profileAvatar, model.avatar)
                binding.profileLogin.text = model.login
                binding.profileGroup.text = model.gang.getTitle(context)
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
                if (viewModel.getId() != model.pdaId) {
                    when (model.friendStatus) {
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
                                    App.getRetrofitService().pdaAPI.requestFriend(model.pdaId)
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
                                                profileViewModel.updateProfile(model.pdaId)
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
                            friendButton.text = getString(R.string.is_friend,model.name)
                            friendButton.setOnClickListener { view: View? ->
                                val pdaAlertDialog =
                                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                                pdaAlertDialog.setTitle(R.string.remove_friend_q)
                                pdaAlertDialog.setPositiveButton(
                                    R.string.okay
                                ) { dialogInterface, _ ->
                                    App.getRetrofitService().pdaAPI.requestFriend(model.pdaId)
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
                                                profileViewModel.updateProfile(model.pdaId)
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
                            friendButton.text = getString(R.string.is_sub,model.name)
                            friendButton.setOnClickListener { view: View? ->
                                val pdaAlertDialog =
                                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                                pdaAlertDialog.setTitle(R.string.add_friend_q)
                                pdaAlertDialog.setPositiveButton(
                                    R.string.okay
                                ) { dialogInterface, _ ->
                                    App.getRetrofitService().pdaAPI.requestFriend(model.pdaId)
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
                                                profileViewModel.updateProfile(model.pdaId)
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
                                    App.getRetrofitService().pdaAPI.requestFriend(model.pdaId)
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
                                                profileViewModel.updateProfile(model.pdaId)
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
        /*if (arguments != null)
            profileViewModel.updateProfile(requireArguments().getInt("pdaId", viewModel.getId()))
        else
            profileViewModel.updateProfile(viewModel.getId())*/
    }

    override fun onClick(p0: View?) {
        val friendsFragment = FriendsFragment()
        val bundle = Bundle()
        val result = profileViewModel.profile.value
        if (result is Result.Success) {
            val profile = result.data

            when (p0!!.id) {
                R.id.profile_friends -> {
                    bundle.putInt("type", 0)
                    bundle.putInt("pdaId", profile!!.pdaId)
                    friendsFragment.arguments = bundle
                    navigationPresenter.addFragment(friendsFragment, true)
                }
                R.id.profile_requests -> {
                    bundle.putInt("type", 1)
                    bundle.putInt("pdaId", profile!!.pdaId)
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