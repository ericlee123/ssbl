package com.eric.ssbl.android.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.ConversationActivity;
import com.eric.ssbl.android.adapters.InboxArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InboxFragment extends ListFragment {

    private final Context _context = getActivity();
    private List<Conversation> _conversations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View _view = inflater.inflate(R.layout.fragment_inbox, container, false);
        ImageButton createMessage = (ImageButton) _view.findViewById(R.id.new_message);
        createMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(getActivity());

                final List<User> relevantUsers = new ArrayList<>();
                List<User> nearbyTemp = DataManager.getNearbyUsers();
                relevantUsers.addAll(DataManager.getCurUser().getFriends());
                nearbyTemp.removeAll(relevantUsers);
                relevantUsers.addAll(nearbyTemp);

                List<String> usernames = new ArrayList<>();
                Iterator<User> iter = relevantUsers.iterator();
                while (iter.hasNext())
                    usernames.add(iter.next().getUsername());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(_context, android.R.layout.select_dialog_singlechoice);
                adapter.addAll(usernames);

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb
                        .setTitle("Choose a user")
                        .setCancelable(true)
                        .setAdapter(new ArrayAdapter<String>(_context, android.R.layout.select_dialog_singlechoice),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        User recipient = relevantUsers.get(which);
                                        // create new message with the recip
                                    }
                                })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                adb.create().show();
            }
        });

        _conversations = DataManager.getCurUser().getConversations();
        if (_conversations != null)
            setListAdapter(new InboxArrayAdapter(getActivity(), _conversations));
        else
            Toast.makeText(getActivity(), "Error retrieving conversations", Toast.LENGTH_SHORT).show();

        return _view;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), ConversationActivity.class);
        i.putExtra("conversation_index", position);
        startActivity(i);
    }
}
