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
import java.util.List;

public class InboxArrayAdapter extends ArrayAdapter<Conversation> {

    private final Context _context;
    private final List<Conversation> _conversations;

    public InboxArrayAdapter(Context context, List<Conversation> conversations) {
        super(context, R.layout.list_inbox, conversations);
        _context = context;
        _conversations = conversations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Conversation c = _conversations.get(position);
        View singleConversation = inflater.inflate(R.layout.list_inbox, parent, false);
        TextView title = (TextView) singleConversation.findViewById(R.id.list_inbox_title);
        TextView preview = (TextView) singleConversation.findViewById(R.id.list_inbox_preview);

        StringBuilder titleText = new StringBuilder();
        Iterator<User> i = c.getRecipients().iterator();
        while (i.hasNext()) {
            User temp = i.next();
            if (!temp.equals(DataManager.getCurUser()))
                titleText.append(temp.getUsername() + ", ");
        }
        if (titleText.length() >= 2)
            titleText.delete(titleText.length() - 2, titleText.length());

        title.setText(titleText.toString());

        List<Message> lm = DataManager.getConversationMap().get(_conversations.get(position));
        if (lm != null && lm.size() > 0)
            preview.setText(lm.get(0).getBody());
        else {
            StringBuilder blah = new StringBuilder();
            int rand = (int) (Math.random() * 4) + 1;
            for (int j = 0; j < rand; j++)
                blah.append("blah ");
            preview.setText(blah.toString());
        }

        return singleConversation;
    }
}
