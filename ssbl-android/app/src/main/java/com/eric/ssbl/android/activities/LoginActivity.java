package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.eric.ssbl.R;

public class LoginActivity extends Activity {

    final Activity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
    }

    public void loginAccount(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void registerAccount(View view) {
        LayoutInflater li = LayoutInflater.from(context);
        View registerPrompt = li.inflate(R.layout.prompt_register, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb
                .setView(registerPrompt)
                .setTitle("Register an account")
                .setCancelable(true)
                .setPositiveButton("Register",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Registering...", Toast.LENGTH_SHORT).show();
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
}
