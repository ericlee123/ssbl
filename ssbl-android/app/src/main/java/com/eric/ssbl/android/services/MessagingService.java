package com.eric.ssbl.android.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class MessagingService extends Service {

    private static User curUser;
    private boolean _open = false;
    private boolean _talking = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (curUser == null) {
            if (DataManager.getCurrentUser() == null) {
                stopSelf();
                return START_NOT_STICKY;
            }
            else {
                curUser = DataManager.getCurrentUser();
            }
        }

        new HttpNewMessageGetter().execute(curUser);

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        stopSelf();

        return START_NOT_STICKY;
    }

    private void createNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New Messages")
                        .setContentText("Go check your inbox");

//        Intent i = new Intent(getApplicationContext(), ConversationActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Bundle b = new Bundle();
//        b.putInt("conversation_index", conversationIndex);
//        i.putExtras(b);
//
//        builder.setContentIntent(pi);

        NotificationManager notifMngr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notifMngr.notify(1, builder.build());


    }

    public static void clearData() {
        curUser = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        _open = (DataManager.getCurrentUser() != null);
        _talking = (DataManager.getOpenConversationActivity() != null);

        // I want to restart this service again in 5 seconds
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (curUser != null) {
            if (!_open) {
                alarm.set(
                        alarm.RTC_WAKEUP,
                        System.currentTimeMillis() + (300000), // 5 minutes
                        PendingIntent.getService(this, 0, new Intent(this, MessagingService.class), 0)
                );
            } else {
                alarm.set(
                        alarm.RTC_WAKEUP,
                        System.currentTimeMillis() + (1000 * (_talking ? 4 : 7)), // 4 or 7 seconds
                        PendingIntent.getService(this, 0, new Intent(this, MessagingService.class), 0)
                );
            }
        }
    }

    private class HttpNewMessageGetter extends AsyncTask<User, Void, Void> {

        private List<Message> newMessages;

        private void fetchNewMessages(User curUser) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/messaging");
            url.append("/" + curUser.getUsername());
            url.append("/" + curUser.getUserId());
            url.append("/new");

            try {

                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
                request.setHeader("Accept", "application/json");

                ObjectMapper om = new ObjectMapper();
                om.enable(SerializationFeature.INDENT_OUTPUT);
                om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

//                System.out.println("fetch_new_messages");
//                System.out.println(url.toString());
//                System.out.println(response.getStatusLine().getStatusCode());
//                System.out.println(jsonString);

                if (jsonString.length() == 0)
                    return;

                newMessages = om.readValue(jsonString, new TypeReference<List<Message>>() {});
            } catch (Exception e) {
                newMessages = null;
                e.printStackTrace();
            }

            if (newMessages != null && newMessages.size() > 0) {
                DataManager.addNewMessages(newMessages);
                createNotification(); // should check if the open conversation activity matches any of the new messages
            }
        }

        @Override
        protected Void doInBackground(User... params) {
            fetchNewMessages(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (newMessages != null && newMessages.size() > 0) {
                if (DataManager.getInboxFragment() != null)
                    DataManager.getInboxFragment().refresh();
                if (DataManager.getOpenConversationActivity() != null)
                    DataManager.getOpenConversationActivity().showMessages();
            }
        }
    }
}
