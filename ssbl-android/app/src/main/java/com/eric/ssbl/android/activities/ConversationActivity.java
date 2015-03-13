package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.adapters.ConversationArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.User;

import java.util.Iterator;

public class ConversationActivity extends ListActivity {

    private final Context _context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        abv.findViewById(R.id.action_bar_delete).setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();
        Conversation c = DataManager.getAllConversations().get(b.getInt("index"));

        StringBuilder title = new StringBuilder();
        Iterator<User> iu = c.getRecipients().iterator();
        while (iu.hasNext()) {
            String recip = iu.next().getUsername();
            if (!recip.equals(DataManager.getCurUser().getUsername()))
                title.append(recip + ", ");
        }
        title.delete(title.length() - 2, title.length());
        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(title.toString());

        setListAdapter(new ConversationArrayAdapter(this, c.getMessages()));
        getListView().setSelection(getListAdapter().getCount() - 1);
    }

    public void sendMessage(View view) {
        // send message in the edit text
        Toast.makeText(this, "Sending message...", Toast.LENGTH_SHORT).show();
    }

    public void deleteButton(View view) {

        AlertDialog.Builder adb = new AlertDialog.Builder(_context);
        adb
                .setTitle("Are you sure?")
                .setCancelable(true)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(_context, "Message deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        adb.create().show();
    }

    public void goBack(View view) {
        finish();
    }
}
