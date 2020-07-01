package net.artux.pda.activities;

public interface MainContract {

    interface View {
        void setFragment(BaseFragment fragment);
        void setAdditionalFragment(AdditionalBaseFragment fragment);
        void attachPresenter(Presenter presenter);
        void setTitle(String title);
        void setAdditionalTitle(String title);
    }

    interface Model {
        interface OnFinishedListener {
            void onFinished(String string);
        }

        void getNextQuote(OnFinishedListener onFinishedListener);
    }

    interface Presenter {
        void attachView(View view);
    }

}
