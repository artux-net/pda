package net.artux.pda.ui.fragments.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.databinding.ItemNoteBinding;
import net.artux.pda.model.profile.NoteModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    List<NoteModel> notes = new LinkedList<>();
    DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.UK)
                    .withZone(ZoneId.systemDefault());
    OnNoteClickListener onNoteClickListener;

    NotesAdapter(OnNoteClickListener onNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(ItemNoteBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false), onNoteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<NoteModel> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemNoteBinding binding;
        OnNoteClickListener listener;
        NoteModel note;

        public NoteViewHolder(@NonNull ItemNoteBinding binding, OnNoteClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(NoteModel note) {
            this.note = note;
            binding.noteTitle.setText(note.getTitle());
            binding.noteTime.setText(timeFormatter.format(note.getTime()));
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(note);
        }
    }

    public interface OnNoteClickListener {
        void onClick(NoteModel note);
    }
}
