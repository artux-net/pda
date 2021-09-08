package net.artux.pda.ui.fragments.notes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.app.App;
import net.artux.pda.databinding.FragmentNoteBinding;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;
import net.artux.pdalib.Member;
import net.artux.pdalib.Status;
import net.artux.pdalib.profile.Note;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NoteFragment extends BaseFragment {

    FragmentNoteBinding binding;
    Note selectedNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (getArguments()!=null){
            selectedNote = (Note) getArguments().getSerializable("note");
            if (selectedNote !=null)
                bindNote(selectedNote);
        }


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateNote();
            }
        };

        binding.editNoteTitle.addTextChangedListener(textWatcher);
        binding.editNoteContent.addTextChangedListener(textWatcher);

        binding.deleteButton.setOnClickListener(view1 -> App.getRetrofitService().getPdaAPI()
                .deleteNote(selectedNote.cid).enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(Call<Status> call, Response<Status> response) {
                        Status status = response.body();
                        if (status!=null){
                            Member member = App.getDataManager().getMember();
                            Iterator<Note> iterator = member.notes.iterator();
                            while(iterator.hasNext()){
                                Note note = iterator.next();
                                if(note.cid==selectedNote.cid)
                                   iterator.remove();
                            }
                            App.getDataManager().setMember(member);
                            Bundle bundle = new Bundle();
                            bundle.putInt("updated", 1);
                            bundle.putInt("deleted", 1);
                            navigationPresenter.passData(bundle);
                        }
                    }

                    @Override
                    public void onFailure(Call<Status> call, Throwable t) {

                    }
                }));
    }

    public void bindNote(Note note){
        binding.viewMessage.setVisibility(View.GONE);
        binding.editNoteView.setVisibility(View.VISIBLE);
        binding.editNoteTitle.setText(note.title);
        binding.editNoteContent.setText(note.content);
        SimpleDateFormat outputFormat =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getDefault());
        Instant instant = new Instant(note.time);
        DateTime time = instant.toDateTime().toDateTime(DateTimeZone.getDefault());

        binding.noteTime.setText(outputFormat.format(time.toDate()));
    }

    public void updateNote(){
        if (selectedNote ==null)
            selectedNote = new Note();
        selectedNote.title = binding.editNoteTitle.getEditableText().toString();
        selectedNote.content = binding.editNoteContent.getEditableText().toString();
        App.getRetrofitService().getPdaAPI().updateNote(selectedNote).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                Note note = response.body();
                if (note!=null){
                    Member member = App.getDataManager().getMember();
                    Iterator<Note> iterator = member.notes.iterator();
                    while(iterator.hasNext()){
                        Note note1 = iterator.next();
                        if(note1.cid==selectedNote.cid)
                            iterator.remove();
                    }
                    member.notes.add(0, note);
                    App.getDataManager().setMember(member);
                    Bundle bundle = new Bundle();
                    bundle.putInt("updated", 0);
                    navigationPresenter.passData(bundle);
                    Timber.d("Note updated");
                }

            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Timber.e(t);
            }
        });
    }
}

