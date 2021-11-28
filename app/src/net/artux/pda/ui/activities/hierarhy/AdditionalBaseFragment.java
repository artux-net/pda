package net.artux.pda.ui.activities.hierarhy;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.artux.pda.viewmodels.MemberViewModel;
import net.artux.pda.viewmodels.ProfileViewModel;
import net.artux.pdalib.Member;

public abstract class AdditionalBaseFragment extends Fragment implements FragmentNavigation.View {

    protected FragmentNavigation.Presenter navigationPresenter;
    protected MemberViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel == null)
            viewModel = getViewModelFactory(this).create(MemberViewModel.class);
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
