package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.eric.ssbl.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class LoginActivity extends Activity {

    final Activity _context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            // Check for hashed login data
//            File login = new File("/SSBL/");
//            if (!login.exists())
//                login.createNewFile();
//        } catch (IOException e) {
//            Toast.makeText(this, R.string.error_creating_directory, Toast.LENGTH_SHORT).show();
//        }
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
    }

    public void loginAccount(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();

//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//
//        Intent i = new Intent(this, ProfileActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setContentIntent(pi);
//
//        NotificationManager notifMngr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // Builds the notification and issues it.
//        notifMngr.notify(1, builder.build());
    }

    public void registerAccount(View view) {
        LayoutInflater li = LayoutInflater.from(_context);
        View registerPrompt = li.inflate(R.layout.prompt_register, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(_context);
        adb
                .setView(registerPrompt)
                .setTitle("Register an account")
                .setCancelable(true)
                .setPositiveButton("Register",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(_context, "Registering...", Toast.LENGTH_SHORT).show();
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

    private class HttpLogin extends AsyncTask<Void, Void, Void> {

        private String _errorMessage = null;

        private void httpLogin() {

            try {
                // Use HttpGet b/c HTTP specs
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet("dummy url");

                HttpResponse response = client.execute(request);

            } catch (Exception e) {
                _errorMessage = "Something went exceptionally wrong. :(";
                e.printStackTrace();
            }
        }


        private String bytesToHex(byte[] bytes) {
            char[] hexArray = "0123456789ABCDEF".toCharArray();
            char[] hexChars = new char[bytes.length * 2];
            for ( int j = 0; j < bytes.length; j++ ) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }

        @Override
        protected Void doInBackground(Void... params) {
            httpLogin();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {

        }
    }

    private class HttpRegister extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void what) {

        }
    }

}
