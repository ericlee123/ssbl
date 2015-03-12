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

import java.util.List;

public class EventListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_event_list, container, false);

        ListView hosting = (ListView) v.findViewById(R.id.event_list);

        hosting.setAdapter(new EventListAdapter(getActivity(), (List) DataManager.getAllEvents()));


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
        startActivity(i);
    }
}
