package net.artux.pda.ui.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.models.profile.FriendModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.profile.adapters.FriendsAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class FriendsFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        navigationPresenter.setTitle("Friends");
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsAdapter = new FriendsAdapter(navigationPresenter);
        recyclerView.setAdapter(friendsAdapter);


        if (getArguments() != null) {
            int pdaId = getArguments().getInt("pdaId", viewModel.getId());
            int type = getArguments().getInt("type", 0);
            updateFriends(pdaId, type);
        } else updateFriends(viewModel.getId(), 0);
    }

    void updateFriends(int pdaId, int type){
        if (type==0)
            App.getRetrofitService().getPdaAPI().getFriends(pdaId).enqueue(new Callback<List<FriendModel>>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<List<FriendModel>> call, Response<List<FriendModel>> response) {
                    List<FriendModel> body = response.body();
                    if (body!=null && body.size()!=0){
                        recyclerView.setVisibility(View.VISIBLE);
                        friendsAdapter.setData(body);
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<List<FriendModel>> call, Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        else
            App.getRetrofitService().getPdaAPI().getSubs(pdaId).enqueue(new Callback<List<FriendModel>>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<List<FriendModel>> call, Response<List<FriendModel>> response) {
                    List<FriendModel> body = response.body();
                    if (body!=null && body.size()!=0){
                        recyclerView.setVisibility(View.VISIBLE);
                        friendsAdapter.setData(body);
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<List<FriendModel>> call, Throwable t) {

                }
            });
    }
}
