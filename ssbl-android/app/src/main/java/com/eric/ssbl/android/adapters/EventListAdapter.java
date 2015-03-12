package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.pojos.Event;

import java.util.List;

public class EventListAdapter extends ArrayAdapter<Event> {

    private final Context _context;
    private final List<Event> _events;

    public EventListAdapter(Context context, List<Event> events) {
        super(context, R.layout.list_inbox, events);
        _context = context;
        _events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_event, parent, false);
        TextView title = (TextView) rowView.findViewById(R.id.list_event_name);
        TextView preview = (TextView) rowView.findViewById(R.id.list_event_preview);

        title.setText(_events.get(position).getTitle());
        preview.setText("Hosted by " + _events.get(position).getHost().getUsername());

        return rowView;
    }

}
