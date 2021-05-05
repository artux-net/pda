package net.artux.pda.ui.fragments.profile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.app.App;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupHolder> {

    List<Integer> relations = new ArrayList<>();

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        holder.bind(position, relations.get(position));
    }

    @Override
    public int getItemCount() {
        if (relations==null){
            return 0;
        }else{
            return relations.size();
        }
    }

    public void setRelations(List<Integer> relations){
        this.relations = relations;
        notifyDataSetChanged();
    }

    static class GroupHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView avatar;
        int[] blocks = {
                R.id.b1,
                R.id.b2,
                R.id.b3,
                R.id.b4,
                R.id.b5,
                R.id.b6,
                R.id.b7,
                R.id.b8,
                R.id.b9,
                R.id.b10
        };


        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.group_title);
            avatar = itemView.findViewById(R.id.group_avatar);
        }

        void bind(int key, int relation){
            title.setText(title.getContext().getResources().getStringArray(R.array.groups)[key]);
            avatar.setImageDrawable(avatar.getContext().getResources().getDrawable(App.group_avatars[key]));

            if (relation<0){
                if (relation<-5) relation=-5;
                while(relation!=0){
                    ((ImageView)title.getRootView().findViewById(blocks[relation+5]))
                            .setImageDrawable(title.getContext().getResources().getDrawable(R.drawable.red_block));
                    relation=relation+1;
                }
            }else if (relation>0){
                if (relation>5) relation = 5;
                while(relation!=0){
                    ((ImageView)title.getRootView().findViewById(blocks[relation+4]))
                            .setImageDrawable(title.getContext().getResources().getDrawable(R.drawable.green_block));
                    relation = relation-1;
                }
            }
        }

    }

}
