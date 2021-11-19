package net.artux.pda.ui.fragments.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.databinding.FragmentListBinding;
import net.artux.pda.ui.activities.hierarhy.AdditionalBaseFragment;
import net.artux.pdalib.profile.Note;

public class NotesFragment extends AdditionalBaseFragment implements NotesAdapter.OnNoteClickListener {

    FragmentListBinding binding;
    NotesAdapter notesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.list.setVisibility(View.VISIBLE);
        binding.viewMessage.setVisibility(View.GONE);

        notesAdapter = new NotesAdapter(this, navigationPresenter);
        binding.list.setAdapter(notesAdapter);

        notesAdapter.setNotes(App.getDataManager().getMember().notes);
    }

    @Override
    public void receiveData(Bundle data) {
        super.receiveData(data);
        if (data.containsKey("updated")){
            notesAdapter.setNotes(App.getDataManager().getMember().notes);
        }
    }

    @Override
    public void onClick(int cid) {
        Note note = new Note();
        for (Note note1: App.getDataManager().getMember().notes){
            if (cid == note1.cid){
                note = note1;
            }
        }
        NoteFragment noteFragment = new NoteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("note", note);
        noteFragment.setArguments(bundle);
        navigationPresenter.passData(bundle);
    }
}
