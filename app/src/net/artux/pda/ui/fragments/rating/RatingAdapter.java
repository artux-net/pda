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
import net.artux.pda.app.App;
import net.artux.pda.ui.fragments.profile.ProfileHelper;
import net.artux.pda.ui.fragments.profile.adapters.ItemsAdapter;

import java.util.ArrayList;
import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    List<UserInfo> list = new ArrayList<>();
    static ItemsAdapter.OnClickListener clickListener;
    static int ownerId;

    RatingAdapter(ItemsAdapter.OnClickListener clickListener){
        RatingAdapter.clickListener = clickListener;
        if(App.getDataManager().getMember()!=null){
            ownerId = App.getDataManager().getMember().getPdaId();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(list.get(position), position+1);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void addData(List<UserInfo> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

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

        void bind(UserInfo userInfo, int pos){
            this.pos.setText("#" + pos);
            if (ownerId == userInfo.pdaId)
                itemView.setBackgroundColor(Color.rgb(30, 40, 50));
            avatar.setImageDrawable(ProfileHelper.getAvatar(avatar.getContext(), userInfo.avatar));
            title.setText(title.getContext().getString(R.string.rating_title,userInfo.login,
                    String.valueOf(userInfo.pdaId), String.valueOf(ProfileHelper.getRang(title.getContext(), userInfo.xp))));
            desc.setText(desc.getContext().getString(R.string.rating_desc,ProfileHelper.getGroup(desc.getContext(), userInfo.group),
                    ProfileHelper.getDays(userInfo.registration), userInfo.location));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(userInfo.pdaId);
                }
            });
        }
    }

}
