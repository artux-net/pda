package net.artux.pda.ui.fragments.additional;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {

    private List<String> content;
    private final StringListClickListener listener;

    public StringAdapter(StringListClickListener listener) {
        this.listener = listener;
        content = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<String> messages) {
        this.content = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position, content.get(position));
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }

        @SuppressLint("SetTextI18n")
        void bind(int pos, String content) {
            itemView.setOnLongClickListener(view -> listener.onLongClick(pos, content));
            itemView.setOnClickListener(view -> listener.onClick(pos, content));
            textView.setText(content);
        }
    }

    public interface StringListClickListener {

        void onClick(int pos, String content);

        default boolean onLongClick(int pos, String content) {
            return false;
        }

    }

}
