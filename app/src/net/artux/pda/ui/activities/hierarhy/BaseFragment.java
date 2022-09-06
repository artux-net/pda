package net.artux.pda.ui.activities.hierarhy;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.ui.fragments.additional.InfoFragment;
import net.artux.pda.ui.viewmodels.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public abstract class BaseFragment extends Fragment implements FragmentNavigation.View {

    protected FragmentNavigation.Presenter navigationPresenter;
    protected UserViewModel viewModel;
    public Class<? extends AdditionalBaseFragment> defaultAdditionalFragment = InfoFragment.class;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("Fragment %s created", getClass().getName());
        if (viewModel == null)
            viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("Fragment %s stopped.", getClass().getName());
    }

    @Override
    public void attachPresenter(
            FragmentNavigation.Presenter presenter) {
        navigationPresenter = presenter;
    }

}
