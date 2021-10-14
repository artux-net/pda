package net.artux.pda.ui.fragments.stories;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.ui.fragments.quest.models.StoryItem;

import java.util.ArrayList;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoryHolder> {

    private List<StoryItem> stories = new ArrayList<>();
    private final OnStoryClickListener listener;

    public StoriesAdapter( OnStoryClickListener listener) {
        this.listener = listener;
    }

    public void setStories(List<StoryItem> stories) {
        this.stories = stories;
        StoryItem item = new StoryItem();
        item.id = -1;
        item.title = "Загрузка стадии на выбор";
        item.desc = "..";

        this.stories.add(item);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story,  parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryHolder holder, int position) {
        holder.bind(stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }


    public interface OnStoryClickListener {
        void onClick(int id);
    }
}
