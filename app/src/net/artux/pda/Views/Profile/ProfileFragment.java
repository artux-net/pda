package net.artux.pda.Views.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import net.artux.pda.BuildConfig;
import net.artux.pda.Models.profile.Data;
import net.artux.pda.R;
import net.artux.pda.app.App;

public class ProfileFragment extends Fragment {

    View mainView;
    Data data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        if(mainView==null){
            mainView = inflater.inflate(R.layout.fragment_profile_equipment, container, false);
        }

        data = App.getDataManager().getMember().getData();

        Glide.with(getActivity())
                .asGif()
                .load("http://"+ BuildConfig.URL +"/files?file="+data.getEquipment().toString())
                .into((ImageView) mainView.findViewById(R.id.slot1));

        Glide.with(getActivity())
                .asGif()
                //.load("http://"+App.URL+"/files?file="+data.getEquipment().getType0().icon)
                .into((ImageView) mainView.findViewById(R.id.slot2));

        Glide.with(getActivity())
                .asDrawable()
                //.load("http://"+App.URL+"/files?file="+data.getEquipment().getArmor().icon)
                    .into((ImageView) mainView.findViewById(R.id.mainSprite));


        return mainView;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
