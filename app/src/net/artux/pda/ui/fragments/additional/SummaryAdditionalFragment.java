package net.artux.pda.ui.fragments.additional;

import static net.artux.pda.ui.util.FragmentExtKt.getViewModelFactory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentAddProfileBinding;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.viewmodels.SummaryViewModel;

import org.jetbrains.annotations.Nullable;

public class SummaryAdditionalFragment extends AdditionalBaseFragment {

    FragmentAddProfileBinding binding;
    ArrayAdapter<String> adapter;
    private SummaryViewModel summaryViewModel;

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

            String[] ids = summaryViewModel.getAllIds();

            adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, ids);
            binding.menuProfile.setAdapter(adapter);

            binding.menuProfile.setOnItemClickListener((parent, view1, position, id) -> {
                Bundle bundle = new Bundle();
                bundle.putString("loadSummary", ids[position]);
                navigationPresenter.passData(bundle);
            });
        }
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
