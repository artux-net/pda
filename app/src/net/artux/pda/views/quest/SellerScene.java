package net.artux.pda.views.quest;

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
import net.artux.pda.views.profile.ItemsAdapter;
import net.artux.pdalib.Status;
import net.artux.pdalib.profile.Data;
import net.artux.pdalib.profile.Seller;
import net.artux.pdalib.profile.items.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerScene extends SceneFragment {

    List<Item> sellerList = new ArrayList<>();
    List<Item> buyerList = new ArrayList<>();

    int sellerId = 0;

    ItemsAdapter sellerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quest_type3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments()!=null)
            sellerId = getArguments().getInt("sellerId");

        RecyclerView sellerView = view.findViewById(R.id.sellerList);
        RecyclerView buyerView = view.findViewById(R.id.buyerList);

        Data data = App.getDataManager().getMember().getData();
        buyerList = data.getAllItems();
        ItemsAdapter buyerAdapter = new ItemsAdapter(pos -> {
            Item item = sellerAdapter.getItems().get(pos);
            App.getRetrofitService().getPdaAPI().sellItem(item.type, item).enqueue(new Callback<Status>() {
                @Override
                public void onResponse(Call<Status> call, Response<Status> response) {
                    Status status = response.body();
                    if (status != null){
                        Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Status> call, Throwable throwable) {

                }
            });
        });
        buyerAdapter.setItems(buyerList);
        buyerView.setAdapter(buyerAdapter);
        buyerView.setLayoutManager(buyerAdapter.getLayoutManager(getContext(),3));


        sellerAdapter = new ItemsAdapter(pos -> {
            Item item = sellerAdapter.getItems().get(pos);
            App.getRetrofitService().getPdaAPI().buyItem(sellerId, item.type, sellerAdapter.getItems().get(pos)).enqueue(new Callback<Status>() {
                @Override
                public void onResponse(Call<Status> call, Response<Status> response) {
                    Status status = response.body();
                    if (status != null){
                        Toast.makeText(getContext(), status.getDescription(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Status> call, Throwable throwable) {

                }
            });
        });

        App.getRetrofitService().getPdaAPI().getSeller(sellerId).enqueue(new Callback<Seller>() {
            @Override
            public void onResponse(Call<Seller> call, Response<Seller> response) {
                Seller seller = response.body();
                if (seller!=null){
                    sellerAdapter.setItems(seller.getAllItems());
                }
            }

            @Override
            public void onFailure(Call<Seller> call, Throwable throwable) {

            }
        });

        sellerView.setLayoutManager(sellerAdapter.getLayoutManager(getContext(),3));
        sellerView.setAdapter(sellerAdapter);

    }
}
