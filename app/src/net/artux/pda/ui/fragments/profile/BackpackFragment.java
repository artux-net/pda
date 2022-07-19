package net.artux.pda.ui.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.fragments.additional.AdditionalFragment;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;

import java.text.DecimalFormat;

public class BackpackFragment extends BaseFragment implements ItemsAdapter.OnClickListener {


    private final ItemsAdapter itemsAdapter = new ItemsAdapter( this);
    private final DecimalFormat formater = new DecimalFormat("##.##");
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
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setVisibility(View.VISIBLE);
        view.findViewById(R.id.viewMessage).setVisibility(View.GONE);

       /* viewModel.getMember().observe(getViewLifecycleOwner(), new Observer<Result<Member>>() {
            @Override
            public void onChanged(Result<Member> memberResult) {
                if(memberResult instanceof Result.Success) {
                    Member member = ((Result.Success<Member>) memberResult).getData();
                    Data data = member.getData();
                    List<Item> items = new ArrayList<>();
                    items.addAll(data.getItems());
                    items.addAll(data.getArmors());
                    items.addAll(data.getArtifacts());
                    items.addAll(data.getWeapons());
                    itemsAdapter.setItems(items);

                    float weight = 0;
                    for (Item item: items)
                        weight += item.weight * item.quantity;

                    navigationPresenter.setTitle("Денег: " + member.getMoney() + ", вес рюкзака: " + formater.format(weight) + " кг");
                }else viewModel.updateMember();
            }
        });*/

        recyclerView.setAdapter(itemsAdapter);
        recyclerView.setLayoutManager(itemsAdapter.getLayoutManager(getContext(),3));
    }

    @Override
    public void onClick(int pos) {
        /*Item item = itemsAdapter.getItems().get(pos);
        Timber.i("Opened item dialog with item: %s", item.title);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogStyle);
        builder.setTitle(item.title);
        builder.setMessage(ItemsHelper.getDesc(item, getContext()));

        builder.setPositiveButton(R.string.enc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EncyclopediaFragment encyclopediaFragment = new EncyclopediaFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.id);
                bundle.putInt("type", item.type);

                encyclopediaFragment.setArguments(bundle);
                AdditionalFragment additionalFragment = new AdditionalFragment();
                additionalFragment.setArguments(bundle);

                navigationPresenter.addFragment(encyclopediaFragment, true);
                navigationPresenter.addAdditionalFragment(additionalFragment);
            }
        });*/
/*
        if (item instanceof Armor || item instanceof Weapon)
        builder.setNeutralButton("Сделать основным", (dialogInterface, i) -> {
            if (item instanceof Armor) {
                App.getRetrofitService().getPdaAPI().setArmor(((Armor) item).hashCode()).enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(Call<Status> call, Response<Status> response) {
                        Status status = response.body();
                        if (status != null) {
                            Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_LONG).show();
                            viewModel.updateMember();
                        }
                    }

                    @Override
                    public void onFailure(Call<Status> call, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }else{
                App.getRetrofitService().getPdaAPI().setWeapon(item.hashCode()).enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(Call<Status> call, Response<Status> response) {
                        Status status = response.body();
                        if (status != null) {
                            Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_LONG).show();
                            viewModel.updateMember();
                        }
                    }

                    @Override
                    public void onFailure(Call<Status> call, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            }
        });*/

        //AlertDialog alertDialog = builder.create();
        //alertDialog.show();
    }

}
