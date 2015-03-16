package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.adapters.ConversationArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Iterator;
import java.util.List;

public class ConversationActivity extends ListActivity {

    private final Context _context = this;
    private Conversation _conversation;
    private View _abv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        _abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(_abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        _abv.findViewById(R.id.action_bar_delete).setVisibility(View.VISIBLE);

        new HttpConversationGetter().execute(getIntent().getExtras().getInt("conversation_id"));

        setContentView(R.layout.activity_conversation);
    }

    private void populate() {

        StringBuilder title = new StringBuilder();
        Iterator<User> iu = _conversation.getRecipients().iterator();
        while (iu.hasNext()) {
            String recip = iu.next().getUsername();
            if (!recip.equals(DataManager.getCurUser().getUsername()))
                title.append(recip + ", ");
        }
        title.delete(title.length() - 2, title.length());
        ((TextView) _abv.findViewById(R.id.action_bar_title)).setText(title.toString());

        setListAdapter(new ConversationArrayAdapter(this, _conversation.getMessages()));
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

    private class HttpConversationGetter extends AsyncTask<Integer, Void, Void> {

        private List<Message> lm;

        private void fetchConversation(int id) {

            StringBuilder url = new StringBuilder();
            url.append(DataManager.getServerUrl() + "/messaging/");
            url.append(DataManager.getCurUser().getUsername() + "/" + DataManager.getCurUser().getUserId());
            url.append("?conversation=" + id);
            url.append("?size=0&additional=2");

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url.toString());
            HttpResponse response = null;
            try {
                System.out.println("hurr");
                response = client.execute(request);
                System.out.println("hellohello: " + response.toString());
                ObjectMapper om = new ObjectMapper();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Integer... params) {
            fetchConversation(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (lm != null) {
                _conversation = new Conversation();
                _conversation.setMessages(lm);
                populate();
            }
            else
                Toast.makeText(_context, "Error retrieving messages", Toast.LENGTH_SHORT).show();
        }
    }
}
