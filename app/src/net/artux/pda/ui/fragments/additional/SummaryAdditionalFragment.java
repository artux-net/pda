package net.artux.pda.ui.fragments.additional;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentAddProfileBinding;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.viewmodels.SummaryViewModel;

import org.jetbrains.annotations.Nullable;

public class SummaryAdditionalFragment extends AdditionalBaseFragment {

    FragmentAddProfileBinding binding;
    ArrayAdapter<String> adapter;
    private SummaryViewModel summaryViewModel;
    private String[] ids;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddProfileBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationPresenter != null)
            navigationPresenter.setAdditionalTitle(getString(R.string.kinds));
        if (getActivity() != null) {
            binding.menuProfile.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            if (summaryViewModel == null)
                summaryViewModel = getViewModelFactory(this).create(SummaryViewModel.class);

            ids = summaryViewModel.getAllIds();

            updateAdapter();

            binding.menuProfile.setOnItemClickListener((parent, view1, position, id) -> {
                Bundle bundle = new Bundle();
                bundle.putString("loadSummary", ids[position]);
                navigationPresenter.passData(bundle);
            });
            binding.menuProfile.setOnItemLongClickListener((parent, view12, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle);
                builder.setTitle("Удалить сводку от " + ids[position] + "?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        summaryViewModel.removeSummary(ids[position]);
                        updateAdapter();
                        Bundle bundle = new Bundle();
                        bundle.putString("reset", "");
                        navigationPresenter.passData(bundle);
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

                return false;
            });
        }
    }

    void updateAdapter(){
        ids = summaryViewModel.getAllIds();

        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, ids);
        binding.menuProfile.setAdapter(adapter);
    }

    @Override
    public void receiveData(Bundle data) {
        super.receiveData(data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
