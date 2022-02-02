package net.artux.pda.ui.activities.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.artux.pda.R;

import java.util.HashMap;

public class AvatarsAdapter extends RecyclerView.Adapter<AvatarsAdapter.ViewHolder> {

    int selected = 0;
    HashMap<Integer, ViewHolder> holders = new HashMap<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holders.put(position, holder);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    public void uncheck(Integer pos){
        ViewHolder holder = holders.get(pos);
        if (holder!=null)
            holder.uncheck();
    }

    public int getSelected() {
        return selected;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView avatar;
        ImageView checked;

        ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imageAvatar);
            checked = itemView.findViewById(R.id.imageChecked);
            itemView.setOnClickListener(this);
        }

        void bind(int id){
            id++;
            Glide.with(avatar)
                    .asDrawable()
                    .apply(new RequestOptions().override(122, 88))
                    .load(Uri.parse("file:///android_asset/avatars/a"+id+".png"))
                    .into(avatar);

            if(selected==id)
                check();
            else
                uncheck();
        }

        void check(){
            checked.setImageDrawable(ResourcesCompat.getDrawable(avatar.getResources(), R.drawable.ic_check, null));
        }

        public void uncheck(){
            checked.setImageDrawable(null);
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                AvatarsAdapter.this.uncheck(selected);
                selected = position;
                check();
            }
        }
    }
}