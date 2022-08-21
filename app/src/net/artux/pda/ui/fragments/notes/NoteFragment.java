package net.artux.pda.ui.fragments.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.artux.pda.app.App;
import net.artux.pda.databinding.FragmentNoteBinding;
import net.artux.pda.model.StatusModel;
import net.artux.pda.model.profile.NoteModel;
import net.artux.pda.ui.activities.hierarhy.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NoteFragment extends BaseFragment {

    FragmentNoteBinding binding;
    NoteModel selectedNote;

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
        if (data != null) {
            updateNote();
            selectedNote = (NoteModel) data.getSerializable("note");
            if (selectedNote != null) {
                bindNote(selectedNote);
            }
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.deleteButton.setOnClickListener(view1 -> ((App) getActivity().getApplication()).getOldApi()
                .deleteNote(0).enqueue(new Callback<StatusModel>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<StatusModel> call, Response<StatusModel> response) {
                        StatusModel status = response.body();
                        if (status != null) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("updated", 1);
                            navigationPresenter.passData(bundle);

                            binding.viewMessage.setVisibility(View.VISIBLE);
                            binding.editNoteView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    @EverythingIsNonNull
                    public void onFailure(Call<StatusModel> call, Throwable t) {

                    }
                }));
    }

    public void bindNote(NoteModel note) {
        binding.viewMessage.setVisibility(View.GONE);
        binding.editNoteView.setVisibility(View.VISIBLE);
        binding.editNoteTitle.setText(note.getTitle());
        binding.editNoteContent.setText(note.getContent());
        SimpleDateFormat outputFormat =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        outputFormat.setTimeZone(TimeZone.getDefault());
        binding.noteTime.setText(outputFormat.format(note.getTime()));
    }

    public void updateNote() {
        String title = binding.editNoteTitle.getEditableText().toString();
        String content = binding.editNoteContent.getEditableText().toString();
        if (selectedNote != null && (!selectedNote.getTitle().equals(title) || !selectedNote.getContent().equals(content))) {
            selectedNote.setTitle(title);
            selectedNote.setContent(content);
            ((App) getActivity().getApplication()).getOldApi().updateNote(selectedNote).enqueue(new Callback<NoteModel>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<NoteModel> call, Response<NoteModel> response) {
                    NoteModel note = response.body();
                    if (note != null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("updated", 1);
                        navigationPresenter.passData(bundle);
                        Timber.d("Note updated");
                    }

                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<NoteModel> call, Throwable t) {
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

