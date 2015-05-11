package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.adapters.ConversationArrayAdapter;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConversationActivity extends ListActivity {

    private Context _context = this;
    private ProgressDialog _loading;
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

        if (!getIntent().hasExtra("conversation_index")) {
            Toast.makeText(_context, "Error loading conversation", Toast.LENGTH_LONG).show();
            return;
        }

        DataManager.setOpenConversationActivity(this);

        _conversation = DataManager.getConversations().get(getIntent().getExtras().getInt("conversation_index"));

        setContentView(R.layout.activity_conversation);

        StringBuilder abTitle = new StringBuilder();
        Iterator<User> i = _conversation.getRecipients().iterator();
        while (i.hasNext()) {
            User temp = i.next();
            if (!temp.equals(DataManager.getCurrentUser()))
                abTitle.append(temp.getUsername() + ", ");
        }
        if (abTitle.length() >= 2)
            abTitle.delete(abTitle.length() - 2, abTitle.length());
        ((TextView) _abv.findViewById(R.id.action_bar_title)).setText(abTitle.toString());

        if (DataManager.getConversationMap().get(_conversation) == null || DataManager.getConversationMap().get(_conversation).size() == 0) {
            _loading = ProgressDialog.show(this, "Loading conversation...", getString(R.string.chill_out), true);
            new HttpConversationFetcher().execute(_conversation);
        }
        else
            showMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataManager.setOpenConversationActivity(null);
        if (DataManager.getInboxFragment() != null)
            DataManager.getInboxFragment().refresh();
    }

    public void showMessages() {
        List<Message> lm = DataManager.getConversationMap().get(_conversation);
        List<Message> reversed = new ArrayList<>();
        for (int i = lm.size() - 1; i >= 0; i--)
            reversed.add(lm.get(i));

        if (lm != null) {
            setListAdapter(new ConversationArrayAdapter(this, reversed));
            getListView().setSelection(getListAdapter().getCount() - 1);
        }
    }

    public void sendMessage(View view) {

        EditText et = (EditText) findViewById(R.id.send_message);
        if (et.getText().length() == 0) {
            Toast.makeText(_context, "Save the trees. Don't send blank messages.", Toast.LENGTH_SHORT).show();
            return;
        }

        Message sending = new Message();
        sending.setSender(DataManager.getCurrentUser());
        sending.setSentTime(System.currentTimeMillis());
        sending.setConversation(_conversation);

        sending.setBody(et.getText().toString());
        et.setText("");

        DataManager.getConversationMap().get(_conversation).add(0, sending);
        new HttpMessageSender().execute(sending);
        showMessages();
    }

    /**
     * For next release.
     * @param view i hate programming
     */
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

    private class HttpConversationFetcher extends AsyncTask<Conversation, Void, Void> {

        private List<Message> lm;

        @Override
        protected Void doInBackground(Conversation... params) {
            lm = DataManager.httpFetchConversation(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();
            if (lm != null)
                showMessages();
            else
                Toast.makeText(_context, "Error retrieving messages", Toast.LENGTH_SHORT).show();
        }
    }

    private class HttpMessageSender extends AsyncTask<Message, Void, Void> {

        private Message message;

        private void message(Message m) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/messaging");
            url.append("/" + DataManager.getCurrentUser().getUsername());
            url.append("/" + DataManager.getCurrentUser().getUserId());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
                request.setHeader("Accept", "application/json");

                ObjectMapper om = new ObjectMapper();
                om.enable(SerializationFeature.INDENT_OUTPUT);
                om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                StringEntity body = new StringEntity(om.writeValueAsString(m), "UTF-8");
                body.setContentType("application/json");
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

//                System.out.println("send_message");
//                System.out.println(url.toString());
//                System.out.println(response.getStatusLine().getStatusCode());
//                System.out.println(jsonString);

                if (jsonString.length() == 0)
                    message = null;
                else
                    message = om.readValue(jsonString, Message.class);
            } catch (Exception e) {
                message = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Message... params) {
            message(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (message == null) {
                DataManager.getConversationMap().get(_conversation).remove(DataManager.getConversationMap().get(_conversation).size() - 1);
                Toast.makeText(_context, "Message not sent", Toast.LENGTH_LONG).show();
            }
            showMessages();
        }
    }
}
