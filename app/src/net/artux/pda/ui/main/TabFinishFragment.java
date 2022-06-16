package net.artux.pda.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import net.artux.pda.databinding.FragmentHelpBinding;
import net.artux.pda.databinding.FragmentHelpStartBinding;
import net.artux.pda.ui.activities.RegisterActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class TabFinishFragment extends Fragment {

    private FragmentHelpStartBinding binding;

    public static TabFinishFragment newInstance() {
        return new TabFinishFragment();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentHelpStartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.oldPlayerButton.setOnClickListener(view -> {
            Toast.makeText(requireContext(), "Для этого необходимо ввойти в учетную запись", Toast.LENGTH_LONG).show();
            requireActivity().finish();
        });
        binding.newPlayerButton.setOnClickListener(view -> {
            Toast.makeText(requireContext(), "Начнем с учетной записи, зарегистрируйся", Toast.LENGTH_LONG).show();
            startActivity(new Intent(requireActivity(), RegisterActivity.class));
            requireActivity().finish();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}