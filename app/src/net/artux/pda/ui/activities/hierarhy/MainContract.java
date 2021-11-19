package net.artux.pda.ui.activities.hierarhy;

import android.os.Bundle;

public interface MainContract {

    interface View {
        void setFragment(BaseFragment fragment, boolean addToBackStack);
        void setAdditionalFragment(AdditionalBaseFragment fragment);

        void setTitle(String title);
        void setAdditionalTitle(String title);

        void setLoadingState(boolean loadingState);
    }

    interface Model {
        interface OnFinishedListener {
            void onFinished(String string);
        }

        void getNextQuote(OnFinishedListener onFinishedListener);
    }

    interface Presenter {
        void attachView(View view);
        void backPressed(BaseFragment baseFragment);
    }

}
