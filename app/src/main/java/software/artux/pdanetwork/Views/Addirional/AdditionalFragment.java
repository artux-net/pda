package software.artux.pdanetwork.Views.Addirional;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.devilsoftware.pdanetwork.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import software.artux.pdanetwork.Views.Encyclopedia.EncyclopediaFragment;
import software.artux.pdanetwork.activities.MainActivity;
import software.artux.pdanetwork.activities.MainActivityController;
import software.artux.pdanetwork.app.App;

public class AdditionalFragment extends Fragment implements AdapterView.OnItemClickListener {

    View mainView;
    ListView listView;
    MainActivityController controller;
    MapAdapter adapter;
    LinkedHashMap<String, String> categories;

    public AdditionalFragment setController(MainActivityController controller){
        this.controller = controller;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
                    controller.setFragmentFromAdditional(position);
                });
            }
        }
        return mainView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String value = (new ArrayList<>(categories.values())).get(position);
        ((EncyclopediaFragment)((MainActivity)getActivity()).mainFragment).load("http://" + App.URL + value);
    }
}
