package net.artux.pda.Views.Profile;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


import java.util.List;

import net.artux.pda.Models.profile.Item;
import net.artux.pda.R;
import net.artux.pda.activities.MainActivity;

public class BackpackFragment extends Fragment {

    private View mainView;

    GridView grid;
    List<Item> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mainView == null){
            mainView = inflater.inflate(R.layout.fragment_profile_backpack, container, false);
            grid = mainView.findViewById(R.id.grid);

            BackpackAdapter backpackAdapter = new BackpackAdapter(getActivity(), R.layout.item_backpack, items, (MainActivity) getActivity());
            grid.setAdapter(backpackAdapter);
            adjustGrid();

        }

        return mainView;
    }

    void adjustGrid(){
        grid.setNumColumns(3);
        grid.setVerticalSpacing(20);
        grid.setHorizontalSpacing(20);
    }

    public void setItems(List<Item> items){
        this.items = items;
    }
}
