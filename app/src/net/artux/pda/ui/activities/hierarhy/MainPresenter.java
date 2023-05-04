package net.artux.pda.ui.activities.hierarhy;

import androidx.fragment.app.Fragment;

public class MainPresenter implements MainContract.Presenter, FragmentNavigation.Presenter {

    private MainContract.View view;
    public Fragment mainFragment;
    public AdditionalBaseFragment additionalFragment;

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        if (mainFragment == null || !mainFragment.getClass().isInstance(fragment)){
            mainFragment = fragment;
            if (mainFragment instanceof BaseFragment) {
                updateAdditionalFragment((BaseFragment) fragment);
            }
            view.setFragment(fragment, addToBackStack);
        }
    }

    void updateAdditionalFragment(BaseFragment baseFragment){
        if(!baseFragment.defaultAdditionalFragment.isInstance(additionalFragment)){
            try {
                additionalFragment = baseFragment.getDefaultAdditionalFragment().newInstance();
                addAdditionalFragment(additionalFragment);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Fragment getCurrentFragment() {
        return mainFragment;
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
    public void setLoadingState(boolean loadingState) {
        view.setLoadingState(loadingState);
    }

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void backPressed(BaseFragment fragment) {
        mainFragment = fragment;
        updateAdditionalFragment(fragment);
    }
}
