package net.artux.pda.ui.fragments.rating;

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
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.profile.ProfileFragment;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RatingFragment extends BaseFragment implements ItemsAdapter.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getString(R.string.rating));
            navigationPresenter.setLoadingState(true);
        }

        RecyclerView recyclerView = view.findViewById(R.id.list);
        View v = view.findViewById(R.id.viewMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RatingAdapter ratingAdapter = new RatingAdapter(this);
        recyclerView.setAdapter(ratingAdapter);

        App.getRetrofitService().getPdaAPI().getRating(0).enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                List<UserInfo> rating = response.body();
                if (navigationPresenter!=null)
                    navigationPresenter.setLoadingState(false);
                if(rating!=null){
                    recyclerView.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                    ratingAdapter.addData(rating);
                }
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable throwable) {
                if (navigationPresenter!=null)
                    navigationPresenter.setLoadingState(false);
                Timber.e(throwable);
            }
        });

        view.findViewById(R.id.rating_more).setOnClickListener(view1 -> App.getRetrofitService()
                .getPdaAPI().getRating(ratingAdapter.getItemCount()).enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                List<UserInfo> rating = response.body();
                if(rating!=null) {
                    ratingAdapter.addData(rating);
                }
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable throwable) {

            }
        }));
    }

    @Override
    public void onClick(int pos) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pdaId", pos);
        profileFragment.setArguments(bundle);
        if (navigationPresenter!=null) {
            navigationPresenter.addFragment(profileFragment, false);
            navigationPresenter.addAdditionalFragment(new AdditionalFragment());
        }
    }
}

