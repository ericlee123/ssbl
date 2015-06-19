package com.eric.ssbl.donate.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eric.ssbl.donate.R;
import com.eric.ssbl.donate.android.pojos.User;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private final Context _context;
    private final List<User> _users;
    private int _friendsIndex;
    private int _nearbyIndex;

    public UserListAdapter(Context context, List<User> users) {
        super(context, R.layout.list_user);
        _context = context;
        _users = users;

        _friendsIndex = -1;
        _nearbyIndex = -1;
    }

//    public UserListAdapter(Context context, List<User> users, int friendsIndex, int nearbyIndex) {
//        super(context, R.layout.list_user);
//        _context = context;
//        _users = users;
//
//        _friendsIndex = friendsIndex;
//        _nearbyIndex = nearbyIndex;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_user, parent, false);

        // this code is so ugly
//        if (position == _friendsIndex || position == _nearbyIndex) {
//            TextView category = (TextView) rowView.findViewById(R.id.list_user_category);
//            if (position == _friendsIndex)
//                category.setText(getContext().getString(R.string.friends));
//            else
//                category.setText(getContext().getString(R.string.nearby_users));
//            category.setVisibility(View.VISIBLE);
//        }

        TextView name = (TextView) rowView.findViewById(R.id.list_user_name);
        name.setText(_users.get(position).getUsername());
        System.out.println("look here: " + _users.get(position).getUsername());

        return rowView;
    }
}
