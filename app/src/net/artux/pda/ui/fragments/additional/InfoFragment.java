package net.artux.pda.ui.fragments.additional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.R;
import net.artux.pda.app.PDAApplication;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import me.grantland.widget.AutofitHelper;

public class InfoFragment extends AdditionalBaseFragment {

    TextView mDaysView;
    TextView mLoginView;
    TextView mGroupView;
    TextView mRangView;
    TextView mXpView;
    ImageView mAvatarView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAvatarView = view.findViewById(R.id.avatar);
        mLoginView = view.findViewById(R.id.loginView);
        mDaysView = view.findViewById(R.id.daysInfo);
        mGroupView = view.findViewById(R.id.groupInfo);
        mRangView = view.findViewById(R.id.rangInfo);
        mXpView = view.findViewById(R.id.ratingInfo);

        viewModel.getMember().observe(getViewLifecycleOwner(), memberResult -> {
            String infoTitle = "PDA #" + memberResult.getPdaId();
            if (((PDAApplication) requireActivity().getApplication()).isTesterMode()) {
                infoTitle += " TESTER MODE";
            }
            navigationPresenter.setAdditionalTitle(infoTitle);
            ProfileHelper.setAvatar(mAvatarView, memberResult.getAvatar());
            mLoginView.setText(memberResult.getName() + " " + memberResult.getNickname());
            AutofitHelper.create(mLoginView);

            mDaysView.setText(ProfileHelper.getDays(memberResult.getRegistration()));
        });

        viewModel.getStoryData().observe(getViewLifecycleOwner(), dataModel -> {
            mGroupView.setText(ProfileHelper.getGroup(dataModel.getGang(), mGroupView.getContext()));
            mRangView.setText(ProfileHelper.getRangTitleByXp(dataModel.getXp(), mRangView.getContext()));
            mXpView.setText(String.valueOf(dataModel.getXp()));
        });
        viewModel.updateFromCache();
    }
}
