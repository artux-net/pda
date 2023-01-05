package net.artux.pda.ui.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentQuest3Binding;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.SellerModel;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pda.ui.viewmodels.ItemsViewModel;
import net.artux.pda.ui.viewmodels.SellerViewModel;
import net.artux.pda.utils.URLHelper;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SellerFragment extends Fragment implements View.OnClickListener {

    private ItemsAdapter sellerAdapter;
    private ItemsAdapter userAdapter;
    private ImageView background;

    private FragmentQuest3Binding binding;

    static SellerFragment newInstance(int sellerId) {
        SellerFragment sellerFragment = new SellerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("seller", sellerId);
        sellerFragment.setArguments(bundle);
        return sellerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuest3Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        ItemsViewModel itemsViewModel = provider.get(ItemsViewModel.class);
        SellerViewModel sellerViewModel = provider.get(SellerViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            long sellerId = args.getInt("seller", 0);

            RecyclerView sellerView = view.findViewById(R.id.sellerList);
            RecyclerView buyerView = view.findViewById(R.id.buyerList);

            background = view.findViewById(R.id.sellerBackground);
            binding.map.setOnClickListener(this);

            itemsViewModel.getStoryData().observe(getViewLifecycleOwner(), dataModel -> {
                List<ItemModel> items = dataModel.getAllItems();
                if (items.size() > 0) {
                    userAdapter.setItems(items);
                }
                binding.playerMoney.setText(getString(R.string.money, String.valueOf(dataModel.getMoney())));
            });

            sellerAdapter = new ItemsAdapter();
            sellerAdapter.setOnClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
                if (item.getQuantity() > 1) {
                    final NumberPicker numberPicker = new NumberPicker(getActivity());
                    numberPicker.setMaxValue(item.getQuantity());
                    numberPicker.setMinValue(1);
                    builder.setView(numberPicker);
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        sellerViewModel.buyItem(item.getId(), numberPicker.getValue());
                    });
                }else{
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        sellerViewModel.buyItem(item.getId(), 1);
                    });
                }

                float price = item.getPrice() * sellerViewModel.getBuyCoefficient();
                builder.setTitle("Вы хотите купить " + item.getTitle() + "?");
                builder.setMessage("Ориентировочная стоимость за штуку: " + price + " RU");
                builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
            binding.sellerList.setAdapter(sellerAdapter);

            userAdapter = new ItemsAdapter();
            userAdapter.setOnClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
                if (item.getQuantity() > 1) {
                    final NumberPicker numberPicker = new NumberPicker(getActivity());
                    numberPicker.setMaxValue(item.getQuantity());
                    numberPicker.setMinValue(1);
                    builder.setView(numberPicker);
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        sellerViewModel.sellItem(item.getId(), numberPicker.getValue());
                    });
                }else{
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        sellerViewModel.sellItem(item.getId(), 1);
                    });
                }

                float price = item.getPrice() * sellerViewModel.getSellerCoefficient();

                builder.setTitle("Вы хотите продать " + item.getTitle() + "?");
                builder.setMessage("Ориентировочная стоимость за штуку: " + price + " RU");
                builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

            itemsViewModel.updateData();
            sellerViewModel.update(sellerId);

            sellerView.setLayoutManager(sellerAdapter.getLayoutManager(requireContext(), 3));
            sellerView.setAdapter(sellerAdapter);

            sellerViewModel.getSeller().observe(getViewLifecycleOwner(), new Observer<SellerModel>() {
                @Override
                public void onChanged(SellerModel sellerModel) {
                    if (sellerModel != null) {
                        sellerAdapter.setItems(sellerModel.getAllItems());
                        binding.sellerName.setText(sellerModel.getName());
                        String imageUrl = URLHelper.getResourceURL(sellerModel.getImage());
                        Glide.with(requireContext())
                                .asDrawable()
                                .centerCrop()
                                .load(imageUrl)
                                .into(background);
                    }
                }
            });
            sellerViewModel.getStatus().observe(getViewLifecycleOwner(), statusModel ->
                    Toast.makeText(requireContext(), statusModel.getDescription(), Toast.LENGTH_SHORT).show());

        } else
            throw new RuntimeException();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.map) {
            getParentFragmentManager().popBackStack();
        }
    }
}
