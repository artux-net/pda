package net.artux.pda.ui.fragments.profile.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.model.user.SimpleUserModel;
import net.artux.pda.ui.activities.hierarhy.FragmentNavigation;
import net.artux.pda.ui.fragments.profile.UserProfileFragment;
import net.artux.pda.ui.fragments.profile.helpers.ProfileHelper;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendHolder> {

    private List<SimpleUserModel> friends;

    public FragmentNavigation.Presenter fragmentNavigation;

    public FriendsAdapter(FragmentNavigation.Presenter fragmentNavigation){
        friends = new ArrayList<>();
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

    public void addData(List<SimpleUserModel> friends){
        this.friends.addAll(friends);
        notifyDataSetChanged();
    }

    public void setData(List<SimpleUserModel> friends){
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

        public void bind(SimpleUserModel friendModel){
            title.setText(friendModel.getLogin() + " [PDA #" + friendModel.getPdaId() +"]");
            desc.setText(ProfileHelper.getGroup(desc.getContext(), friendModel.getGang().getId()));
            ProfileHelper.setAvatar(avatar, friendModel.getAvatar());
            avatar.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserProfileFragment profileFragment = new UserProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pdaId", friendModel.getPdaId());
                    profileFragment.setArguments(bundle);
                    fragmentNavigation.addFragment(profileFragment, true);
                }
            });
        }
    }
}
