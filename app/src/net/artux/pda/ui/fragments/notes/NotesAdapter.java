package net.artux.pda.ui.fragments.notes;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.app.App;
import net.artux.pda.databinding.ItemNoteBinding;
import net.artux.pda.ui.activities.hierarhy.FragmentNavigation;
import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Note;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;
import timber.log.Timber;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    List<Note> notes = new ArrayList<>();
    OnNoteClickListener onNoteClickListener;
    FragmentNavigation.Presenter presenter;

    NotesAdapter(OnNoteClickListener onNoteClickListener, FragmentNavigation.Presenter presenter){
        this.onNoteClickListener = onNoteClickListener;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(ItemNoteBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false), onNoteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
       if (position==0){
            holder.binding.noteTitle.setText("Добавить заметку");
            holder.binding.getRoot().setBackgroundColor(Color.rgb(5, 20, 13));
            holder.binding.getRoot().setOnClickListener(view -> {
                Context context = holder.binding.getRoot().getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Создание заметки..");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Введите заголовок..");
                builder.setView(input);
                builder.setPositiveButton("Создать", (dialog, which) -> {
                            String title = input.getText().toString();
                            App.getRetrofitService().getPdaAPI().createNote(title).enqueue(new Callback<Note>() {
                                @Override
                                @EverythingIsNonNull
                                public void onResponse(Call<Note> call, Response<Note> response) {
                                    Note note = response.body();
                                    if (note != null) {
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("updated", 0);
                                        presenter.passData(bundle);
                                    }
                                }

                                @Override
                                @EverythingIsNonNull
                                public void onFailure(Call<Note> call, Throwable t) {

                                }
                            });
                        });
                builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
                builder.show();
                App.getRetrofitService().getPdaAPI().createNote("");
            });
        }else
            holder.bind(notes.get(position-1));
    }

    @Override
    public int getItemCount() {
        return notes.size()+1;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemNoteBinding binding;
        OnNoteClickListener listener;
        Note note;

        public NoteViewHolder(@NonNull ItemNoteBinding binding, OnNoteClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(Note note){
            this.note = note;
            binding.noteTitle.setText(note.title);

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getDefault());
            Instant instant = new Instant(note.time);
            DateTime time = instant.toDateTime().toDateTime(DateTimeZone.getDefault());

            binding.noteTime.setText(outputFormat.format(time.toDate()));
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(note);
        }
    }

    public interface OnNoteClickListener{
        void onClick(Note note);
    }
}
