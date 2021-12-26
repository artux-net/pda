package net.artux.pda.ui.fragments.additional;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.app.DataManager;
import net.artux.pda.repositories.Result;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;
import net.artux.pdalib.Member;
import net.artux.pdalib.Profile;

import me.grantland.widget.AutofitHelper;

public class InfoFragment extends AdditionalBaseFragment {

    TextView mDaysView;
    TextView mLoginView;
    TextView mGroupView;
    TextView mRangView;
    TextView mAchievementsView;
    TextView mLocationInfo;
    ImageView mAvatarView;

    DataManager mDataManager;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDataManager = App.getDataManager();

        mAvatarView = view.findViewById(R.id.avatar);
        mLoginView = view.findViewById(R.id.loginView);
        mDaysView = view.findViewById(R.id.daysInfo);
        mGroupView = view.findViewById(R.id.groupInfo);
        mRangView = view.findViewById(R.id.rangInfo);
        mAchievementsView = view.findViewById(R.id.achievementsInfo);
        mLocationInfo = view.findViewById(R.id.locationInfo);

        viewModel.getMember().observe(getViewLifecycleOwner(), memberResult -> {
            if(memberResult instanceof Result.Success){
                Member member = ((Result.Success<Member>) memberResult).getData();
                Profile profile = new Profile(member);
                navigationPresenter.setAdditionalTitle("PDA #" + member.getPdaId());
                ProfileHelper.setAvatar(mAvatarView, profile.getAvatar());
                mLoginView.setText(profile.getLogin());
                AutofitHelper.create(mLoginView);

                mDaysView.setText(ProfileHelper.getDays(profile));
                mGroupView.setText(ProfileHelper.getGroup(profile, mGroupView.getContext()));
                mRangView.setText(ProfileHelper.getRang(profile, mRangView.getContext()));
                mAchievementsView.setText("");
                mLocationInfo.setText(profile.getLocation());
            }else viewModel.updateMember();
        });

    }
}
