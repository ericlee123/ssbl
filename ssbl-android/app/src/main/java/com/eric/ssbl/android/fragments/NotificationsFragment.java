package com.eric.ssbl.android.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.adapters.NotificationArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Notification;

import java.util.List;

public class NotificationsFragment extends ListFragment {

    private List<Notification> _notifs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _notifs = DataManager.getCurUser().getNotifications();
        if (_notifs != null)
            setListAdapter(new NotificationArrayAdapter(getActivity(), _notifs));
        else
            Toast.makeText(getActivity(), "Error retrieving notifications", Toast.LENGTH_SHORT).show();

        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        return v;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        // figure out type and stuff
    }
}
