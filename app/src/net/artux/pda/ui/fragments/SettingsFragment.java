package net.artux.pda.ui.fragments;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.databinding.ActivitySettingsBinding;
import net.artux.pda.repositories.Result;
import net.artux.pda.ui.activities.LoginActivity;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pdalib.Member;
import net.artux.pdalib.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {


    private ActivitySettingsBinding binding;
    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivitySettingsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickListener(binding.getRoot());
        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getString(R.string.settings));
        }
        binding.version.setText(getResources().getString(R.string.version, BuildConfig.VERSION_NAME));
        binding.build.setText(getResources().getString(R.string.build, String.valueOf(BuildConfig.VERSION_CODE)));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        viewModel.getMember().observe(getViewLifecycleOwner(), memberResult -> {
            if (memberResult instanceof Result.Success){
                Member member = ((Result.Success<Member>) memberResult).getData();
                String json = gson.toJson(member);
                binding.debugMember.setText(json);
            }
        });

    }

    void setOnClickListener(ViewGroup view){
        for (int i = 0; i < view.getChildCount(); i++) {
            if(view.getChildAt(i) instanceof ViewGroup)
                setOnClickListener((ViewGroup) view.getChildAt(i));
            else
                view.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signOut:
                App.getDataManager().removeAllData();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.clearImages:
                new Thread(() -> {
                    if (getContext()!=null) {
                        Glide.get(getContext()).clearDiskCache();
                    }
                }).start();
                break;
            case R.id.showDebug:
                if (binding.debugMember.getVisibility()==View.VISIBLE)
                    binding.debugMember.setVisibility(View.GONE);
                else
                    binding.debugMember.setVisibility(View.VISIBLE);
                break;
            case R.id.resetData:
                viewModel.resetData();
                break;
            case R.id.resetStory:
                HashMap<String, List<String>> action = new HashMap<>();
                action.put("reset_current", new ArrayList<>());
                viewModel.syncMember(action);
                break;
        }
    }
}
