package net.artux.pda.activities;

import android.os.Bundle;

public class MainPresenter implements MainContract.Presenter, FragmentNavigation.Presenter {

    private MainContract.View view;

    @Override
    public void addFragment(BaseFragment fragment, boolean addToBackStack) {
        view.setFragment(fragment, addToBackStack);
    }

    @Override
    public void addAdditionalFragment(AdditionalBaseFragment fragment) {
        view.setAdditionalFragment(fragment);
    }

    @Override
    public void setTitle(String title) {
        view.setTitle(title);
    }

    @Override
    public void setAdditionalTitle(String title) {
        view.setAdditionalTitle(title);
    }

    @Override
    public void passData(Bundle data) {
        view.passData(data);
    }

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }
}
