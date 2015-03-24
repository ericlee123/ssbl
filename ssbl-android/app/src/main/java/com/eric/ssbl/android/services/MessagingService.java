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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        if (DataManager.getCurUser() == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        System.out.println("MessageService active");
        new HttpNewMessageGetter().execute(DataManager.getCurUser());

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        stopSelf();

        return START_NOT_STICKY;
    }

    private void createNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(DataManager.getCurUser().getUsername())
                        .setContentText("You have new messages");

//        Intent i = new Intent(getApplicationContext(), ConversationActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setContentIntent(pi);

        NotificationManager notifMngr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notifMngr.notify(1, builder.build());


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        // If no account is active or the app is kill, don't keep running the service
        if (DataManager.getCurUser() == null)
            return;

        // I want to restart this service again in 5 seconds
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 7),
                PendingIntent.getService(this, 0, new Intent(this, MessagingService.class), 0)
        );
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

                System.out.println("fetch_new_messages");
                System.out.println(url.toString());
                System.out.println(response.getStatusLine().getStatusCode());
                System.out.println(jsonString);

                if (jsonString.length() == 0)
                    return;

                newMessages = om.readValue(jsonString, new TypeReference<List<Message>>() {});
            } catch (Exception e) {
                newMessages = null;
                e.printStackTrace();
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
                DataManager.addNewMessages(newMessages);
                if (DataManager.getOpenConversationActivity() == null)
                    createNotification();
            }
        }
    }
}
