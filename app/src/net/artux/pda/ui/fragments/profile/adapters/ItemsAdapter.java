package net.artux.pda.ui.fragments.profile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.artux.pda.BuildConfig;
import net.artux.pda.R;
import net.artux.pdalib.profile.items.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<Item> items = new ArrayList<>();
    OnClickListener onClickListener;

    public ItemsAdapter(){}

    public ItemsAdapter(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public GridLayoutManager getLayoutManager(Context context, int span){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,  span);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(getItemViewType(position)){
                    default:
                        return 1;
                    case 1:
                        return 2;
                }
            }
        });
        return gridLayoutManager;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_backpack, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public List<Item> getItems() {
        return items;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        ImageView image;
        TextView weight;
        TextView quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            image = itemView.findViewById(R.id.itemImage);
            weight  = itemView.findViewById(R.id.itemWeight);
            quantity = itemView.findViewById(R.id.itemQuantity);
        }

        void bind(Item item){
            title.setText(item.title);

            itemView.setOnClickListener(view -> {
                onClickListener.onClick(getPosition());
            });

            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher);

            Glide.with(title.getContext())
                    .asGif()
                    .load("https://"+ BuildConfig.URL_API+"/files?file="+item.icon)
                    .apply(options)
                    .into(image);

            quantity.setText(String.valueOf(item.quantity));
            weight.setText(weight.getContext().getString(R.string.weight, String.valueOf(item.weight)));
        }
    }

    public interface OnClickListener{
        void onClick(int pos);
    }
}
