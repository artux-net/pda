package net.artux.pda.ui.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.user.ProfileModel;
import net.artux.pda.model.user.SimpleUserModel;
import net.artux.pda.model.user.UserRelation;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.profile.adapters.FriendsAdapter;
import net.artux.pda.ui.viewmodels.ProfileViewModel;

import java.util.List;
import java.util.UUID;

public class FriendsFragment extends BaseFragment {

    private FragmentListBinding listBinding;
    private FriendsAdapter friendsAdapter;
    private ProfileViewModel profileViewModel;
    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    static FriendsFragment of(ProfileModel profileModel, ListType type){
        FriendsFragment friendsFragment = new FriendsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id", profileModel.getId());
        bundle.putSerializable("type", type);
        bundle.putString("nickname", profileModel.getLogin());
        friendsFragment.setArguments(bundle);
        return friendsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listBinding = FragmentListBinding.inflate(inflater, container, true);
        return listBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(profileViewModel==null)
            profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        listBinding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsAdapter = new FriendsAdapter(navigationPresenter);
        listBinding.list.setAdapter(friendsAdapter);
        profileViewModel.getFriends().observe(getViewLifecycleOwner(), new Observer<List<SimpleUserModel>>() {
            @Override
            public void onChanged(List<SimpleUserModel> simpleUserModels) {
                if (simpleUserModels.size()>0){
                    listBinding.viewMessage.setVisibility(View.GONE);
                    listBinding.list.setVisibility(View.VISIBLE);
                    friendsAdapter.setData(simpleUserModels);
                }else{
                    listBinding.viewMessage.setVisibility(View.VISIBLE);
                    listBinding.list.setVisibility(View.GONE);
                }
            }
        });

        if (getArguments() != null) {
            UUID pdaId = (UUID) getArguments().getSerializable("id");
            ListType type = (ListType) getArguments().getSerializable("type");
            String title = getArguments().getString("nickname");
            navigationPresenter.setTitle(getString(R.string.friends_of, title));
            updateList(pdaId, type);
        };

    }

    void updateList(UUID pdaId, ListType type){
        switch (type){
            case SUBS:
                profileViewModel.updateFriends(pdaId, UserRelation.SUBSCRIBER);
                break;
            case FRIENDS:
                profileViewModel.updateFriends(pdaId, UserRelation.FRIEND);
                break;
            case MYREQUESTS:
                profileViewModel.updateMyRequests();
                break;
        }
    }

    enum ListType{
        FRIENDS,
        SUBS,
        MYREQUESTS
    }
}
