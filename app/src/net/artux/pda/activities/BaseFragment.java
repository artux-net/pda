package net.artux.pda.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment implements FragmentNavigation.View {

    protected FragmentNavigation.Presenter navigationPresenter;

    @Override
    public void attachPresenter(
            FragmentNavigation.Presenter presenter) {
        navigationPresenter = presenter;
    }

    @Override
    public void receiveData(Bundle data) {

    }
}
