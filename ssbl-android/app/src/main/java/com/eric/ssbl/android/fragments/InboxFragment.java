package com.eric.ssbl.android.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.ConversationActivity;
import com.eric.ssbl.android.adapters.InboxArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InboxFragment extends ListFragment {

    private List<Conversation> _conversations;
    private AlertDialog.Builder _selectUser;
    private AlertDialog.Builder _firstMessage;
    private int _which;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<User> relevantUsers = new ArrayList<>();
        List<User> nearbyTemp = DataManager.getNearbyUsers();
        relevantUsers.addAll(DataManager.getCurUser().getFriends());
        nearbyTemp.removeAll(relevantUsers);
        relevantUsers.addAll(nearbyTemp);

        List<String> usernames = new ArrayList<>();
        Iterator<User> iter = relevantUsers.iterator();
        while (iter.hasNext())
            usernames.add(iter.next().getUsername());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item);
        adapter.addAll(usernames);

        LayoutInflater li = LayoutInflater.from(getActivity());
        final View temp = li.inflate(R.layout.prompt_first_message, null);

        _firstMessage = new AlertDialog.Builder(getActivity());
        _firstMessage
                .setTitle("Compose message")
                .setCancelable(true)
                .setView(temp)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Message first = new Message();
                        first.setSentTime(System.currentTimeMillis());
                        first.setSender(DataManager.getCurUser());

                        String body = ((EditText) temp.findViewById(R.id.prompt_first_message_body)).getText().toString();
                        first.setBody(body);

                        // send the message to the server
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        _selectUser = new AlertDialog.Builder(getActivity());
        _selectUser
                .setTitle("Start conversation with")
                .setCancelable(true)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _firstMessage.show();
                        _which = which;
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View _view = inflater.inflate(R.layout.fragment_inbox, container, false);
        ImageButton createMessage = (ImageButton) _view.findViewById(R.id.new_message);
        createMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _selectUser.show();
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
        i.putExtra("conversation_id", _conversations.get(position).getConversationId());
        i.putExtra("conversation_title", "wow");
        startActivity(i);
    }
}
