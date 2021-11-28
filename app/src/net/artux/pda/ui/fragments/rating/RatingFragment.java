package net.artux.pda.ui.fragments.rating;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

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
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pdalib.ResponsePage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingFragment extends BaseFragment implements ItemsAdapter.OnClickListener {

    private int number = 1;
    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

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
        RatingAdapter ratingAdapter = new RatingAdapter(this, viewModel.getId());
        recyclerView.setAdapter(ratingAdapter);

        App.getRetrofitService().getPdaAPI().getRating(number).enqueue(new Callback<ResponsePage<UserInfo>>() {
            @Override
            public void onResponse(Call<ResponsePage<UserInfo>> call, Response<ResponsePage<UserInfo>> response) {
                ResponsePage<UserInfo> rating = response.body();
                if(rating!=null){
                    List<UserInfo> list = rating.getData();
                    if (navigationPresenter!=null)
                        navigationPresenter.setLoadingState(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                    ratingAdapter.addData(list);
                }
            }

            @Override
            public void onFailure(Call<ResponsePage<UserInfo>> call, Throwable t) {

            }
        });

        view.findViewById(R.id.rating_more).setOnClickListener(view1 -> App.getRetrofitService()
                .getPdaAPI().getRating(++number).enqueue(new Callback<ResponsePage<UserInfo>>() {
                    @Override
                    public void onResponse(Call<ResponsePage<UserInfo>> call, Response<ResponsePage<UserInfo>> response) {
                        ResponsePage<UserInfo> rating = response.body();
                        if(rating!=null){
                            ratingAdapter.addData(rating.getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePage<UserInfo>> call, Throwable t) {

                    }
                }));
    }

    @Override
    public void onClick(int pos) {
        UserProfileFragment profileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pdaId", pos);
        profileFragment.setArguments(bundle);
        if (navigationPresenter!=null) {
            navigationPresenter.addFragment(profileFragment, true);
        }
    }
}

