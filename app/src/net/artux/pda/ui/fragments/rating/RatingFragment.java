package net.artux.pda.ui.fragments.rating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RatingFragment extends BaseFragment implements RatingAdapter.OnClickListener {

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
            ratingViewModel = new ViewModelProvider(requireActivity()).get(RatingViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        View v = view.findViewById(R.id.viewMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RatingAdapter ratingAdapter = new RatingAdapter(this, viewModel.getId());
        recyclerView.setAdapter(ratingAdapter);

        ratingViewModel.getList().observe(getViewLifecycleOwner(), new Observer<List<UserInfo>>() {
            @Override
            public void onChanged(List<UserInfo> listResult) {
                if (navigationPresenter != null)
                    navigationPresenter.setLoadingState(false);
                recyclerView.setVisibility(View.VISIBLE);
                v.setVisibility(View.GONE);
                ratingAdapter.addData(listResult);
            }
        });

        view.findViewById(R.id.rating_more).setOnClickListener(view1 -> {
            ratingViewModel.nextPage();
        });

        ratingViewModel.updateList();
    }

    @Override
    public void onClick(UserInfo userInfo) {
        if (navigationPresenter != null) {
            navigationPresenter.addFragment(UserProfileFragment.of(userInfo.id), true);
        }
    }
}

