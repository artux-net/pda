package net.artux.pda.ui.activities.hierarhy;

public interface FragmentNavigation {
    interface View {
        void attachPresenter(Presenter presenter);
    }
    interface Presenter {
        void addFragment(BaseFragment fragment, boolean addToBackStack);
        BaseFragment getCurrentFragment();
        void addAdditionalFragment(AdditionalBaseFragment fragment);

        void setTitle(String title);
        void setAdditionalTitle(String title);

        void setLoadingState(boolean loadingState);
    }
}
