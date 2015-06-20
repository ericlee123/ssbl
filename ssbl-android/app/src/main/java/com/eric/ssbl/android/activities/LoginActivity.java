package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
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
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.Notification;
import com.eric.ssbl.android.pojos.User;
import com.eric.ssbl.android.services.MessagingService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class LoginActivity extends Activity {

    private Context _context = this;
    private File _loginFile;
    private ProgressDialog _loading;
    private View _registerPrompt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _loginFile = new File(getFilesDir(), "login");

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // Get aws server url
        new HttpServerUrlInit().execute();

//        File opened = new File(getFilesDir(), "opened");
//        if (!opened.exists()) {
//            try {
//                opened.createNewFile();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            AlertDialog.Builder adb = new AlertDialog.Builder(this);
//
//            adb
//                    .setTitle("Welcome!!!")
//                    .setMessage("We listened to your feedback and overhauled the poop out of everything! New features include user privacy, " +
//                            "messaging functions, and a better user interface. In case you made an account with the old application, you will have" +
//                            " to make a new account (compatibility issues). Sorry! Everything should be working a lot better, and if you stumble upon" +
//                            " any errors, shoot us an email at hunnymustardapps@gmail.com. Have fun smashing!")
//                    .setNeutralButton("Okay!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            adb.create().show();
//        }

        // check for stored login info
        if (_loginFile.exists()) {
            try {
                Scanner scan = new Scanner(_loginFile);
                JSONObject login = new JSONObject(scan.nextLine());
                scan.close();
                ((EditText) findViewById(R.id.login_username)).setText(login.getString("username"));
                ((EditText) findViewById(R.id.login_password)).setText(login.getString("password"));
                ((CheckBox) findViewById(R.id.login_remember_me)).setChecked(true);

//                NameValuePair cred = new BasicNameValuePair(login.getString("username"), login.getString("password"));
//                new HttpLogin().execute(cred);

            } catch (Exception e) {
                _loginFile.delete(); // The file may have issues, do not cause infinite loop of failure for users
                e.printStackTrace();
            }
        }

        // Ask to turn location on if not on
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app will have weird behavior if GPS is not enabled. Do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void loginAccount(View view) {

        _loading = ProgressDialog.show(this, getString(R.string.logging_in), getString(R.string.chill_out), true);

        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        NameValuePair login = new BasicNameValuePair(username, hashPassword(password));
        new HttpLogin().execute(login);
    }

    private void rememberMe() {

        try {
            _loginFile.delete();
            _loginFile.createNewFile();

            JSONObject login = new JSONObject();
            login.put("username", ((EditText) findViewById(R.id.login_username)).getText().toString());
            login.put("password", ((EditText) findViewById(R.id.login_password)).getText().toString());

            PrintWriter pw = new PrintWriter(_loginFile);
            pw.print(login.toString());
            pw.close();

        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.remember_me_error), Toast.LENGTH_SHORT).show();
            _loginFile.delete();
            e.printStackTrace();
        }
    }

    public void promptRegister(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        _registerPrompt = li.inflate(R.layout.prompt_register, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb
                .setView(_registerPrompt)
                .setTitle("Register an account")
                .setCancelable(true)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _loading = ProgressDialog.show(_context, getString(R.string.registering), getString(R.string.chill_out), true);

                        String email = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_email)).getText().toString();
                        String username = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_username)).getText().toString();
                        String password = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_password)).getText().toString();
                        String confirm = ((EditText) _registerPrompt.findViewById(R.id.prompt_register_confirm_password)).getText().toString();

                        String emailRegex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
                        if (!email.matches(emailRegex)) {
                            _loading.dismiss();
                            Toast.makeText(_context, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String usernameRegex = "^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
                        if (!username.matches(usernameRegex)) {
                            _loading.dismiss();
                            Toast.makeText(_context, "Please enter a valid username", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!password.equals(confirm)) {
                            _loading.dismiss();
                            Toast.makeText(_context, "Passwords do not match", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        User u = new User();
                        u.setEmail(email);
                        u.setUsername(username);
                        u.setPassword(hashPassword(password));
                        u.setBlurb("I like to smash, bro");
                        u.setLastLoginTime(System.currentTimeMillis());
                        u.setLastLocationTime(System.currentTimeMillis());
                        u.setLastMessageTime(System.currentTimeMillis());
                        u.setGames(new ArrayList<Game>());
                        u.setEvents(new ArrayList<Event>());

//                        List<Notification> temp = new ArrayList<>();
//                        Notification n = new Notification();
//                        n.setMessage("Welcome to Super Smash Bros. Locator!");
//                        n.setSendTime(System.currentTimeMillis());
//                        n.setReceiver(u);
//                        n.setType(Notification.Type.SYSTEM);
//                        temp.add(n);
//                        u.setNotifications(temp);

                        u.setNotifications(new ArrayList<Notification>());
                        u.setFriends(new ArrayList<User>());
                        u.setConversations(new ArrayList<Conversation>());
                        u.setPrivate(false);

                        new HttpRegister().execute(u);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        adb.create().show();
    }

    private void initiateApp() {
        _loading = ProgressDialog.show(_context, "Initializing data...", "Almost there, chill", true);
        new AppInitializer().execute();
    }

    public void goToMain() {
        _loading.dismiss();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Follows the MySQL password function
    private String hashPassword(String plainText) {
        byte[] hashed = DigestUtils.sha1(DigestUtils.sha1(plainText.getBytes()));
        return "*" + bytesToHex(hashed).toUpperCase();
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

    private class HttpServerUrlInit extends AsyncTask<Void, Void, Void> {

        private String tinycc = "http://tiny.cc/ssblredirect";
        private String actual;

        private void getAwsUrl() {
            try {
                HttpClient client = new DefaultHttpClient();

                HttpParams params = new BasicHttpParams();
                params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);

                HttpGet request = new HttpGet(tinycc);
                request.setParams(params);

                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() == 303)
                    actual = response.getHeaders("Location")[0].getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... nothing) {
            getAwsUrl();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (actual != null)
                DataManager.setServerUrl(actual);
        }
    }

    private class HttpLogin extends AsyncTask<NameValuePair, Void, Void> {

        private User curUser;
        private String errorMessage;

        private void httpLogin(NameValuePair login) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/auth/login");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url.toString());

                request.addHeader("username", login.getName());
                request.addHeader("password", login.getValue());

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

//                System.out.println("login");
//                System.out.println(url.toString());
//                System.out.println(response.getStatusLine().getStatusCode());
//                System.out.println(jsonString);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 401)
                    errorMessage = "Incorrect login";
                else if (statusCode == 500)
                    errorMessage = "Database error. Sorry please try again :(";
                else {
                    if (jsonString.length() == 0)
                        return;

                    ObjectMapper om = new ObjectMapper();
                    om.enable(SerializationFeature.INDENT_OUTPUT);
                    om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    curUser = om.readValue(jsonString, User.class);
                }
            } catch (Exception e) {
                if (e instanceof HttpHostConnectException)
                    errorMessage = "Error connecting to server";
                curUser = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(NameValuePair... params) {
            httpLogin(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();
            if (curUser != null) {
                DataManager.setCurrentUser(curUser);
                DataManager.setBackupUser(curUser);
                DataManager.setConversations(curUser.getConversations());
                initiateApp();
            }
            else {
                if (errorMessage == null)
                    errorMessage = "Something unexpected happened...";
                Toast.makeText(_context, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class HttpRegister extends AsyncTask<User, Void, Void> {

        private User curUser;
        private String errorMessage;

        private void httpRegister(User newUser) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/edit/user/create");

            try {

                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
                request.setHeader("Accept", "application/json");

                ObjectMapper om = new ObjectMapper();
                om.enable(SerializationFeature.INDENT_OUTPUT);
                om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                StringEntity body = new StringEntity(om.writeValueAsString(newUser), "UTF-8");
                body.setContentType("application/json");
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

//                System.out.println("register");
//                System.out.println(url.toString());
//                System.out.println(response.getStatusLine().getStatusCode());
//                System.out.println(jsonString);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 500)
                    errorMessage = "Database error. Sorry please try again :(";
                else if (statusCode == 509)
                    errorMessage = "A user with your email/username already exists.";
                else {
                    if (jsonString.length() == 0)
                        return;
                    curUser = om.readValue(jsonString, User.class);
                }
            } catch (Exception e) {
                if (e instanceof HttpHostConnectException)
                    errorMessage = "Error connecting to server";
                curUser = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(User... params) {
            httpRegister(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();
            if (curUser != null) {
                DataManager.setCurrentUser(curUser);
                DataManager.setBackupUser(curUser);
                DataManager.setConversations(curUser.getConversations());
                initiateApp();
            }
            else {
                if (errorMessage == null)
                    errorMessage = "Something unexpected occurred";
                Toast.makeText(_context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AppInitializer extends AsyncTask<Void, Void, Void> {

        private void initiateData() {
            if (((CheckBox) findViewById(R.id.login_remember_me)).isChecked())
                rememberMe();
            else
                _loginFile.delete();

            new DataManager().initLocationData(getApplicationContext());
            DataManager.initSettings(getFilesDir());
            DataManager.reloadConversations();
            startService(new Intent(_context, MessagingService.class));
        }

        @Override
        protected Void doInBackground(Void... params) {
            initiateData();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            goToMain();
        }
    }
}
