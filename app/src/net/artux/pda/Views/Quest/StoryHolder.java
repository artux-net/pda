package net.artux.pda.Views.Quest;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.artux.pda.R;
import net.artux.pda.Views.Quest.Models.StoriesAdapter;
import net.artux.pda.Views.Quest.Models.StoryItem;

public class StoryHolder extends RecyclerView.ViewHolder {

    public StoryHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(StoryItem storyItem, StoriesAdapter.OnItemClickListener listener){
        ((TextView)itemView.findViewById(R.id.storyTitle)).setText(storyItem.title);
        ((TextView)itemView.findViewById(R.id.storyDesc)).setText(storyItem.desc);
        Glide.with(itemView)
                .load(storyItem.iconUrl)
                .into((ImageView) itemView.findViewById(R.id.storyIcon));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(storyItem.id);
            }
        });
    }
}
