package net.artux.pda.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.databinding.ActivitySettingsBinding;
import net.artux.pda.model.StatusModel;
import net.artux.pda.ui.activities.LoginActivity;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.viewmodels.QuestViewModel;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private ActivitySettingsBinding binding;
    private File cacheDirectory;

    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    private QuestViewModel questViewModel;

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
        questViewModel = new ViewModelProvider(requireActivity()).get(QuestViewModel.class);
        if (navigationPresenter != null) {
            navigationPresenter.setTitle(getString(R.string.settings));
        }
        binding.version.setText(getResources().getString(R.string.version, BuildConfig.VERSION_NAME));
        binding.build.setText(getResources().getString(R.string.build, String.valueOf(BuildConfig.VERSION_CODE)));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        viewModel.getMember().observe(getViewLifecycleOwner(), memberResult -> {
            String json = gson.toJson(memberResult);
            binding.debugMember.setText(json);
        });
        questViewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<StatusModel>() {
            @Override
            public void onChanged(StatusModel statusModel) {
                Toast.makeText(requireContext(), statusModel.getDescription(), Toast.LENGTH_LONG).show();
            }
        });

        PackageManager m = requireActivity().getPackageManager();
        String s = requireActivity().getPackageName();
        PackageInfo p;
        try {
            p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        cacheDirectory = new File(s + "/cache");
        Timber.d(Arrays.toString(cacheDirectory.list()));

        binding.mapCache.setText("Сохраненные карты: " + ((float) (dirSize(cacheDirectory) / (1024 * 1024))) + " мб");

        //binding.testAds.setOnClickListener(v -> Appodeal.startTestActivity(requireActivity()));
    }

    private static long dirSize(File dir) {
        Timber.d(dir.getAbsolutePath());
        Timber.d(Arrays.toString(Objects.requireNonNull(dir.getParentFile()).listFiles()));
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (file.isDirectory()) {
                        result += dirSize(file);
                    } else {
                        result += file.length();
                    }
                }
            }
            return result;
        }
        return 0;
    }

    void setOnClickListener(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            if (view.getChildAt(i) instanceof ViewGroup)
                setOnClickListener((ViewGroup) view.getChildAt(i));
            else
                view.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signOut:
                viewModel.signOut();
                questViewModel.clear();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                requireActivity().finish();
                break;
            case R.id.questResetButton:
                questViewModel.clear();
                Toast.makeText(requireContext(), "Ok!", Toast.LENGTH_SHORT).show();
            case R.id.imagesResetButton:
                new Thread(() -> {
                    if (getContext() != null) {
                        Glide.get(getContext()).clearDiskCache();
                        Looper.prepare();
                        Toast.makeText(requireContext(), "Ok!", Toast.LENGTH_SHORT).show();
                    }
                }).start();
                break;
            case R.id.showDebug:
                if (binding.debugMember.getVisibility() == View.VISIBLE)
                    binding.debugMember.setVisibility(View.GONE);
                else
                    binding.debugMember.setVisibility(View.VISIBLE);
                break;
            case R.id.resetData:
                clearSharedPreferences(requireContext().getApplicationContext());
                questViewModel.resetData();
                break;
            case R.id.mapCacheResetButton:
                if (cacheDirectory.delete()) {
                    binding.mapCache.setText("Сохраненные карты: " + (dirSize(cacheDirectory) / (1024 * 1024)) + " мб");
                    Toast.makeText(requireContext(), "Ok!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static void clearSharedPreferences(Context ctx) {
        File dir = new File(ctx.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (String child : children) {
            if (!child.equals("prefs.xml")) {
                ctx.getSharedPreferences(child.replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
                //delete the file
                new File(dir, child).delete();
            }
        }
    }
}
