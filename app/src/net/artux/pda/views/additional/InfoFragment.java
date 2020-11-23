package net.artux.pda.views.additional;

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
import net.artux.pda.activities.AdditionalBaseFragment;
import net.artux.pda.app.App;
import net.artux.pda.app.DataManager;
import net.artux.pda.models.ProfileHelper;
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

        if (mDataManager.getMember()!=null) {
            Profile profile = new Profile(mDataManager.getMember());
            navigationPresenter.setAdditionalTitle("PDA #" + App.getDataManager().getMember().getPdaId());
            mAvatarView.setImageDrawable(ProfileHelper.getAvatar(profile, mAvatarView.getContext()));
            mLoginView.setText(profile.getLogin());
            AutofitHelper.create(mLoginView);

            mDaysView.setText(ProfileHelper.getDays(profile));
            mGroupView.setText(ProfileHelper.getGroup(profile, mGroupView.getContext()));
            mRangView.setText(ProfileHelper.getRang(profile, mRangView.getContext()));
            mAchievementsView.setText("");
            mLocationInfo.setText(profile.getLocation());
        }else
            navigationPresenter.setAdditionalTitle("Unable to get member");

    }
}
