package net.artux.pda.ui.fragments.quest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentQuest3Binding;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.repositories.SellerRepository;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;
import net.artux.pda.ui.fragments.profile.helpers.ItemsHelper;
import net.artux.pda.ui.viewmodels.QuestViewModel;
import net.artux.pda.ui.viewmodels.SellerViewModel;
import net.artux.pda.utils.URLHelper;

import java.text.DecimalFormat;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SellerFragment extends Fragment implements View.OnClickListener {

    private final DecimalFormat formater = new DecimalFormat("##.##");
    private ItemsAdapter sellerAdapter;
    private ItemsAdapter userAdapter;
    private ImageView background;
    private SellerViewModel sellerViewModel;

    private FragmentQuest3Binding binding;

    public static SellerFragment newInstance(int sellerId) {
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
        sellerViewModel = provider.get(SellerViewModel.class);
        QuestViewModel questViewModel = provider.get(QuestViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            long sellerId = args.getInt("seller", 0);

            RecyclerView sellerView = view.findViewById(R.id.sellerList);
            RecyclerView buyerView = view.findViewById(R.id.buyerList);
            sellerAdapter = new ItemsAdapter();
            userAdapter = new ItemsAdapter();

            sellerAdapter.setOnClickListener(item -> showDialog(SellerRepository.OperationType.BUY, item));
            userAdapter.setOnClickListener(item -> showDialog(SellerRepository.OperationType.SELL, item));

            background = view.findViewById(R.id.sellerBackground);
            binding.map.setOnClickListener(this);

            questViewModel.getStoryData().observe(getViewLifecycleOwner(), dataModel -> {
                List<ItemModel> items = dataModel.getAllItems();
                if (items.size() > 0) {
                    userAdapter.setItems(items);
                }
                binding.playerMoney.setText(getString(R.string.money, String.valueOf(dataModel.getMoney())));
            });

            sellerViewModel.updateSeller(sellerId);

            sellerView.setLayoutManager(sellerAdapter.getLayoutManager(requireContext(), 3));
            sellerView.setAdapter(sellerAdapter);

            buyerView.setLayoutManager(userAdapter.getLayoutManager(requireContext(), 3));
            buyerView.setAdapter(userAdapter);

            sellerViewModel.getSeller().observe(getViewLifecycleOwner(), sellerModel -> {
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
            });
            sellerViewModel.getStatus().observe(getViewLifecycleOwner(), statusModel -> {
                questViewModel.updateStoryDataFromCache();
                sellerViewModel.updateSeller(sellerId);
                Toast.makeText(requireContext(), statusModel.getDescription(), Toast.LENGTH_SHORT).show();
            });
        } else
            throw new RuntimeException();
    }

    private void showDialog(SellerRepository.OperationType operationType, ItemModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.PDADialogStyle);
        float coefficient;
        if (operationType == SellerRepository.OperationType.BUY) {
            builder.setTitle(getString(R.string.buy_question, item.getTitle()));
            coefficient = sellerViewModel.getBuyCoefficient();
        } else {
            builder.setTitle(getString(R.string.sell_question, item.getTitle()));
            coefficient = sellerViewModel.getSellerCoefficient();
        }

        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(requireContext());
        textView.setPadding(10, 10, 10, 10);
        float price = item.getPrice() * coefficient;
        textView.setText(getString(R.string.cost, formater.format(price)));
        linearLayout.addView(textView);
        builder.setView(linearLayout);

        if (item.getQuantity() > 1) {
            final NumberPicker numberPicker = new NumberPicker(getActivity());
            numberPicker.setMaxValue(item.getQuantity());
            numberPicker.setMinValue(1);
            linearLayout.addView(numberPicker);

            numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                float price1 = item.getPrice() * coefficient * newVal;
                textView.setText(getString(R.string.cost, formater.format(price1)));
            });

            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                if (operationType == SellerRepository.OperationType.BUY) {
                    sellerViewModel.buyItem(item.getId(), numberPicker.getValue());
                } else {
                    sellerViewModel.sellItem(item.getId(), numberPicker.getValue());
                }
            });
        } else
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                if (operationType == SellerRepository.OperationType.BUY) {
                    sellerViewModel.buyItem(item.getId(), 1);
                } else {
                    sellerViewModel.sellItem(item.getId(), 1);
                }
            });


        builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {
        });
        builder.setNeutralButton(R.string.info, ((dialog, which) -> showInfoDialog(item)));

        builder.create().show();
    }

    private void showInfoDialog(ItemModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.PDADialogStyle);
        builder.setTitle(item.getTitle());
        builder.setMessage(ItemsHelper.getDesc(item, getContext()));
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.map) {
            getParentFragmentManager().popBackStack();
        }
    }
}
