package net.artux.pda.Views.Additional;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.artux.pda.Models.Member;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.app.DataManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.grantland.widget.AutofitHelper;

public class InfoFragment extends Fragment {

    View mainView;

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

        if(mainView==null){
            mainView = inflater.inflate(R.layout.fragment_info, container, false);
            mDataManager = App.getDataManager();

            Member member = mDataManager.getMember();

            Log.d("IFrag", member.toJson());

            mAvatarView = mainView.findViewById(R.id.avatar);
            mLoginView = mainView.findViewById(R.id.loginView);
            mDaysView = mainView.findViewById(R.id.daysInfo);
            mGroupView = mainView.findViewById(R.id.groupInfo);
            mRangView = mainView.findViewById(R.id.rangInfo);
            mAchievementsView = mainView.findViewById(R.id.achievementsInfo);
            mLocationInfo = mainView.findViewById(R.id.locationInfo);
            mAvatarView.setImageDrawable(getResources().getDrawable(App.avatars[Integer.parseInt(member.getAvatarId())]));


            mLoginView.setText(member.getLogin());
            AutofitHelper.create(mLoginView);

            Date now = new Date();

            DateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.US);

            try {
                Date past = df.parse(member.getRegistrationDate());
                int days = daysBetween(past, now);
                mDaysView.setText(days + " " + getDayAddition(days));
            } catch (ParseException e) {
                e.printStackTrace();
            }



            mGroupView.setText(getResources().getStringArray(R.array.groups)[member.getGroup()]);

            mRangView.setText(getResources().getStringArray(R.array.rangs)[member.getXp()]);

            mAchievementsView.setText("");

            mLocationInfo.setText(member.getLocation());
        }

        return mainView;
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public String getDayAddition(int num) {
        int preLastDigit = num % 100 / 10;
        if (preLastDigit == 1)
        {
            return "дней";
        }

        switch (num % 10)
        {
            case 1:
                return "день";
            case 2:
            case 3:
            case 4:
                return "дня";
            default:
                return "дней";
        }
    }
}
