package net.artux.pda.ui.fragments.profile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.GangRelation;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.util.EnumMap;

public class GroupRelationsAdapter extends RecyclerView.Adapter<GroupRelationsAdapter.GroupHolder> {

    private GangRelation objectRelation = new GangRelation();
    private final EnumMap<Gang, Integer> map = new EnumMap<>(Gang.class);

    public GroupRelationsAdapter() {
        updateContent();
    }

    private void updateContent() {
        for (Gang gang : Gang.values()) {
            map.put(gang, objectRelation.getFor(gang));
        }
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        Gang gang = Gang.values()[position];
        holder.bind(gang, map.get(gang));
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRelations(GangRelation objectRelation) {
        this.objectRelation = objectRelation;
        updateContent();
        notifyDataSetChanged();
    }

    static class GroupHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView avatar;
        private final Context context;
        private final View root;
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
            context = title.getContext();
            root = title.getRootView();
        }

        void bind(Gang gang, int relation) {
            title.setText(context.getResources().getStringArray(R.array.groups)[gang.getId()]);
            ProfileHelper.setGangAvatar(avatar, gang);
            relation /= 20;

            if (relation < 0) {
                if (relation < -5) relation = -5;
                while (relation != 0) {
                    ((ImageView) root.findViewById(blocks[relation + 5]))
                            .setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.red_block));
                    relation = relation + 1;
                }
            } else if (relation > 0) {
                if (relation > 5) relation = 5;
                while (relation != 0) {
                    ((ImageView) root.findViewById(blocks[relation + 4]))
                            .setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.green_block));
                    relation = relation - 1;
                }
            }
        }

    }

}
