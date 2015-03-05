package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eric.ssbl.R;

public class MessageArrayAdapter extends ArrayAdapter<String> {

    private Context _context;
    private String[] _values;

    public MessageArrayAdapter(Context context, String[] values) {
        super(context, R.layout.list_message, values);
        _context = context;
        _values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_message, parent, false);

        if (position % 2 == 0)
            ((ImageView) rowView.findViewById(R.id.message_square)).setImageResource(R.drawable.purple_rounded_square);

        TextView textView = (TextView) rowView.findViewById(R.id.message_unit);
        textView.setText(_values[position]);

        return rowView;
    }
}
