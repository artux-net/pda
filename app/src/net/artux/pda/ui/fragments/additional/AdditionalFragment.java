package net.artux.pda.ui.fragments.additional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.fragments.encyclopedia.EncyclopediaFragment;
import net.artux.pda.ui.fragments.profile.BackpackFragment;
import net.artux.pda.ui.fragments.profile.EquipmentFragment;
import net.artux.pda.ui.fragments.profile.SummaryFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.rating.RatingFragment;
import net.artux.pda.ui.fragments.settings.PrefsFragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdditionalFragment extends AdditionalBaseFragment implements StringAdapter.StringListClickListener {

    private FragmentListBinding binding;
    private StringAdapter adapter;
    private final List<Class<? extends Fragment>> fragmentClasses = new ArrayList<>();

    {
        fragmentClasses.add(EquipmentFragment.class);
        fragmentClasses.add(UserProfileFragment.class);
        fragmentClasses.add(BackpackFragment.class);
        fragmentClasses.add(EncyclopediaFragment.class);
        fragmentClasses.add(RatingFragment.class);
        //fragmentClasses.add(null);
        fragmentClasses.add(SummaryFragment.class);
        fragmentClasses.add(PrefsFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter != null)
            navigationPresenter.setAdditionalTitle(getString(R.string.kinds));

        adapter = new StringAdapter(this);
        adapter.setItems(Arrays.asList(getResources().getStringArray(R.array.profile_buttons)));
        binding.list.setVisibility(View.VISIBLE);
        binding.viewMessage.setVisibility(View.GONE);
        binding.list.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(int position, String content) {
        if (fragmentClasses.get(position) != null)
            try {
                navigationPresenter.addFragment(fragmentClasses.get(position).newInstance(), true);
            } catch (IllegalAccessException | java.lang.InstantiationException e) {
                e.printStackTrace();
            }
    }
}
