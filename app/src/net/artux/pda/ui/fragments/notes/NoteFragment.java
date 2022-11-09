package net.artux.pda.ui.fragments.notes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.R;
import net.artux.pda.databinding.FragmentNoteBinding;
import net.artux.pda.model.profile.NoteModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pda.ui.viewmodels.NoteViewModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NoteFragment extends BaseFragment {

    private FragmentNoteBinding binding;
    private NoteViewModel noteViewModel;
    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.UK)
                    .withZone(ZoneId.systemDefault());

    {
        defaultAdditionalFragment = NotesFragment.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);
        if (navigationPresenter!=null) {
            navigationPresenter.setTitle(getResources().getString(R.string.notes));
            navigationPresenter.setLoadingState(true);
        }
        noteViewModel.getActiveNote().observe(getViewLifecycleOwner(), this::bindNote);
        binding.deleteButton.setOnClickListener(view1 -> noteViewModel.deleteNote());
        binding.editNoteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                noteViewModel.editContent(editable.toString());
            }
        });
        binding.editNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                noteViewModel.editTitle(editable.toString());
            }
        });
        noteViewModel.getStatus().observe(getViewLifecycleOwner(), statusModel -> Toast.makeText(requireContext(), statusModel.getDescription(), Toast.LENGTH_SHORT).show());
        noteViewModel.updateNotes();
    }

    public void bindNote(NoteModel note) {
        if (note != null) {
            binding.viewMessage.setVisibility(View.GONE);
            binding.editNoteView.setVisibility(View.VISIBLE);
            binding.editNoteTitle.setText(note.getTitle());
            binding.editNoteContent.setText(note.getContent());
            binding.noteTime.setText(timeFormatter.format(note.getTime()));
        } else {
            binding.viewMessage.setVisibility(View.VISIBLE);
            binding.editNoteView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        noteViewModel.syncActiveNote();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

