package net.artux.pda.ui.fragments.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

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
    private NoteModel currentNoteModel;

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
        if (noteViewModel == null)
            noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        noteViewModel.getActiveNote().observe(getViewLifecycleOwner(), this::bindNote);
        binding.deleteButton.setOnClickListener(view1 -> noteViewModel.deleteNote());
        noteViewModel.getStatus().observe(getViewLifecycleOwner(), statusModel -> Toast.makeText(requireContext(), statusModel.getDescription(), Toast.LENGTH_SHORT).show());
    }

    public void bindNote(NoteModel note) {
        if (note != null) {
            updateNote();
            currentNoteModel = note;
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

    public void updateNote() {
        if (currentNoteModel!=null) {
            String title = binding.editNoteTitle.getEditableText().toString();
            String content = binding.editNoteContent.getEditableText().toString();
            noteViewModel.editNote(title, content, currentNoteModel.getId());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateNote();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}

