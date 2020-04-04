package net.artux.pda.activities;

import android.content.Intent;
import android.view.View;


import net.artux.pda.R;
import net.artux.pda.Views.Addirional.AdditionalFragment;
import net.artux.pda.Views.Chat.DialogsFragment;
import net.artux.pda.Views.Addirional.InfoFragment;
import net.artux.pda.Views.News.NewsFragment;
import net.artux.pda.Views.Profile.BackpackFragment;
import net.artux.pda.Views.Profile.ProfileFragment;
import net.artux.pda.app.App;

public class MainActivityController implements View.OnClickListener {

    MainActivity mainActivity;

    NewsFragment mNewsFragment;
    InfoFragment mInfoFragment;
    DialogsFragment mDialogsFragment;
    ProfileFragment mProfileFragment;

    MainActivityController(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    void firstSetup(){
        mNewsFragment = new NewsFragment();
        mInfoFragment = new InfoFragment();

        mainActivity.setupMainFragment(mNewsFragment);
        mainActivity.setTitle(mainActivity.getResources().getString(R.string.news));
        mainActivity.setupAdditionalFragment(mInfoFragment);
        if(App.getDataManager().getMember()!=null){
            mainActivity.setAdditionalTitle("PDA #" + App.getDataManager().getMember().getPdaId());
        }

    }

    public void setFragmentFromAdditional(int position) {
        switch (position) {
            case 0:
                mainActivity.setupMainFragment(mProfileFragment);
                mainActivity.setTitle(mainActivity.getResources().getString(R.string.profile));
                break;
            case 1:
                BackpackFragment backpackFragment = new BackpackFragment();
                backpackFragment.setItems(App.getDataManager().getMember().getData().getItems());

                mainActivity.setupMainFragment(backpackFragment);
                mainActivity.setTitle(mainActivity.getResources().getString(R.string.backpack));
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                break;

        }
    }

    @Override
    public void onClick(View v) {

        mainActivity.setupAdditionalFragment(mInfoFragment);
        switch (v.getId()){
            case R.id.news:
                if(mNewsFragment==null)
                {
                    mNewsFragment = new NewsFragment();
                }
                mainActivity.setTitle(mainActivity.getResources().getString(R.string.news));
                mainActivity.setupMainFragment(mNewsFragment);
                break;
            case R.id.messages:
                if(mDialogsFragment ==null){
                    mDialogsFragment = new DialogsFragment();
                }
                mainActivity.setTitle(mainActivity.getResources().getString(R.string.chat));
                mainActivity.setupMainFragment(mDialogsFragment);
                break;
            case R.id.profile:
                if(mProfileFragment==null)
                {
                    mProfileFragment = new ProfileFragment();
                }
                mainActivity.setupMainFragment(mProfileFragment);
                mainActivity.setTitle(mainActivity.getResources().getString(R.string.profile));
                mainActivity.setupAdditionalFragment(new AdditionalFragment().setController(this));
                break;
            case R.id.settings:
                mainActivity.startActivity(new Intent(mainActivity, SettingsActivity.class));
                break;
            case R.id.quest:
                mainActivity.startActivity(new Intent(mainActivity, QuestActivity.class));
                break;
        }
    }
}
