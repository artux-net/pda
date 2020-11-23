package net.artux.pda.views.quest;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.views.quest.models.StoryItem;

import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoryHolder> {

    private List<StoryItem> stories;
    private OnItemClickListener listener;

    public StoriesAdapter(List<StoryItem> stories, OnItemClickListener listener) {
        this.stories = stories;
        StoryItem item = new StoryItem();
        item.id = -1;
        item.title = "Load any stage.";
        item.desc = "Load any stage that you want.";

        this.stories.add(item);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story,  parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoryHolder holder, int position) {
        holder.bind(stories.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }


    public interface OnItemClickListener{
        void onClick(int id);
    }
}
