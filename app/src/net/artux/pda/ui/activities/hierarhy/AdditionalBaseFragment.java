package net.artux.pda.ui.activities.hierarhy;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.ui.viewmodels.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class AdditionalBaseFragment extends Fragment implements FragmentNavigation.View {

    protected FragmentNavigation.Presenter navigationPresenter;
    protected UserViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel == null)
            viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void attachPresenter(
            FragmentNavigation.Presenter presenter) {
        navigationPresenter = presenter;
    }

}
