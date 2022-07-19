package net.artux.pda.ui.fragments.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.app.App;
import net.artux.pda.databinding.FragmentNoteBinding;
import net.artux.pda.models.Status;
import net.artux.pda.models.profile.Note;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;
import timber.log.Timber;

public class NoteFragment extends BaseFragment {

    FragmentNoteBinding binding;
    Note selectedNote;
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
    public void receiveData(Bundle data) {
        super.receiveData(data);
        if (data!=null){
            updateNote();
            selectedNote = (Note) data.getSerializable("note");
            if (selectedNote!=null) {
                bindNote(selectedNote);
            }
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.deleteButton.setOnClickListener(view1 -> App.getRetrofitService().getPdaAPI()
                .deleteNote(selectedNote.cid).enqueue(new Callback<Status>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<Status> call, Response<Status> response) {
                        Status status = response.body();
                        if (status!=null){
                            Bundle bundle = new Bundle();
                            bundle.putInt("updated", 1);
                            navigationPresenter.passData(bundle);

                            binding.viewMessage.setVisibility(View.VISIBLE);
                            binding.editNoteView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    @EverythingIsNonNull
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
        String title = binding.editNoteTitle.getEditableText().toString();
        String content = binding.editNoteContent.getEditableText().toString();
        if (selectedNote !=null && (!selectedNote.title.equals(title) || !selectedNote.content.equals(content))) {
            selectedNote.title = title;
            selectedNote.content = content;
            App.getRetrofitService().getPdaAPI().updateNote(selectedNote).enqueue(new Callback<Note>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<Note> call, Response<Note> response) {
                    Note note = response.body();
                    if (note != null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("updated", 1);
                        navigationPresenter.passData(bundle);
                        Timber.d("Note updated");
                    }

                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<Note> call, Throwable t) {
                    Timber.e(t);
                }
            });
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

