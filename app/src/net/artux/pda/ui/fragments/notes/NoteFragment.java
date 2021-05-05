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
import net.artux.pdalib.profile.Note;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NoteFragment extends BaseFragment {

    FragmentNoteBinding binding;
    Note note;

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
            note = (Note) getArguments().getSerializable("note");
            if (note!=null)
                bindNote(note);
        }else{

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

        binding.deleteButton.setOnClickListener(view1 -> App.getRetrofitService().getPdaAPI().deleteNote(note.cid).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member = response.body();
                if (member!=null) {
                    App.getDataManager().setMember(member);
                    Bundle bundle = new Bundle();
                    bundle.putInt("updated", 1);
                    bundle.putInt("deleted", 1);
                    navigationPresenter.passData(bundle);
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Timber.e(t);
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
        if (note==null)
            note = new Note();
        note.title = binding.editNoteTitle.getEditableText().toString();
        note.content = binding.editNoteContent.getEditableText().toString();
        App.getRetrofitService().getPdaAPI().updateNote(note).enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Member member = response.body();
                if (member!=null){
                    App.getDataManager().setMember(member);
                    Bundle bundle = new Bundle();
                    bundle.putInt("updated", 0);
                    navigationPresenter.passData(bundle);
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Timber.e(t);
            }
        });
    }
}

