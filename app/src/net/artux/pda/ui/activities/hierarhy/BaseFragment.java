package net.artux.pda.ui.activities.hierarhy;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import timber.log.Timber;

public abstract class BaseFragment extends Fragment implements FragmentNavigation.View {

    protected FragmentNavigation.Presenter navigationPresenter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("View created %s", getClass().getName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Timber.d("Fragment attached");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("Fragment create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("Fragment destroyed");
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("Fragment start");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("Fragment resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("Fragment pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("Fragment stop");
    }

    @Override
    public void attachPresenter(
            FragmentNavigation.Presenter presenter) {
        navigationPresenter = presenter;
    }

    @Override
    public void receiveData(Bundle data) {

    }
}
