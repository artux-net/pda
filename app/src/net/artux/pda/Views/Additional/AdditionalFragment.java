package net.artux.pda.Views.Additional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.Views.Encyclopedia.EncyclopediaFragment;
import net.artux.pda.activities.AdditionalBaseFragment;
import net.artux.pda.activities.MainActivity;
import net.artux.pda.app.App;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdditionalFragment extends AdditionalBaseFragment implements AdapterView.OnItemClickListener {

    View mainView;
    ListView listView;
    MapAdapter adapter;
    LinkedHashMap<String, String> categories;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        navigationPresenter.setAdditionalTitle(getString(R.string.kinds));
        if (mainView==null){
            mainView = inflater.inflate(R.layout.fragment_add_profile, container, false);

            if(getArguments()!=null){
                App.getRetrofitService().getPdaAPI()
                        .getCategories(getResources().getConfiguration().locale.getISO3Language()).enqueue(new Callback<LinkedHashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<LinkedHashMap<String, String>> call, Response<LinkedHashMap<String, String>> response) {
                        categories = response.body();
                        if(categories!=null) {
                            listView = mainView.findViewById(R.id.menu_profile);
                            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                            adapter = new MapAdapter(categories);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(AdditionalFragment.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkedHashMap<String, String>> call, Throwable t) {
                        Toast.makeText(getActivity(), "Не удалось загрузить категории", Toast.LENGTH_SHORT).show();
                    }
                });



            } else {

                listView = mainView.findViewById(R.id.menu_profile);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        getActivity(), R.array.profile_buttons,
                        android.R.layout.simple_list_item_1);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    //TODO check
                    //controller.setFragmentFromAdditional(position);
                });
            }
        }
        return mainView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String value = (new ArrayList<>(categories.values())).get(position);
        ((EncyclopediaFragment)((MainActivity)getActivity()).mainFragment).load("http://" + BuildConfig.URL + value);
    }
}
