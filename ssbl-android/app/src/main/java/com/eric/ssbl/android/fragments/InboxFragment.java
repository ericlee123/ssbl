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
import com.eric.ssbl.android.activities.MessageActivity;
import com.eric.ssbl.android.adapters.InboxArrayAdapter;

public class InboxFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String[] temp = new String[]{"one", "two", "three", "four", "six", "ten", "fuck", "what"};
        setListAdapter(new InboxArrayAdapter(getActivity(), temp));

        View v = inflater.inflate(R.layout.fragment_inbox, container, false);
        ImageButton createMessage = (ImageButton) v.findViewById(R.id.chart_refresh);
        createMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new message
            }
        });
        return v;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), MessageActivity.class);
        startActivity(i);
    }
}
