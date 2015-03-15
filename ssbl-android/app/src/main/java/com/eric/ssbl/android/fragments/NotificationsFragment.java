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
import com.eric.ssbl.android.pojos.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends ListFragment {

    private List<Notification> _notifs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _notifs = new ArrayList<>();
        Notification temp = new Notification();
        temp.setMessage("Welcome to Super Smash Bros. Locator");
        temp.setSendTime(System.currentTimeMillis() - 9L);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        _notifs.add(temp);
        setListAdapter(new NotificationArrayAdapter(getActivity(), _notifs));

        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        return v;
    }

//    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), ConversationActivity.class);
        i.putExtra("notif_id", _notifs.get(position).getNotificationId());
        startActivity(i);
    }
}
