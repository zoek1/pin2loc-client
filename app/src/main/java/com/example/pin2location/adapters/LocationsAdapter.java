package com.example.pin2location.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.pin2location.R;
import com.example.pin2location.models.Location;

/**
 * Created by meliodas on 13/05/16.
 */
public class LocationsAdapter extends ArrayAdapter<Location> {
    public LocationsAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_item, parent, false);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.location.setText(getItem(position).getProperties().name);
        return convertView;
    }


    class ViewHolder {
        TextView location;
    }
}
