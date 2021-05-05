package net.artux.pda.ui.fragments.stories;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pda.ui.fragments.quest.models.StoryItem;

public class StoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    StoriesAdapter.OnStoryClickListener listener;
    StoryItem storyItem;

    public StoryHolder(@NonNull View itemView, StoriesAdapter.OnStoryClickListener listener) {
        super(itemView);
        this.listener = listener;
    }

    public void bind(StoryItem storyItem){
        this.storyItem = storyItem;
        ((TextView)itemView.findViewById(R.id.storyTitle)).setText(storyItem.title);
        ((TextView)itemView.findViewById(R.id.storyDesc)).setText(storyItem.desc);
        if (storyItem.iconUrl != null)
        if (storyItem.iconUrl.contains("http"))
        Glide.with(itemView)
                .load(storyItem.iconUrl)
                .into((ImageView) itemView.findViewById(R.id.storyIcon));
        else
            Glide.with(itemView)
                    .load("https://" + BuildConfig.URL + "/" + storyItem.iconUrl)
                    .into((ImageView) itemView.findViewById(R.id.storyIcon));
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        listener.onClick(storyItem.id);
    }
}
