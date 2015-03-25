package com.eric.ssbl.android.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.LinkedList;
import java.util.List;

public class InboxFragment extends ListFragment {

    private static List<Conversation> _conversations;
    private AlertDialog.Builder _selectUser;
    private AlertDialog.Builder _firstMessage;
    private static boolean _refreshed;

    private static int _which;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<User> relevantUsers = new ArrayList<>();
        List<User> nearbyTemp = DataManager.getNearbyUsers();
        relevantUsers.addAll(DataManager.getCurUser().getFriends());
        nearbyTemp.removeAll(relevantUsers);
        relevantUsers.addAll(nearbyTemp);
        relevantUsers.remove(DataManager.getCurUser());

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

                        EditText body = (EditText) temp.findViewById(R.id.prompt_first_message_body);
                        if (body.getText().length() == 0) {
                            Toast.makeText(getActivity(), "Why would you send a blank message?", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Message first = new Message();
                        first.setSentTime(System.currentTimeMillis());
                        first.setSender(DataManager.getCurUser());
                        first.setBody(body.getText().toString());

                        Conversation temp = new Conversation();
                        temp.addRecipient(relevantUsers.get(_which));
                        temp.addRecipient(DataManager.getCurUser());
                        first.setConversation(temp);

                        DataManager.getCurUser().addConversation(temp);
                        DataManager.refreshConversations();
                        new HttpFirstMessageSender().execute(first);
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
                        _which = which;
                        _firstMessage.show();
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

        DataManager.setInboxFragment(this);

        View _view = inflater.inflate(R.layout.fragment_inbox, container, false);
        ImageButton createMessage = (ImageButton) _view.findViewById(R.id.new_message);
        createMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _selectUser.show();
            }
        });

        if (!_refreshed)
            refresh();

        return _view;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {

        Intent i = new Intent(getActivity(), ConversationActivity.class);
        Bundle b = new Bundle();
        b.putInt("conversation_index", position);
        i.putExtras(b);
        startActivity(i);
    }

    public void refresh() {
        _refreshed = true;
        _conversations = DataManager.getCurUser().getConversations();
        if (_conversations != null)
            setListAdapter(new InboxArrayAdapter(getActivity(), _conversations));
        else
            Toast.makeText(getActivity(), "Error retrieving conversations", Toast.LENGTH_SHORT).show();
    }

    public static void clearData() {
        _refreshed = false;
        if (_conversations != null)
            _conversations.clear();
    }

    private class HttpFirstMessageSender extends AsyncTask<Message, Void, Void> {

        private Message message;

        private void message(Message m) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/messaging");
            url.append("/" + DataManager.getCurUser().getUsername());
            url.append("/" + DataManager.getCurUser().getUserId());

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

                System.out.println("send_first_message");
                System.out.println(url.toString());
                System.out.println(response.getStatusLine().getStatusCode());
                System.out.println(jsonString);

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
            if (message != null) {
                DataManager.getCurUser().getConversations().add(message.getConversation());
                List<Message> one = new LinkedList<>();
                one.add(message);
                DataManager.getConversationMap().put(message.getConversation(), one);

                Intent i = new Intent(getActivity(), ConversationActivity.class);
                Bundle b = new Bundle();
                b.putInt("conversation_index", DataManager.getCurUser().getConversations().size() - 1);
                i.putExtras(b);
                startActivity(i);
            }
            else
                Toast.makeText(getActivity(), "Error creating new conversation :(", Toast.LENGTH_LONG).show();
        }
    }
}
