package net.artux.pda.ui.fragments.additional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.Summary;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.viewmodels.SummaryViewModel;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SummaryAdditionalFragment extends AdditionalBaseFragment implements StringAdapter.StringListClickListener {

    private FragmentListBinding binding;
    private StringAdapter adapter;
    private SummaryViewModel summaryViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter != null)
            navigationPresenter.setAdditionalTitle(getString(R.string.kinds));
        if (summaryViewModel == null)
            summaryViewModel = new ViewModelProvider(requireActivity()).get(SummaryViewModel.class);

        adapter = new StringAdapter(this);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.list.setAdapter(adapter);

        summaryViewModel.getSummaries().observe(getViewLifecycleOwner(), summaries -> {
            if (summaries.size() > 0) {
                binding.list.setVisibility(View.VISIBLE);
                binding.viewMessage.setVisibility(View.GONE);
                List<String> content = summaries.stream()
                        .map(Summary::getTitle)
                        .collect(Collectors.toList());
                adapter.setItems(content);
            } else {
                binding.viewMessage.setVisibility(View.VISIBLE);
                binding.list.setVisibility(View.GONE);
            }
        });
        summaryViewModel.updateSummaries();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(int pos, String content) {
        summaryViewModel.openSummary(content);
    }

    @Override
    public boolean onLongClick(int pos, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.PDADialogStyle);
        builder.setTitle("Удалить сводку от " + content + "?");
        builder.setPositiveButton("Да", (dialog, which) -> {
            summaryViewModel.removeSummary(content);
        });
        builder.setNegativeButton("Нет", (dialog, which) -> {
        });
        builder.show();
        return true;
    }
}
