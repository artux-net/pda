package net.artux.pda.activities;

import android.os.Bundle;

public interface FragmentNavigation {
    interface View {
        void attachPresenter(Presenter presenter);
        void receiveData(Bundle data);
    }
    interface Presenter {
        void addFragment(BaseFragment fragment, boolean addToBackStack);
        void addAdditionalFragment(AdditionalBaseFragment fragment);

        void setTitle(String title);
        void setAdditionalTitle(String title);

        void passData(Bundle data);
    }
}
