package com.example.pin2location.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pin2location.R;
import com.example.pin2location.models.Location;

import java.util.List;


/**
 * Created by meliodas on 13/05/16.
 */
public class SimpleAdapter extends BaseAdapter {

    private List<Location> locations;
    private Context context;

    public SimpleAdapter(Context context, List<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    @Override
    public int getCount() {
        return locations.size();
    }

    @Override
    public Location getItem(int position) {
        return locations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.location_item, parent, false);
        }

        TextView tx = (TextView) convertView.findViewById(R.id.location);
        tx.setText(getItem(position).getProperties().name);

        return convertView;
    }
}
