package net.artux.pda.ui.fragments.additional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentAddProfileBinding;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.SettingsFragment;
import net.artux.pda.ui.fragments.encyclopedia.EncyclopediaFragment;
import net.artux.pda.ui.fragments.profile.BackpackFragment;
import net.artux.pda.ui.fragments.profile.EquipmentFragment;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.rating.RatingFragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdditionalFragment extends AdditionalBaseFragment {

    FragmentAddProfileBinding binding;
    ArrayAdapter<CharSequence> adapter;
    List<Class<? extends BaseFragment>> fragmentClasses = new ArrayList<>();
    {
        fragmentClasses.add(EquipmentFragment.class);
        fragmentClasses.add(UserProfileFragment.class);
        fragmentClasses.add(BackpackFragment.class);
        fragmentClasses.add(EncyclopediaFragment.class);
        fragmentClasses.add(RatingFragment.class);
        fragmentClasses.add(null);
        fragmentClasses.add(null);
        fragmentClasses.add(SettingsFragment.class);
    }
    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddProfileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(navigationPresenter!=null)
            navigationPresenter.setAdditionalTitle(getString(R.string.kinds));
        if (getActivity()!=null){
            binding.menuProfile.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            adapter = ArrayAdapter.createFromResource(
                    getActivity(), R.array.profile_buttons,
                    android.R.layout.simple_list_item_1);
            binding.menuProfile.setAdapter(adapter);

           binding.menuProfile.setOnItemClickListener((parent, view1, position, id) -> {
               //resetSelection();
               //view1.setBackgroundColor(getResources().getColor(R.color.black_overlay));
                if (fragmentClasses.get(position) != null)
                    try {
                        navigationPresenter.addFragment(fragmentClasses.get(position).newInstance(), true);
                    } catch (IllegalAccessException | java.lang.InstantiationException e) {
                        e.printStackTrace();
                    }
            });
        }
    }

    void resetSelection(){
        for (int i = 0; i < binding.menuProfile.getChildCount(); i++) {
            binding.menuProfile.getChildAt(i)
                    .setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    public void receiveData(Bundle data) {
        super.receiveData(data);
        //resetSelection();
        /*int pos = getPosition(data.getString("fragment"));
        binding.menuProfile.getChildAt(pos)
                .setBackgroundColor(getResources().getColor(R.color.black_overlay));*/
    }

    int getPosition(String simpleName){
        int i = 0;
        for (Class<? extends BaseFragment> t : fragmentClasses) {
            if (t!=null && t.getSimpleName().equals(simpleName))
                return i;
            i++;
        }
        return 0;
    }


}
