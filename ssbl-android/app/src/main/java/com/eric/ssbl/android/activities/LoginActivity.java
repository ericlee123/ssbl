package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.eric.ssbl.android.managers.DataManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LoginActivity extends Activity {

    private File _loginFile;
    private JSONObject _loginObj;
    private ProgressDialog _loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _loginFile = new File(getFilesDir(), "yummy.hunnymustard");

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        // check for stored login info
        if (_loginFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(_loginFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                _loginObj = (JSONObject) ois.readObject();
                ((EditText) findViewById(R.id.login_username)).setText(_loginObj.getString("username"));
                ((EditText) findViewById(R.id.login_password)).setText(_loginObj.getString("password"));
                ((CheckBox) findViewById(R.id.login_remember_me)).setChecked(true);
                new HttpLogin().execute();
            } catch (Exception e) {
                _loginFile.delete(); // The file may have issues, do not cause infinite loop of failure for users
                e.printStackTrace();
            }
        }
    }

    public void loginAccount(View view) {

        DataManager.init();

        startActivity(new Intent(this, MainActivity.class));
        finish();

//        _loginObj = new JSONObject();
//
//        try {
//            _loginObj.put("username", ((EditText) findViewById(R.id.login_username)).getText());
//            _loginObj.put("password", ((EditText) findViewById(R.id.login_password)).getText());
//            new HttpLogin().execute();
//        } catch (Exception e) {
//            Toast.makeText(this, getString(R.string.sww_error), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }

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
            _loginFile.delete();
            _loginFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(_loginFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_loginObj);
            oos.close();
            fos.close();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.remember_me_error), Toast.LENGTH_SHORT).show();
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

        // set the current user in the general manager
        if (((CheckBox) findViewById(R.id.login_remember_me)).isChecked())
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

            String url = DataManager.getServerUrl();

            try {
                // Use HttpGet b/c HTTP specs
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);

                // hash password and put back into the json?

                HttpResponse response = client.execute(request);

                // get status codes
                // if successful, retrieve the current user and set as curUser in manager class

            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.sww_error), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }




        @Override
        protected Void doInBackground(Void... params) {
            _loading = ProgressDialog.show(getActivity(), getString(R.string.logging_in), getString(R.string.chill_out), true);
            httpLogin();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();
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

    private class HttpRegister extends AsyncTask<JSONObject, Void, Void> {

        private boolean _success;

        private void httpRegister(JSONObject login) {
            String url = DataManager.getServerUrl();

            try {
                // Use HttpGet b/c HTTP specs
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);

                // hash password and put back into the json?

                HttpResponse response = client.execute(request);

                // get status codes

            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.sww_error), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            _loading = ProgressDialog.show(getActivity(), getString(R.string.registering), getString(R.string.chill_out), true);
            httpRegister(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();
        }
    }

}
