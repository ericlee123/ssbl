package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.eric.ssbl.R;

public class LoginActivity extends Activity {

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

    }
}
