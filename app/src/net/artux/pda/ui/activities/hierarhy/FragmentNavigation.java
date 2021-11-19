package net.artux.pda.ui.activities.hierarhy;

import android.os.Bundle;

import com.badlogic.gdx.utils.compression.lzma.Base;

public interface FragmentNavigation {
    interface View {
        void attachPresenter(Presenter presenter);
        void receiveData(Bundle data);
    }
    interface Presenter {
        void addFragment(BaseFragment fragment, boolean addToBackStack);
        BaseFragment getCurrentFragment();
        void addAdditionalFragment(AdditionalBaseFragment fragment);

        void setTitle(String title);
        void setAdditionalTitle(String title);

        void passData(Bundle data);
        void setLoadingState(boolean loadingState);
    }
}
