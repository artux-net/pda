package net.artux.pda.Views.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import net.artux.pda.Models.Profile;
import net.artux.pda.R;
import net.artux.pda.activities.BaseFragment;
import net.artux.pda.app.App;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends BaseFragment {

    View mainView;
    ImageView avatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mainView==null){
            mainView = inflater.inflate(R.layout.fragment_profile, container, false);
        }

        if (getArguments()!=null) {
            int pda = getArguments().getInt("pda", 0);
            if (pda!=0)
                App.getRetrofitService().getPdaAPI().getProfile(pda).enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        Profile profile = response.body();
                        if (profile != null)
                            setProfile(profile);
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable throwable) {

                    }
                });

        }else {
            App.getRetrofitService().getPdaAPI().getMyProfile().enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    Profile profile = response.body();
                    if (profile != null)
                        setProfile(profile);
                }

                @Override
                public void onFailure(Call<Profile> call, Throwable throwable) {

                }
            });

        }
        return mainView;
    }

    public void setProfile(Profile profile) {
        avatar = mainView.findViewById(R.id.profile_avatar);
        avatar.setImageDrawable(profile.getAvatar(getContext()));
        ((TextView)mainView.findViewById(R.id.profile_login)).setText(profile.getLogin());
        ((TextView)mainView.findViewById(R.id.profile_group)).setText(profile.getGroup(getContext()));
        ((TextView)mainView.findViewById(R.id.profile_location)).setText(profile.getLocation());
        ((TextView)mainView.findViewById(R.id.profile_time)).setText(profile.getDays());
        ((TextView)mainView.findViewById(R.id.profile_rang)).setText(profile.getRang(getContext()));


    }
}
