package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.ConversationActivity;
import com.eric.ssbl.android.adapters.NotificationArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Notification;

import java.util.List;

public class NotificationsFragment extends ListFragment {

    private List<Notification> _notifs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _notifs = DataManager.getNotifications();

        setListAdapter(new NotificationArrayAdapter(getActivity(), _notifs));

        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        return v;
    }

//    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), ConversationActivity.class);
        i.putExtra("index", position);
        startActivity(i);
    }
}
