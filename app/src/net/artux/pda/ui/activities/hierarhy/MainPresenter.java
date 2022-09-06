package net.artux.pda.ui.activities.hierarhy;

public class MainPresenter implements MainContract.Presenter, FragmentNavigation.Presenter {

    private MainContract.View view;
    public BaseFragment mainFragment;
    public AdditionalBaseFragment additionalFragment;

    @Override
    public void addFragment(BaseFragment fragment, boolean addToBackStack) {
        if (mainFragment == null || !mainFragment.getClass().isInstance(fragment)){
            mainFragment = fragment;
            updateAdditionalFragment(fragment);
            view.setFragment(fragment, addToBackStack);
        }
    }

    void updateAdditionalFragment(BaseFragment baseFragment){
        if(!baseFragment.defaultAdditionalFragment.isInstance(additionalFragment)){
            try {
                additionalFragment = baseFragment.defaultAdditionalFragment.newInstance();
                addAdditionalFragment(additionalFragment);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public BaseFragment getCurrentFragment() {
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
