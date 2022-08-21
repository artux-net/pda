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
import com.bumptech.glide.signature.ObjectKey;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.repositories.util.Result;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;

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
        if (navigationPresenter!=null)
            navigationPresenter.setTitle("Снаряжение");

        viewModel.getMember().observe(getViewLifecycleOwner(), memberResult -> {
            if(memberResult instanceof Result.Success){
                UserModel userModel = ((Result.Success<UserModel>) memberResult).getData();
                //TODO
               /* Equipment equipment = userModel
                        .getData()
                        .getEquipment();

                if (equipment.getArmor()!=null)
                    defineSlot(view, R.id.mainSlot, equipment.getArmor());
                if (equipment.getFirstWeapon()!=null)
                    defineSlot(view, R.id.slot2, equipment.getFirstWeapon());
                if (equipment.getSecondWeapon()!=null)
                    defineSlot(view, R.id.slot1, equipment.getSecondWeapon());*/
            }else viewModel.updateMember();
        });


    }

    private void defineSlot(View view, int slotId, ItemModel item){
        View slot = view.findViewById(slotId);
        TextView title = slot.findViewById(R.id.itemTitle);
        ImageView imageView = slot.findViewById(R.id.itemImage);

        title.setText(item.getTitle());

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

        String link = BuildConfig.PROTOCOL + "://"+ BuildConfig.URL+"base/items/icons/"+item.getIcon();

        //String cached = GlideUtil.sha256BytesToHex(link.getBytes(StandardCharsets.UTF_8));

        Glide.with(title.getContext())
                .asGif()
                .load(link)
                .apply(options)
                .signature(new ObjectKey(link))
                .into(imageView);
    }
}
