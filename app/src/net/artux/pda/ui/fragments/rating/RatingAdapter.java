package net.artux.pda.ui.fragments.rating;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    List<UserInfo> list = new ArrayList<>();
    OnClickListener clickListener;
    UUID ownerId;

    RatingAdapter(OnClickListener clickListener, UUID ownerId) {
        this.clickListener = clickListener;
        this.ownerId = ownerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void addItems(List<UserInfo> update) {
        int oldSize = list.size();
        list.addAll(update);
        if (oldSize < 1)
            oldSize = 1;
        notifyItemRangeInserted(oldSize, update.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView title;
        TextView desc;
        TextView pos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.rating_avatar);
            title = itemView.findViewById(R.id.rating_title);
            desc = itemView.findViewById(R.id.rating_desc);
            pos = itemView.findViewById(R.id.rating_pos);
        }

        void bind(UserInfo userInfo, int pos) {
            this.pos.setText("#" + pos);
            if (ownerId.equals(userInfo.id))
                itemView.setBackgroundColor(Color.rgb(30, 40, 50));
            ProfileHelper.setAvatar(avatar, userInfo.avatar);
            title.setText(title.getContext().getString(R.string.rating_title, userInfo.login,
                    String.valueOf(userInfo.pdaId), ProfileHelper.getRangTitleByXp(userInfo.xp, title.getContext())));

            desc.setText(desc.getContext().getString(R.string.rating_desc,
                    ProfileHelper.getGroup(desc.getContext(), userInfo.gang.getId()),
                    ProfileHelper.getDays(userInfo.registration)));

            itemView.setOnClickListener(view -> clickListener.onClick(userInfo));
        }
    }

    interface OnClickListener {
        void onClick(UserInfo userInfo);
    }

}
