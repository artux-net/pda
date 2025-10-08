package net.artux.pda.ui.activities.hierarhy;

import androidx.fragment.app.Fragment;

public interface FragmentNavigation {

    interface Presenter {
        void addFragment(Fragment fragment, boolean addToBackStack);

        Fragment getCurrentFragment();

        void addAdditionalFragment(AdditionalBaseFragment fragment);

        void setTitle(String title);

        void setAdditionalTitle(String title);

        void setLoadingState(boolean loadingState);
    }
}
