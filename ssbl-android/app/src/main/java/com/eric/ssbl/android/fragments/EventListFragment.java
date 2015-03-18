package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.EditEventActivity;
import com.eric.ssbl.android.activities.EventActivity;
import com.eric.ssbl.android.adapters.EventListAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Event;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends ListFragment {

    private List<Event> _allEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_event_list, container, false);

        _allEvents = new ArrayList<Event>();
        List<Event> hosting = DataManager.getHostingEvents();
        List<Event> attending = DataManager.getCurUser().getEvents();
        List<Event> nearby = DataManager.getNearbyEvents();

        _allEvents.addAll(hosting);
        _allEvents.addAll(attending);
        _allEvents.addAll(nearby);

        int num1 = (hosting.size() == 0) ? -1 : 0;
        int num2 = (attending.size() == 0) ? -1 : hosting.size();
        int num3 = (nearby.size() == 0) ? -1 : hosting.size() + attending.size();

        setListAdapter(new EventListAdapter(getActivity(), _allEvents, num1, num2, num3));

        ImageButton createEvent = (ImageButton) v.findViewById(R.id.create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditEventActivity.class);
                Bundle b = new Bundle();
                b.putBoolean("new_event", true);
                i.putExtras(b);
                startActivity(i);
            }
        });
        return v;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), EventActivity.class);
        Bundle b = new Bundle();
        b.putInt("event_id", _allEvents.get(position).getEventId());
        i.putExtras(b);
        startActivity(i);
    }

    public void reset() {
        if (_allEvents != null)
            _allEvents.clear();
    }
}
