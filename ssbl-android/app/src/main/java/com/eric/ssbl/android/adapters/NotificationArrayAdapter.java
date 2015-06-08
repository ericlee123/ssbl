package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.pojos.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        ((TextView) notificationView.findViewById(R.id.list_notification_text)).setText(n.getMessage());
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date time = new Date(n.getSendTime());
        ((TextView) notificationView.findViewById(R.id.list_notification_date)).setText(formatter.format(time));

        return notificationView;
    }
}
