package net.artux.pda.activities;

public class MainPresenter implements MainContract.Presenter, FragmentNavigation.Presenter {

    private MainContract.View view;

    @Override
    public void addFragment(BaseFragment fragment) {
        view.setFragment(fragment);
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
    public void attachView(MainContract.View view) {
        this.view = view;
    }
}
