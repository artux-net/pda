package net.artux.pda.ui.fragments.stories;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.model.quest.StoryItem;

public class StoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    StoriesAdapter.OnStoryClickListener listener;
    StoryItem storyItem;

    public StoryHolder(@NonNull View itemView, StoriesAdapter.OnStoryClickListener listener) {
        super(itemView);
        this.listener = listener;
    }

    public void bind(StoryItem storyItem) {
        this.storyItem = storyItem;
        ((TextView) itemView.findViewById(R.id.storyTitle)).setText(storyItem.getTitle());
        ((TextView) itemView.findViewById(R.id.storyDesc)).setText(storyItem.getDesc());
        if (storyItem.getIconUrl() != null)
            if (storyItem.getIconUrl().contains("http"))
                Glide.with(itemView)
                        .load(storyItem.getIconUrl())
                        .into((ImageView) itemView.findViewById(R.id.storyIcon));
            else
                Glide.with(itemView)
                        .load("https://" + BuildConfig.URL + "/" + storyItem.getIconUrl())
                        .into((ImageView) itemView.findViewById(R.id.storyIcon));
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        listener.onClick(storyItem.getId());
    }
}
