package net.artux.pda.ui.fragments.additional;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.ui.activities.SettingsActivity;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.fragments.encyclopedia.EncyclopediaFragment;
import net.artux.pda.ui.fragments.profile.BackpackFragment;
import net.artux.pda.ui.fragments.profile.EquipmentFragment;
import net.artux.pda.ui.fragments.profile.ProfileFragment;
import net.artux.pda.ui.fragments.rating.RatingFragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdditionalFragment extends AdditionalBaseFragment implements AdapterView.OnItemClickListener {

    ListView listView;
    MapAdapter adapter;
    LinkedHashMap<String, String> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(navigationPresenter!=null)
            navigationPresenter.setAdditionalTitle(getString(R.string.kinds));
        if(getArguments()!=null){
            App.getRetrofitService().getPdaAPI()
                    .getCategories(getResources().getConfiguration().locale.getISO3Language()).enqueue(new Callback<LinkedHashMap<String, String>>() {
                @Override
                public void onResponse(@NonNull Call<LinkedHashMap<String, String>> call, @NonNull Response<LinkedHashMap<String, String>> response) {
                    categories = response.body();
                    if(categories!=null) {
                        listView = view.findViewById(R.id.menu_profile);
                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        adapter = new MapAdapter(categories);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(AdditionalFragment.this);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LinkedHashMap<String, String>> call, @NonNull Throwable t) {
                    Toast.makeText(getActivity(), "Не удалось загрузить категории", Toast.LENGTH_SHORT).show();
                }
            });



        } else if (getActivity()!=null){
            listView = view.findViewById(R.id.menu_profile);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getActivity(), R.array.profile_buttons,
                    android.R.layout.simple_list_item_1);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                switch (position){
                    case 0:
                        navigationPresenter.addFragment(new EquipmentFragment(), false);
                        break;
                    case 1:
                        navigationPresenter.addFragment(new ProfileFragment(), false);
                        break;
                    case 2:
                        navigationPresenter.addFragment(new BackpackFragment(), false);
                        break;
                    case 3:
                        navigationPresenter.addFragment(new EncyclopediaFragment(), false);
                        break;
                    case 4:
                        navigationPresenter.addFragment(new RatingFragment(), false);
                        break;
                    case 7:
                        startActivity(new Intent(getActivity(), SettingsActivity.class));
                        break;
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String value = (new ArrayList<>(categories.values())).get(position);
        Bundle bundle = new Bundle();
        bundle.putString("load", "https://" + BuildConfig.URL_API + value);
        navigationPresenter.passData(bundle);
    }
}
