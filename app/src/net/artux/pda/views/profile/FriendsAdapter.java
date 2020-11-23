package net.artux.pda.views.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.activities.FragmentNavigation;
import net.artux.pda.models.ProfileHelper;
import net.artux.pdalib.profile.FriendModel;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    List<FriendModel> friends = new ArrayList<>();

    public FragmentNavigation.Presenter fragmentNavigation;

    FriendsAdapter(FragmentNavigation.Presenter fragmentNavigation){
        this.fragmentNavigation = fragmentNavigation;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void addData(List<FriendModel> friends){
        this.friends.addAll(friends);
        notifyDataSetChanged();
    }

    public void setData(List<FriendModel> friends){
        this.friends = friends;
        notifyDataSetChanged();
    }

    class FriendHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView avatar;
        TextView desc;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleDialog);
            avatar = itemView.findViewById(R.id.avatarDialog);
            desc = itemView.findViewById(R.id.lastMessage);
        }

        public void bind(FriendModel friendModel){
            title.setText(friendModel.name + " [PDA #" + friendModel.id+"]");
            desc.setText(ProfileHelper.getGroup(desc.getContext(), friendModel.group));
            avatar.setImageDrawable(ProfileHelper.getAvatar(avatar.getContext(), friendModel.avatar));
            avatar.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pdaId", friendModel.id);
                    profileFragment.setArguments(bundle);
                    fragmentNavigation.addFragment(profileFragment, true);
                }
            });
        }
    }
}
