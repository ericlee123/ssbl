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
    private final int _hostingIndex;
    private final int _attendingIndex;
    private final int _nearbyIndex;

    public EventListAdapter(Context context, List<Event> events, int hostingIndex, int attendingIndex, int nearbyIndex) {
        super(context, R.layout.list_inbox, events);
        _context = context;
        _events = events;

        _hostingIndex = hostingIndex;
        _attendingIndex = attendingIndex;
        _nearbyIndex = nearbyIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_event, parent, false);

        // this code is so ugly
        if (position == _hostingIndex || position == _attendingIndex || position == _nearbyIndex) {
            TextView category = (TextView) rowView.findViewById(R.id.list_event_category);
            if (position == _hostingIndex)
                category.setText(getContext().getString(R.string.hosting_events));
            else if (position == _attendingIndex)
                category.setText(getContext().getString(R.string.attending_events));
            else
                category.setText(getContext().getString(R.string.nearby_events));
            category.setVisibility(View.VISIBLE);
        }

        TextView title = (TextView) rowView.findViewById(R.id.list_event_name);
        TextView preview = (TextView) rowView.findViewById(R.id.list_event_preview);

        title.setText(_events.get(position).getTitle());
        preview.setText("Hosted by " + _events.get(position).getHost().getUsername());

        return rowView;
    }

}
