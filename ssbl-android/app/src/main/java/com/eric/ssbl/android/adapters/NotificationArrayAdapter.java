package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.eric.ssbl.R;
import com.eric.ssbl.android.pojos.Notification;

import java.util.List;

public class NotificationArrayAdapter extends ArrayAdapter<Notification> {

    private Context _context;
    private List<Notification> _notifications;

    public NotificationArrayAdapter(Context context, List<Notification> notifications) {
        super(context, R.layout.list_message, notifications);
        _context = context;
        _notifications = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Notification n = _notifications.get(position);

        View notificationView = inflater.inflate(R.layout.list_notification, parent, false);

        return notificationView;
    }
}
