package net.artux.pda.ui.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.model.items.ItemType;
import net.artux.pda.model.items.WearableModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.viewmodels.ItemsViewModel;

public class EquipmentFragment extends BaseFragment {

    {
        defaultAdditionalFragment = AdditionalFragment.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_equipment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter != null)
            navigationPresenter.setTitle("Снаряжение");

        ItemsViewModel itemsViewModel = new ViewModelProvider(requireActivity()).get(ItemsViewModel.class);

        itemsViewModel.getStoryData().observe(getViewLifecycleOwner(), dataModel -> {
            defineSlot(view, R.id.mainSlot, dataModel.getCurrentWearable(ItemType.ARMOR));
            defineSlot(view, R.id.slot1, dataModel.getCurrentWearable(ItemType.RIFLE));
            defineSlot(view, R.id.slot2, dataModel.getCurrentWearable(ItemType.PISTOL));
        });
        itemsViewModel.updateDataFromCache();
    }

    private void defineSlot(View view, int slotId, WearableModel item) {
        if (item != null && item.isEquipped()) {
            View slot = view.findViewById(slotId);
            TextView title = slot.findViewById(R.id.itemTitle);
            ImageView imageView = slot.findViewById(R.id.itemImage);

            title.setText(item.getTitle());

            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher);

            String link = BuildConfig.PROTOCOL + "://" + BuildConfig.URL_API + "base/items/icons/" + item.getIcon();

            Glide.with(title.getContext())
                    .asGif()
                    .load(link)
                    .apply(options)
                    .signature(new ObjectKey(link))
                    .into(imageView);
        }
    }
}
