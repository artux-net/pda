package net.artux.pda.ui.fragments.notes;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.databinding.FragmentAdditionalNotesBinding;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.model.profile.NoteModel;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pda.ui.viewmodels.NoteViewModel;

public class NotesFragment extends AdditionalBaseFragment implements NotesAdapter.OnNoteClickListener {

    private FragmentAdditionalNotesBinding binding;
    private FragmentListBinding listBinding;
    private NotesAdapter notesAdapter;
    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdditionalNotesBinding.inflate(inflater, container, false);
        listBinding = binding.listContainer;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        notesAdapter = new NotesAdapter(this);
        listBinding.list.setAdapter(notesAdapter);

        noteViewModel.getNotes().observe(getViewLifecycleOwner(), noteModels -> {
            if (noteModels.size()>0) {
                listBinding.list.setVisibility(View.VISIBLE);
                listBinding.viewMessage.setVisibility(View.GONE);
                notesAdapter.setNotes(noteModels);
            }else {
                listBinding.list.setVisibility(View.GONE);
                listBinding.viewMessage.setVisibility(View.VISIBLE);
            }
        });
        binding.noteCreateBtn.setOnClickListener(view1 -> {
            Context context = requireContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Создание заметки..");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("Введите заголовок..");
            builder.setView(input);
            builder.setPositiveButton("Создать", (dialog, which) -> {
                String title = input.getText().toString();
                noteViewModel.createNote(title, "");
            });
            builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        noteViewModel.updateNotes();
    }

    @Override
    public void onClick(NoteModel note) {
        noteViewModel.openNote(note.getId());
    }
}
