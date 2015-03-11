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
import com.eric.ssbl.android.managers.GeneralManager;
import com.eric.ssbl.android.pojos.Message;

public class ConversationArrayAdapter extends ArrayAdapter<Message> {

    private Context _context;
    private Message[] _messages;

    public ConversationArrayAdapter(Context context, Message[] messages) {
        super(context, R.layout.list_message, messages);
        _context = context;
        _messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Message m = _messages[position];

        View messageView = inflater.inflate(R.layout.list_message, parent, false);

        RelativeLayout rl = (RelativeLayout) messageView.findViewById(R.id.message_layout);

        // Square
        ImageView square = (ImageView) messageView.findViewById(R.id.message_square);
        RelativeLayout.LayoutParams squareLayoutParams = (RelativeLayout.LayoutParams) square.getLayoutParams();

        TextView body = (TextView) messageView.findViewById(R.id.message_body);
        RelativeLayout.LayoutParams bodyLayoutParams = (RelativeLayout.LayoutParams) body.getLayoutParams();
        body.setText(m.getBody());

        TextView sender = (TextView) messageView.findViewById(R.id.message_sender);
        sender.setText(m.getSender().getUsername());

        if (m.getSender().equals(GeneralManager.getCurUser())) {
            square.setImageResource(R.drawable.purple_rounded_square);
            squareLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            bodyLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.message_square);
        }
        else {
            square.setImageResource(R.drawable.gray_rounded_square);
            squareLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            bodyLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.message_square);

            sender.setVisibility(View.VISIBLE);
        }

        square.setLayoutParams(squareLayoutParams);
        body.setLayoutParams(bodyLayoutParams);

        return messageView;
    }
}
