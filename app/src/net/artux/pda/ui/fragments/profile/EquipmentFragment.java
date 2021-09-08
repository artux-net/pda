package net.artux.pda.ui.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pdalib.profile.Equipment;
import net.artux.pdalib.profile.items.Item;

public class EquipmentFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_equipment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null)
            navigationPresenter.setTitle("Снаряжение");
        Equipment equipment = App.getDataManager()
                .getMember()
                .getData()
                .getEquipment();

        if (equipment.getArmor()!=null)
            defineSlot(view, R.id.mainSlot, equipment.getArmor());


        if (equipment.getFirstWeapon()!=null)
            defineSlot(view, R.id.slot2, equipment.getFirstWeapon());
        if (equipment.getSecondWeapon()!=null)
            defineSlot(view, R.id.slot1, equipment.getSecondWeapon());
    }

    private void defineSlot(View view, int slotId, Item item){
        View slot = view.findViewById(slotId);
        TextView title = slot.findViewById(R.id.itemTitle);
        ImageView imageView = slot.findViewById(R.id.itemImage);

        title.setText(item.getTitle());

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

        Glide.with(title.getContext())
                .asGif()
                .load(BuildConfig.PROTOCOL + "://"+ BuildConfig.URL+"base/items/icons/"+item.icon)
                .apply(options)
                .into(imageView);
    }
}
