package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eric.ssbl.R;

public class EventListAdapter extends ArrayAdapter<String> {

    private final Context _context;
    private final String[] _values;

    public EventListAdapter(Context context, String[] values) {
        super(context, R.layout.list_inbox, values);
        _context = context;
        _values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_event, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.list_event_name);
        TextView preview = (TextView) rowView.findViewById(R.id.list_event_preview);
        preview.setText("it will b fun");
        name.setText(_values[position]);

        return rowView;
    }

}
