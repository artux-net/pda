package net.artux.pda.activities;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements FragmentNavigation.View {

    protected FragmentNavigation.Presenter navigationPresenter;

    @Override
    public void attachPresenter(
            FragmentNavigation.Presenter presenter) {
        navigationPresenter = presenter;
    }
}
