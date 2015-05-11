package com.eric.ssbl.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Message;

import java.util.List;

public class ConversationArrayAdapter extends ArrayAdapter<Message> {

    private Context _context;
    private List<Message> _messages;

    public ConversationArrayAdapter(Context context, List<Message> messages) {
        super(context, R.layout.list_message, messages);
        _context = context;
        _messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Message m = _messages.get(position);

        View messageView = inflater.inflate(R.layout.list_message, parent, false);

        RelativeLayout rl = (RelativeLayout) messageView.findViewById(R.id.message_layout);

        // Square
        ImageView square = (ImageView) messageView.findViewById(R.id.message_square);
        RelativeLayout.LayoutParams squareLayoutParams = (RelativeLayout.LayoutParams) square.getLayoutParams();

        TextView body = (TextView) messageView.findViewById(R.id.message_body);
        RelativeLayout.LayoutParams bodyLayoutParams = (RelativeLayout.LayoutParams) body.getLayoutParams();
        body.setText(m.getBody());

        TextView details = (TextView) messageView.findViewById(R.id.message_details);
        StringBuilder temp = new StringBuilder();
        temp.append(m.getSender().getUsername() + " - ");
        int elapsed = (int) ((System.currentTimeMillis() - m.getSentTime()) / 60000);
        if (elapsed < 60)
            temp.append(elapsed + " minutes ago");
        else if (elapsed < 1440)
            temp.append((elapsed / 60) + " hours ago");
        else
            temp.append((elapsed / 1440) + " days ago");
        details.setText(temp.toString());

        if (m.getSender().equals(DataManager.getCurrentUser())) {
            square.setImageResource(R.drawable.purple_rounded_square);
            squareLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            bodyLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.message_square);
        }
        else {
            square.setImageResource(R.drawable.gray_rounded_square);
            squareLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            bodyLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.message_square);

            details.setVisibility(View.VISIBLE);
        }

        square.setLayoutParams(squareLayoutParams);
        body.setLayoutParams(bodyLayoutParams);

        return messageView;
    }
}
