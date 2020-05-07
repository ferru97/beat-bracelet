package com.ferru97.beatbracelet.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ferru97.beatbracelet.R;

import java.util.List;

public class BraceletAdapter extends ArrayAdapter<Bracelet> {

    public BraceletAdapter(Context context, int textViewResourceId, List<Bracelet> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.bracelet_element, null);
        TextView name = (TextView)convertView.findViewById(R.id.brc_name);
        Bracelet c = getItem(position);
        name.setText(c.getName());
        return convertView;
    }

}
