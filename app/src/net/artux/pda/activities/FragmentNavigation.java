package net.artux.pda.activities;

public interface FragmentNavigation {
    interface View {
        void attachPresenter(Presenter presenter);
    }
    interface Presenter {
        void addFragment(BaseFragment fragment);
        void addAdditionalFragment(AdditionalBaseFragment fragment);

        void setTitle(String title);
        void setAdditionalTitle(String title);
    }
}
