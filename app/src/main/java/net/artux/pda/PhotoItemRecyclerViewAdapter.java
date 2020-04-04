package net.artux.pda;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import net.artux.pda.activities.PickImageActivity;

public class PhotoItemRecyclerViewAdapter extends RecyclerView.Adapter<PhotoItemRecyclerViewAdapter.ViewHolder> {


    public PhotoItemRecyclerViewAdapter(PickImageActivity pickImageActivity, int[] idAvatars) {
        this.mContext = pickImageActivity;
        this.idAvatars = idAvatars;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(photoView);
    }

    private int[] idAvatars;
    private PickImageActivity mContext;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        ImageView imageView = holder.mPhotoImageView;
        imageView.setImageDrawable(mContext.getResources().getDrawable(idAvatars[position]));

    }

    @Override
    public int getItemCount() {
        return idAvatars.length-1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mPhotoImageView;

        ViewHolder(View itemView) {
            super(itemView);
            mPhotoImageView = itemView.findViewById(R.id.imageAvatar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getPosition();
            if(position != RecyclerView.NO_POSITION) {
                mContext.setResult(position);
                mContext.finish();
            }
        }
    }
}