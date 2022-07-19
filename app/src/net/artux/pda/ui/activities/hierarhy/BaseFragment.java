package net.artux.pda.ui.activities.hierarhy;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.artux.pda.ui.fragments.additional.InfoFragment;
import net.artux.pda.ui.viewmodels.UserViewModel;

import timber.log.Timber;

public abstract class BaseFragment extends Fragment implements FragmentNavigation.View {

    protected FragmentNavigation.Presenter navigationPresenter;
    protected UserViewModel viewModel;

    public Class<? extends AdditionalBaseFragment> defaultAdditionalFragment = InfoFragment.class;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("Fragment %s created", getClass().getName());
        if (viewModel == null)
            viewModel = getViewModelFactory(this).create(UserViewModel.class);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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

    @Override
    public void receiveData(Bundle data) {

    }

}
