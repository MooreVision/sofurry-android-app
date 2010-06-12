package com.sofurry;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AccountActivity extends Activity {
	
	Button buttonOk;
	Button buttonCancel;
	EditText textfieldUsername;
	EditText textfieldPassword;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.loginlayout);
	    buttonOk = (Button) findViewById(R.id.AccountOkButton);
	    buttonCancel= (Button) findViewById(R.id.AccountCancelButton);
	    textfieldUsername = (EditText) findViewById(R.id.UsernameField);
	    textfieldPassword = (EditText) findViewById(R.id.PasswordField);
	    textfieldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
	    buttonOk.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Log.i("logon", "ok clicked");
				storeCredentials();
			}});
	    buttonCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Log.i("logon", "cancel clicked");
				closeActivity();
			}});
	    
	    // Restore preferences
	    Authentication.loadAuthenticationInformation(this);
	    String username = Authentication.getUsername();
	    String password = Authentication.getPassword();
	    textfieldUsername.setText(username);
	    textfieldPassword.setText(password);

	}
	
	private void storeCredentials() {
	      String username = textfieldUsername.getText().toString();
	      String password = textfieldPassword.getText().toString();
	      Authentication.updateAuthenticationInformation(this, username, password);
	      closeActivity();
	}
	
	private void closeActivity() {
		Log.i("closeActivity","closing...");
        setResult(RESULT_OK);
        finish();
	}

}
