package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.eric.ssbl.R;
import com.eric.ssbl.android.pojos.User;

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
    }

    public UserListAdapter(Context context, List<User> users, int friendsIndex, int nearbyIndex) {
        super(context, R.layout.list_user);
        _context = context;
        _users = users;

        _friendsIndex = friendsIndex;
        _nearbyIndex = nearbyIndex;
    }
}
