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

import java.util.List;

import net.artux.pda.Models.profile.Item;
import net.artux.pda.R;
import net.artux.pda.activities.MainActivity;
import net.artux.pda.app.App;

public class BackpackAdapter extends ArrayAdapter<Item>{

    private List<Item> items;
    LayoutInflater lInflater;
    Context context;
    MainActivity mainActivity;

    public BackpackAdapter(@NonNull Context context, int resource, @NonNull List<Item> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        this.items = objects;
        this.context = context;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = lInflater.inflate(R.layout.item_backpack, parent, false);

        }

        Item item = items.get(position);

        itemView.setOnClickListener(new UltimateClickListener(item, context, mainActivity));

        TextView title = itemView.findViewById(R.id.itemTitle);
        ImageView image = itemView.findViewById(R.id.itemImage);
        TextView weight  = itemView.findViewById(R.id.itemWeight);
        title.setText(item.title);

        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

        Glide.with(getContext())
                .asGif()
                .load("http://"+App.URL+"/files?file="+item.icon)
                .apply(options)
                .into(image);

        weight.setText("Вес: " + item.weight);

        return itemView;
    }
}
