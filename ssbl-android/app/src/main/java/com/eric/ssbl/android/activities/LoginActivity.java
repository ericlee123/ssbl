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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.GeneralManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class LoginActivity extends Activity {

    private File _loginFile;
    private JSONObject _loginObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _loginFile = new File(getFilesDir(), "file.mystery");

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        // check for stored login info
        if (_loginFile.exists()) {
            try {
                Scanner sc = new Scanner(_loginFile);
                sc.useDelimiter("\\Z");
                _loginObj = new JSONObject(sc.next());
                ((EditText) findViewById(R.id.login_username)).setText(_loginObj.getString("username"));
                ((EditText) findViewById(R.id.login_password)).setText(_loginObj.getString("password"));
            } catch (Exception e) {
                _loginFile.delete(); // The file may have issues, do not cause infinite loop of failure for users
                e.printStackTrace();
            }
        }
    }

    public void loginAccount(View view) {

        new HttpLogin().execute();

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

    private void rememberMe() {

        try {
            _loginObj.put("username", "hello");
            _loginObj.put("password", "derp");
            PrintWriter out = new PrintWriter(_loginFile);
            out.print(_loginObj);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void promptRegister(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View registerPrompt = li.inflate(R.layout.prompt_register, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb
                .setView(registerPrompt)
                .setTitle("Register an account")
                .setCancelable(true)
                .setPositiveButton("Register",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Registering...", Toast.LENGTH_SHORT).show();
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

    private void goToMain() {

        if (((CheckBox) findViewById(R.id.login_password)).isChecked())
            rememberMe();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private Activity getActivity() {
        return this;
    }

    private class HttpLogin extends AsyncTask<Void, Void, Void> {

        private boolean _success;

        private void httpLogin() {

            String url = GeneralManager.getServerUrl();

            try {
                // Use HttpGet b/c HTTP specs
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);

                HttpResponse response = client.execute(request);

                // get status codes

            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.sww_error), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }




        @Override
        protected Void doInBackground(Void... params) {
            httpLogin();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (_success)
                goToMain();
            else {
                // display error message
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
