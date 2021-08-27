package net.artux.pda.ui.fragments.quest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.ui.activities.LoginActivity;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pdalib.Member;
import net.artux.pdalib.Status;
import net.artux.pdalib.profile.Data;
import net.artux.pdalib.profile.Seller;
import net.artux.pdalib.profile.items.Item;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SellerActivity extends AppCompatActivity implements View.OnClickListener {

    List<Item> buyerList = new ArrayList<>();

    int sellerId = 0;

    ItemsAdapter sellerAdapter;

    RecyclerView buyerView;
    ImageView background;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quest3);


        sellerId = getIntent().getIntExtra("seller", 0);


        RecyclerView sellerView = findViewById(R.id.sellerList);
        buyerView = findViewById(R.id.buyerList);
        background = findViewById(R.id.sellerBackground);
        findViewById(R.id.map).setOnClickListener(this);

        updateMember();

        sellerAdapter = new ItemsAdapter();
        sellerAdapter.setOnClickListener(pos -> {
            Item item = sellerAdapter.getItems().get(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Вы хотите купить " + item.title + " за " + item.price + "?");

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    App.getRetrofitService().getPdaAPI().buyItem(item.type, sellerId, sellerAdapter.getItems().get(pos).toString()).enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(Call<Status> call, Response<Status> response) {
                            Status status = response.body();
                            System.out.println(response.headers().toString());
                            if (status != null){
                                Toast.makeText(SellerActivity.this, status.getDescription(), Toast.LENGTH_LONG).show();
                                updateMember();
                            }else {
                                Toast.makeText(SellerActivity.this, "err", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Status> call, Throwable throwable) {
                            Toast.makeText(SellerActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {});

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        App.getRetrofitService().getPdaAPI().getSeller(sellerId).enqueue(new Callback<Seller>() {
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
            public void onFailure(Call<Seller> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        sellerView.setLayoutManager(sellerAdapter.getLayoutManager(this,3));
        sellerView.setAdapter(sellerAdapter);
    }

    void updateMember(){
        App.getRetrofitService().getPdaAPI()
                .loginUser().enqueue(new Callback<Member>() {
            @Override
            public void onResponse(@NonNull Call<Member> call, @NonNull Response<Member> response) {
                Member member = response.body();
                if (response.code()==502)
                    Toast.makeText(getApplicationContext(), R.string.unable_connect, Toast.LENGTH_SHORT).show();
                else if (member!=null) {
                    System.out.println("Member updated");
                    App.getDataManager().setMember(member);
                    if (member.getData().getAllItems().size()!=buyerList.size())
                        updateList();
                } else {
                    Toast.makeText(getApplicationContext(), "Member error, try to login again", Toast.LENGTH_SHORT).show();
                    App.getDataManager().removeAllData();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    SellerActivity.this.finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Member> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.error_server_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateList(){
        Data data = App.getDataManager().getMember().getData();
        ((TextView)findViewById(R.id.playerName)).setText(App.getDataManager().getMember().getName());
        ((TextView)findViewById(R.id.playerMoney)).setText(getString(R.string.money, String.valueOf(App.getDataManager().getMember().getMoney())));
        buyerList = data.getAllItems();
        ItemsAdapter buyerAdapter = new ItemsAdapter();
        buyerAdapter.setOnClickListener(pos -> {
            Item item = buyerAdapter.getItems().get(pos);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Вы хотите продать " + item.title + " за " + item.priceToSell() + "?");

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    App.getRetrofitService().getPdaAPI().sellItem(item.type, item.toString()).enqueue(new Callback<Status>() {
                        @Override
                        public void onResponse(Call<Status> call, Response<Status> response) {
                            Status status = response.body();
                            if (status != null){
                                Toast.makeText(SellerActivity.this, status.getDescription(), Toast.LENGTH_LONG).show();
                                updateMember();
                            }
                        }

                        @Override
                        public void onFailure(Call<Status> call, Throwable throwable) {

                        }
                    });
                }
            });
            builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {});

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });
        buyerAdapter.setItems(buyerList);
        buyerView.setAdapter(buyerAdapter);
        buyerView.setLayoutManager(buyerAdapter.getLayoutManager(this,3));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.map:
                if (getIntent().getExtras()!=null) {
                    Intent intent = new Intent(this, QuestActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                    finish();
                    break;
                }
        }
    }
}
