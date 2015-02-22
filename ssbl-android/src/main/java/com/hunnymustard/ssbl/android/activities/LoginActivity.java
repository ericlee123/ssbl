package com.hunnymustard.ssbl.android.activities;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hunnymustard.ssbl.android.R;

public class LoginActivity extends Activity {
	
	private ProgressDialog _loading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	public void accountLogin(View view) {
		
		// Clean up later
		String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
		String password = ((EditText) findViewById(R.id.login_password)).getText().toString();
		
		if (username.length() == 0 || password.length() == 0) {
			Toast.makeText(this, "You left something blank.", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (((CheckBox) findViewById(R.id.login_remember_me)).isChecked()) {
			
		}
		
		HttpLogin hl = new HttpLogin();
		NameValuePair ep = new BasicNameValuePair(username, password);
		hl.execute(ep);
		_loading = ProgressDialog.show(this, "Logging in", "Have dinner with me!!!", true);
	}
	
	public void accountRegister(View view) {
		Intent intent = new Intent(this, LoginActivity.class); // RegisterActivity
		startActivity(intent);
	}
	
	private void goToMap() {
		Intent intent = new Intent(this, LoginActivity.class); // PlayerMapActivity
		startActivity(intent);
		finish();
	}
	
	private Activity getActivity() {
		return this;
	}
	
	private class HttpLogin extends AsyncTask<NameValuePair, Void, Void> {

		private String _errorMessage = null;
		
		private void httpLogin(NameValuePair login) {
			
			String url = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/SSBLServlet/Basic";
			try {
				// Use HttpGet b/c HTTP specs
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				
				request.addHeader("type", "login");
				request.addHeader("username", login.getName());
				
				// Hash the password
				String password = login.getValue();
				byte[] utf8 = password.getBytes();
				byte[] temp = DigestUtils.sha1(DigestUtils.sha1(utf8));
				password = "*" + bytesToHex(temp).toUpperCase();
				request.addHeader("password", password);
				
				HttpResponse response = client.execute(request);		
				
				if (response.getHeaders("response_phrase").length == 0)
					_errorMessage = "An unknown error has occurred.";
				else {
					String phrase = response.getHeaders("response_phrase")[0].getValue();
					if (phrase.equals("okay")) {
//						PlayerManager.setCurUser(login.getName());
					}
					else
						_errorMessage = phrase;
				}
				
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
		protected Void doInBackground(NameValuePair... params) {
			httpLogin(params[0]);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void what) {
			_loading.dismiss();
			if (_errorMessage == null)
				goToMap();
			else
				Toast.makeText(getActivity(), _errorMessage, Toast.LENGTH_LONG).show();
		}
		
	}
}
