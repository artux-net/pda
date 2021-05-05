package net.artux.pda.ui.fragments.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.encyclopedia.EncyclopediaFragment;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pdalib.Status;
import net.artux.pdalib.profile.Data;
import net.artux.pdalib.profile.items.Armor;
import net.artux.pdalib.profile.items.Item;
import net.artux.pdalib.profile.items.Weapon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackpackFragment extends BaseFragment implements ItemsAdapter.OnClickListener {

    List<Item> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter!=null)
            navigationPresenter.setTitle("Денег: " + App.getDataManager().getMember().getMoney());
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setVisibility(View.VISIBLE);
        view.findViewById(R.id.viewMessage).setVisibility(View.GONE);

        Data data = App.getDataManager().getMember().getData();
        items.addAll(data.getItems());
        items.addAll(data.getArmors());
        items.addAll(data.getArtifacts());
        items.addAll(data.getWeapons());

        ItemsAdapter itemsAdapter = new ItemsAdapter( this);
        itemsAdapter.setItems(items);
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setLayoutManager(itemsAdapter.getLayoutManager(getContext(),3));

        //grid.setVerticalSpacing(20);
        //grid.setHorizontalSpacing(20);

    }

    @Override
    public void onClick(int pos) {
        Item item = items.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
        builder.setTitle(item.title);
        builder.setMessage(ItemsHelper.getDesc(item, getContext()));

        builder.setPositiveButton(R.string.enc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EncyclopediaFragment encyclopediaFragment = new EncyclopediaFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.library_id);
                bundle.putInt("type", item.type);

                encyclopediaFragment.setArguments(bundle);
                AdditionalFragment additionalFragment = new AdditionalFragment();
                additionalFragment.setArguments(bundle);

                navigationPresenter.addFragment(encyclopediaFragment, true);
                navigationPresenter.addAdditionalFragment(additionalFragment);
            }
        });

        if (item instanceof Armor || item instanceof Weapon)
        builder.setNeutralButton("Set as main", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (item instanceof Armor) {
                            App.getRetrofitService().getPdaAPI().setArmor(item.type, (Armor) item).enqueue(new Callback<Status>() {
                                @Override
                                public void onResponse(Call<Status> call, Response<Status> response) {
                                    Status status = response.body();
                                    if (status != null) {
                                        Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Status> call, Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                     // if (item instanceof Weapon)
                        }else{
                            App.getRetrofitService().getPdaAPI().setWeapon(item.type, (Weapon) item).enqueue(new Callback<Status>() {
                                @Override
                                public void onResponse(Call<Status> call, Response<Status> response) {
                                    Status status = response.body();
                                    if (status != null) {
                                        Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Status> call, Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                        }
                    }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
