package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginActivity extends Activity {

    private Context _context = this;
    private File _loginFile;
    private ProgressDialog _loading;
    private View _registerPrompt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("start service");
        startService(new Intent(getBaseContext(), MessagingService.class));

        _loginFile = new File(getFilesDir(), "yummy.hunnymustard");

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        File opened = new File(getFilesDir(), "opened");
        if (!opened.exists()) {
            try {
                opened.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            adb
                    .setTitle("Welcome!!!")
                    .setMessage("We listened to your feedback and overhauled the poop out of everything! New features include user privacy, " +
                            "messaging functions, and a better user interface. In case you made an account with the old application, you will have" +
                            " to make a new account (compatibility issues). Sorry! Everything should be working a lot better, and if you stumble upon" +
                            " any errors, shoot us an email at hunnymustardapps@gmail.com. Have fun smashing!")
                    .setNeutralButton("Okay!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            adb.create().show();
        }

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
    }

    public void loginAccount(View view) {

        startActivity(new Intent(this, EditEventActivity.class));
        return;

//        _loading = ProgressDialog.show(this, getString(R.string.logging_in), getString(R.string.chill_out), true);
//
//        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
//        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();
//
//        NameValuePair login = new BasicNameValuePair(username, hashPassword(password));
//        new HttpLogin().execute(login);
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

                        List<Notification> temp = new ArrayList<>();
                        Notification welcome = new Notification();
                        welcome.setMessage("Welcome to Super Smash Bros. Locator!");
                        welcome.setSendTime(System.currentTimeMillis());
                        welcome.setType(Notification.Type.SYSTEM);
                        temp.add(welcome);
                        u.setNotifications(temp);

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

        // set the current user in the general manager
        if (((CheckBox) findViewById(R.id.login_remember_me)).isChecked())
            rememberMe();
        else
            _loginFile.delete();

        new DataManager().initNearby(this);
        DataManager.initSettings(getFilesDir());
        DataManager.setAppActive(true);
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

                System.out.println(login.getValue());
                System.out.println("login url: " + url.toString());
                System.out.println("curUser: " + jsonString);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 401)
                    errorMessage = "Incorrect login";
                else if (statusCode == 500)
                    errorMessage = "Database error. Sorry please try again :(";
                else {
                    if (jsonString.length() == 0)
                        return;

                    ObjectMapper om = new ObjectMapper();
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
            if (curUser != null) {
                curUser.setLastLoginTime(System.currentTimeMillis());
                DataManager.updateCurUser(curUser);
                initiateApp();
            }
            else {
                if (errorMessage == null)
                    errorMessage = "Something unexpected happened...";

                Toast.makeText(_context, errorMessage, Toast.LENGTH_LONG).show();
                _loading.dismiss();
            }
        }
    }

    private class HttpRegister extends AsyncTask<User, Void, Void> {

        private User curUser;
        private String errorMessage;

        private void httpRegister(User newUser) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/auth/register");

            System.out.println("register url: " + url.toString());
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
                request.addHeader("Accept", "application/json");

                ObjectMapper om = new ObjectMapper();
                om.enable(SerializationFeature.INDENT_OUTPUT);
                om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//                StringEntity body = new StringEntity("{\n" +
//                        " \"@id\" : 1,\n" +
//                        " \"location\" : null,\n" +
//                        " \"private\" : null,\n" +
//                        " \"username\" : \"weffff\",\n" +
//                        " \"password\" : \"pwd\",\n" +
//                        " \"email\" : \";lkjas@you.com\",\n" +
//                        " \"userId\" : null,\n" +
//                        " \"lastLoginTime\" : null,\n" +
//                        " \"lastLocationTime\" : null,\n" +
//                        " \"lastMessageTime\" : null,\n" +
//                        " \"blurb\" : null,\n" +
//                        " \"games\" : null,\n" +
//                        " \"events\" : null,\n" +
//                        " \"notifications\" : null,\n" +
//                        " \"friends\" : null,\n" +
//                        " \"conversations\" : null\n" +
//                        "}");
                StringEntity body = new StringEntity(om.writeValueAsString(newUser), "UTF-8");
                body.setContentType("application/json");
                request.setEntity(body);

                System.out.println(om.writeValueAsString(newUser));

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("register status code: " + statusCode);
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
            if (curUser != null) {
                DataManager.updateCurUser(curUser);
                initiateApp();
            }
            else {
                if (errorMessage == null)
                    errorMessage = "Something unexpected occurred";

                _loading.dismiss();
                Toast.makeText(_context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
