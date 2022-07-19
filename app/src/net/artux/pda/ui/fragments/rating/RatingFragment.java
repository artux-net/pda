package net.artux.pda.ui.fragments.rating;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.repositories.util.Result;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;

import java.util.List;

public class RatingFragment extends BaseFragment implements ItemsAdapter.OnClickListener {

    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    RatingViewModel ratingViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter != null) {
            navigationPresenter.setTitle(getString(R.string.rating));
            navigationPresenter.setLoadingState(true);
        }

        if (ratingViewModel == null)
            ratingViewModel = getViewModelFactory(this).create(RatingViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        View v = view.findViewById(R.id.viewMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RatingAdapter ratingAdapter = new RatingAdapter(this, viewModel.getId());
        recyclerView.setAdapter(ratingAdapter);

        ratingViewModel.getList().observe(getViewLifecycleOwner(), new Observer<Result<List<UserInfo>>>() {
            @Override
            public void onChanged(Result<List<UserInfo>> listResult) {
                if (listResult.isSuccess()) {
                    List<UserInfo> list = ((Result.Success<List<UserInfo>>) listResult).getData();
                    if (navigationPresenter != null)
                        navigationPresenter.setLoadingState(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                    ratingAdapter.addData(list);
                }
            }
        });

        view.findViewById(R.id.rating_more).setOnClickListener(view1 -> {
            ratingViewModel.nextPage();
        });

        ratingViewModel.updateList();
    }

    @Override
    public void onClick(int pos) {
        UserProfileFragment profileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pdaId", pos);
        profileFragment.setArguments(bundle);
        if (navigationPresenter != null) {
            navigationPresenter.addFragment(profileFragment, true);
        }
    }
}

