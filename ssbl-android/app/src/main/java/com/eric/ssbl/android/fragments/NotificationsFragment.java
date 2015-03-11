package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.ConversationActivity;
import com.eric.ssbl.android.managers.Manager;
import com.eric.ssbl.android.pojos.Conversation;

import java.util.List;

/**
 * Created by eric on 3/1/15.
 */
public class NotificationsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        List<Conversation> conversations = Manager.getConversations();
        Conversation[] arr = new Conversation[conversations.size()];
        arr = conversations.toArray(arr);

//        setListAdapter(new InboxArrayAdapter(getActivity(), arr));

        View v = inflater.inflate(R.layout.fragment_inbox, container, false);
        ImageButton createMessage = (ImageButton) v.findViewById(R.id.new_message);
        createMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new message
            }
        });
        return v;
    }

//    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), ConversationActivity.class);
        i.putExtra("index", position);
        startActivity(i);
    }
}
