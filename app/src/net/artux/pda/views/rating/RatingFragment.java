package net.artux.pda.views.rating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.activities.BaseFragment;
import net.artux.pda.app.App;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationPresenter.setTitle(getString(R.string.rating));

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RatingAdapter ratingAdapter = new RatingAdapter();
        recyclerView.setAdapter(ratingAdapter);

        App.getRetrofitService().getPdaAPI().getRating(0).enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                List<UserInfo> rating = response.body();
                if(rating!=null) ratingAdapter.addData(rating);
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable throwable) {

            }
        });

        view.findViewById(R.id.rating_more).setOnClickListener(view1 -> App.getRetrofitService()
                .getPdaAPI().getRating(ratingAdapter.getItemCount()).enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                List<UserInfo> rating = response.body();
                if(rating!=null) ratingAdapter.addData(rating);
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable throwable) {

            }
        }));
    }
}

