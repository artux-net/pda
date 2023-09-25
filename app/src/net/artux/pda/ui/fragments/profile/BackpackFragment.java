package net.artux.pda.ui.fragments.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.WearableModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.encyclopedia.EncyclopediaFragment;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pda.ui.fragments.profile.helpers.ItemsHelper;
import net.artux.pda.ui.viewmodels.ItemsViewModel;

import java.text.DecimalFormat;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class BackpackFragment extends BaseFragment implements ItemsAdapter.OnClickListener {


    private final ItemsAdapter itemsAdapter = new ItemsAdapter(this);
    private final DecimalFormat formater = new DecimalFormat("##.##");
    private ItemsViewModel itemsViewModel;

    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemsViewModel = new ViewModelProvider(requireActivity()).get(ItemsViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setLayoutManager(itemsAdapter.getLayoutManager(getContext(), 4));
        view.findViewById(R.id.viewMessage).setVisibility(View.GONE);

        itemsViewModel.getStoryData().observe(getViewLifecycleOwner(), storyDataModel -> {
            List<ItemModel> items = storyDataModel.getAllItems();
            itemsAdapter.setItems(items);

            navigationPresenter.setTitle("Денег: " + storyDataModel.getMoney() + ", вес рюкзака: " + formater.format(storyDataModel.getTotalWeight()) + " кг");
        });
        itemsViewModel.getStatus().observe(getViewLifecycleOwner(), status -> {
            Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_LONG).show();
            viewModel.updateMember();
        });
        itemsViewModel.updateDataFromCache();
        itemsViewModel.updateData();
    }

    @Override
    public void onClick(ItemModel item) {
        Timber.i("Opened item dialog with item: %s", item.getTitle());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.PDADialogStyle);
        builder.setTitle(item.getTitle());
        builder.setMessage(ItemsHelper.getDesc(item, getContext()));

        builder.setPositiveButton(R.string.enc, (dialogInterface, i) -> {
            navigationPresenter.addFragment(EncyclopediaFragment.of(item), true);
        });

        if (item instanceof WearableModel wearableModel) {
            if (!wearableModel.isEquipped())
                builder.setNeutralButton(getString(R.string.item_as_main),
                        (dialogInterface, i) -> itemsViewModel.setWearable((WearableModel) item));
            else
                builder.setNeutralButton(getString(R.string.take_off),
                    (dialogInterface, i) -> itemsViewModel.setWearable((WearableModel) item));
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
