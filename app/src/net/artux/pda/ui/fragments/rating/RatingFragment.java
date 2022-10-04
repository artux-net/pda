package net.artux.pda.ui.fragments.rating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.databinding.FragmentRatingBinding;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RatingFragment extends BaseFragment implements RatingAdapter.OnClickListener {

    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    protected RatingViewModel ratingViewModel;
    protected FragmentRatingBinding binding;
    protected FragmentListBinding listBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRatingBinding.inflate(inflater);
        listBinding = binding.listContainer;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationPresenter.setTitle(getString(R.string.ratingTitle));
        ratingViewModel = new ViewModelProvider(requireActivity()).get(RatingViewModel.class);

        RatingAdapter ratingAdapter = new RatingAdapter(this, viewModel.getId());
        listBinding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        listBinding.list.setAdapter(ratingAdapter);
        binding.ratingMore.setOnClickListener(view1 -> ratingViewModel.nextPage());

        ratingViewModel.getState().observe(getViewLifecycleOwner(),
                aBoolean -> navigationPresenter.setLoadingState(aBoolean));

        ratingViewModel.getStatus().observe(getViewLifecycleOwner(),
                statusModel -> listBinding.viewMessage.setText(statusModel.getDescription()));

        ratingViewModel.getList().observe(getViewLifecycleOwner(), listResult -> {
            if (listResult.size() > 0) {
                listBinding.list.setVisibility(View.VISIBLE);
                listBinding.viewMessage.setVisibility(View.GONE);
                ratingAdapter.setData(listResult);
            } else {
                listBinding.list.setVisibility(View.GONE);
                listBinding.viewMessage.setVisibility(View.VISIBLE);
            }
        });
        ratingViewModel.updateTop();
    }

    @Override
    public void onClick(UserInfo userInfo) {
        navigationPresenter.addFragment(UserProfileFragment.of(userInfo.id), true);
    }
}

