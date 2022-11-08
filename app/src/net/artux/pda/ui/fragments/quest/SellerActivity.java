package net.artux.pda.ui.fragments.quest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentQuest3Binding;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.ui.activities.QuestActivity;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pda.ui.viewmodels.ItemsViewModel;
import net.artux.pda.ui.viewmodels.SellerViewModel;
import net.artux.pda.ui.viewmodels.UserViewModel;
import net.artux.pda.utils.URLHelper;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SellerActivity extends AppCompatActivity implements View.OnClickListener {

    private ItemsAdapter sellerAdapter;
    private ItemsAdapter userAdapter;
    private RecyclerView buyerView;
    private ImageView background;

    private FragmentQuest3Binding binding;

    private ItemsViewModel itemsViewModel;
    private SellerViewModel sellerViewModel;
    private UserViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentQuest3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(UserViewModel.class);
        itemsViewModel = provider.get(ItemsViewModel.class);
        sellerViewModel = provider.get(SellerViewModel.class);

        long sellerId = getIntent().getIntExtra("seller", 0);

        RecyclerView sellerView = findViewById(R.id.sellerList);
        buyerView = findViewById(R.id.buyerList);
        background = findViewById(R.id.sellerBackground);
        binding.map.setOnClickListener(this);

       /* itemsViewModel.getStoryData().observe(this, new Observer<StoryDataModel>() {
            @Override
            public void onChanged(StoryDataModel dataModel) {
                List<ItemModel> items = dataModel.getAllItems();
                if (items.size() > 0) {
                    userAdapter.setItems(items);
                }
                binding.playerMoney.setText(getString(R.string.money, String.valueOf(dataModel.getMoney())));
            }
        });*/

        sellerAdapter = new ItemsAdapter();
        sellerAdapter.setOnClickListener(item -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Вы хотите купить " + item.getTitle() + " за " + item.getPrice() + "?");


            builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
        binding.sellerList.setAdapter(sellerAdapter);
        userAdapter = new ItemsAdapter();
        userAdapter.setOnClickListener(item -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Вы хотите продать " + item.getTitle() + " за " + item.getPrice() + "?");
            builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        sellerViewModel.getSeller().observe(this, sellerModel -> {
            List<ItemModel> items = sellerModel.getAllItems();

            binding.sellerName.setText(sellerModel.getName());
            String imageUrl = URLHelper.getResourceURL(sellerModel.getImage());
            Glide.with(getApplicationContext())
                    .asDrawable()
                    .centerCrop()
                    .load(imageUrl)
                    .into(background);

            if (items.size() > 0) {
                sellerAdapter.setItems(items);
            }
        });
        sellerViewModel.updateFromCache(sellerId);
        sellerViewModel.update(sellerId);

        sellerView.setLayoutManager(sellerAdapter.getLayoutManager(this, 3));
        sellerView.setAdapter(sellerAdapter);
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
