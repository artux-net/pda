package net.artux.pda.Views.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.artux.pda.BuildConfig;
import net.artux.pda.Models.profile.Item;
import net.artux.pda.R;
import net.artux.pda.activities.FragmentNavigation;

import java.util.List;

public class BackpackAdapter extends ArrayAdapter<Item>{

    private List<Item> items;
    LayoutInflater lInflater;
    Context context;
    FragmentNavigation.Presenter presenter;

    public BackpackAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects, FragmentNavigation.Presenter presenter) {
        super(context, resource, objects);
        this.items = objects;
        this.context = context;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.presenter = presenter;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = lInflater.inflate(R.layout.item_backpack, parent, false);

        }

        Item item = items.get(position);

        itemView.setOnClickListener(new UltimateClickListener(item, context, presenter));

        TextView title = itemView.findViewById(R.id.itemTitle);
        ImageView image = itemView.findViewById(R.id.itemImage);
        TextView weight  = itemView.findViewById(R.id.itemWeight);
        title.setText(item.title);

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

        Glide.with(getContext())
                .asGif()
                .load("http://"+ BuildConfig.URL+"/files?file="+item.icon)
                .apply(options)
                .into(image);

        weight.setText("Вес: " + item.weight);

        return itemView;
    }
}
