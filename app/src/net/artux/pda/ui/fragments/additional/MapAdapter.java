package net.artux.pda.ui.fragments.additional;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Map;

public class MapAdapter extends BaseAdapter{

    private final LinkedList<Map.Entry<String, String>> mData;

    public MapAdapter(Map<String, String> map) {
        mData = new LinkedList<>();
        mData.addAll(map.entrySet());
    }
    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public Map.Entry<String, String> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        ((TextView) result.findViewById(android.R.id.text1)).setText(item.getKey());
        return result;
    }
}

