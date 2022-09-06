package net.artux.pda.ui.fragments.quest;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pda.ui.viewmodels.UserViewModel;

public class SellerActivity extends AppCompatActivity implements View.OnClickListener {

    private ItemsAdapter sellerAdapter;
    private RecyclerView buyerView;
    private ImageView background;

    private UserViewModel viewModel;

    private int sellerId = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quest3);
        if (viewModel == null)
            viewModel = getViewModelFactory(this).create(UserViewModel.class);

        sellerId = getIntent().getIntExtra("seller", 0);


        RecyclerView sellerView = findViewById(R.id.sellerList);
        buyerView = findViewById(R.id.buyerList);
        background = findViewById(R.id.sellerBackground);
        findViewById(R.id.map).setOnClickListener(this);

        viewModel.getMember().observe(this, this::updateList);

        sellerAdapter = new ItemsAdapter();
        sellerAdapter.setOnClickListener(pos -> {
            //todo
            /*Item item = sellerAdapter.getItems().get(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Вы хотите купить " + item.title + " за " + item.price + "?");

            builder.setPositiveButton(R.string.yes, (dialogInterface, i) ->((App)getApplication()).getRetrofitService().getPdaAPI().buyItem(sellerId, sellerAdapter.getItems().get(pos).hashCode()).enqueue(new Callback<Status>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<Status> call, Response<Status> response) {
                    Status status = response.body();
                    if (status != null){
                        Toast.makeText(SellerActivity.this, status.getDescription(), Toast.LENGTH_LONG).show();
                        viewModel.updateMember();
                    }else {
                        System.out.println(response.toString());
                        Toast.makeText(SellerActivity.this, "err", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<Status> call, Throwable throwable) {
                    Toast.makeText(SellerActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            }));
            builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {});

            AlertDialog alertDialog = builder.create();
            alertDialog.show();*/
        });
        //todo
        /*((PDAApplication)getApplication()).getOldApi().getSeller(sellerId).enqueue(new Callback<Seller>() {
            @Override
            public void onResponse(Call<Seller> call, Response<Seller> response) {
                Seller seller = response.body();
                if (seller!=null){
                    Timber.d(seller.toString());
                    sellerAdapter.setItems(seller.getAllItems());
                    ((TextView)findViewById(R.id.sellerName)).setText(seller.name);
                    if (seller.avatar.contains("http"))
                        Glide.with(getApplicationContext())
                                .asDrawable()
                                .centerCrop()
                                .load(seller.avatar)
                                .into(background);
                    else
                        Glide.with(getApplicationContext())
                                .asDrawable()
                                .centerCrop()
                                .load("https://" + BuildConfig.URL + "/" + seller.avatar)
                                .into(background);

                }else
                    Timber.d(response.toString());
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Seller> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });*/

        sellerView.setLayoutManager(sellerAdapter.getLayoutManager(this,3));
        sellerView.setAdapter(sellerAdapter);
    }

    void updateList(UserModel userModel){
        //TODO
        /*Data data = userModel.getData();//
        ((TextView)findViewById(R.id.playerName)).setText(userModel.getName());
        ((TextView)findViewById(R.id.playerMoney)).setText(getString(R.string.money, String.valueOf(userModel.getMoney())));
        List<Item> buyerList = data.getAllItems();
        ItemsAdapter buyerAdapter = new ItemsAdapter();
        buyerAdapter.setOnClickListener(pos -> {
            Item item = buyerAdapter.getItems().get(pos);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Вы хотите продать " + item.title + " за " + item.priceToSell() + "?");

            builder.setPositiveButton(R.string.yes, (dialogInterface, i) ->((App)getApplication()).getRetrofitService().getPdaAPI().sellItem(item.hashCode()).enqueue(new Callback<Status>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<Status> call, Response<Status> response) {
                    Status status = response.body();
                    if (status != null){
                        Toast.makeText(SellerActivity.this, status.getDescription(), Toast.LENGTH_LONG).show();
                        viewModel.updateMember();
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<Status> call, Throwable throwable) {

                }
            }));
            builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {});

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });
        buyerAdapter.setItems(buyerList);
        buyerView.setAdapter(buyerAdapter);
        buyerView.setLayoutManager(buyerAdapter.getLayoutManager(this,3));*/
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.map) {
            if (getIntent().getExtras() != null) {
                Intent intent = new Intent(this, QuestActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        }
    }
}
