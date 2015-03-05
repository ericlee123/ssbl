package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eric.ssbl.R;

public class InboxArrayAdapter extends ArrayAdapter<String> {

    private final Context _context;
    private final String[] _values;

    public InboxArrayAdapter(Context context, String[] values) {
        super(context, R.layout.list_inbox, values);
        _context = context;
        _values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_inbox, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.list_inbox_name);
        TextView preview = (TextView) rowView.findViewById(R.id.list_inbox_preview);
        name.setText(_values[position]);

        return rowView;
    }
}
