package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.User;

import java.util.Iterator;

public class InboxArrayAdapter extends ArrayAdapter<Conversation> {

    private final Context _context;
    private final Conversation[] _conversations;

    public InboxArrayAdapter(Context context, Conversation[] conversations) {
        super(context, R.layout.list_inbox, conversations);
        _context = context;
        _conversations = conversations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Conversation c = _conversations[position];
        View singleConversation = inflater.inflate(R.layout.list_inbox, parent, false);
        TextView title = (TextView) singleConversation.findViewById(R.id.list_inbox_title);
        TextView preview = (TextView) singleConversation.findViewById(R.id.list_inbox_preview);

        StringBuilder titleText = new StringBuilder();
        Iterator<User> i = c.getRecipients().iterator();
        while (i.hasNext()) {
            String temp = i.next().getUsername();
            if (!temp.equals(DataManager.getCurUser().getUsername()))
                titleText.append(temp + ", ");
        }
        if (titleText.length() >= 2)
            titleText.delete(titleText.length() - 2, titleText.length());

        title.setText(titleText.toString());

        StringBuilder previewText = new StringBuilder();
        Message last = c.getMessages().get(c.getMessages().size() - 1);
        if (last.getSender().getUsername().equals(DataManager.getCurUser().getUsername()))
            previewText.append("You: ");
        previewText.append(last.getBody());
        preview.setText(previewText.toString());

        return singleConversation;
    }
}
