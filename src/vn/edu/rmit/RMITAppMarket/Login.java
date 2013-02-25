package vn.edu.rmit.RMITAppMarket;

import vn.edu.rmit.Tools.Database;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the login form.
 * 
 * @author Vuong Do Thanh Huy
 * 
 */
public class Login extends Activity {

	public static final int MAIN_FORM = 1;
	public static final int SIGNUP_FORM = 2;
	private EditText idText;
	private EditText passwordText;
	private Button loginButton;
	private TextView registerNewAccount;
	private Database database;
//	private NetworkConnection networkConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initComponents();
	}

	private void initComponents() {
		idText = (EditText) findViewById(R.id.input_username);
		passwordText = (EditText) findViewById(R.id.input_passwd);
		loginButton = (Button) findViewById(R.id.btn_login);
		registerNewAccount = (TextView) findViewById(R.id.label_signup);
		
//		networkConnection = new NetworkConnection();
		
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				login();
			}
		});
		registerNewAccount.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				registerNewAccount();
			}
		});
		database = Database.createDatabase(getApplicationContext());
	}

	private void login() {

		String username = idText.getText().toString().trim();
		String password = passwordText.getText().toString().trim();
//		boolean status = networkConnection.signIn(id, password);
		boolean status = database.signIn(username, password);
		if (username.equals("") || password.equals("")) {
			status = false;
		}
		if (status) {
			// Successful login.
			loginOK();
		} else {
			// error!
			alertError("Sorry! Wrong ID or Passwd. Try again!");
			passwordText.setText(null);
		}
	}

	private void loginOK() {
		// switch to main form.
		alertError("Login successful!");
		database.closeDatabase();
		Intent mainForm = new Intent(Login.this, RMITAppMarket.class);
		startActivity(mainForm);
		finish();
	}

	private void registerNewAccount() {

		// switch intent signup
		Intent signupForm = new Intent(Login.this, NewAccount.class);
		startActivityForResult(signupForm, SIGNUP_FORM);
		finish();
	}

	private void alertError(final String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	//check later
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//		case SIGNUP_FORM:
//			idText.setText(null);
//			passwordText.setTag(null);
//		case MAIN_FORM:
//			alertError("You have been logged off. Login again.");
//			idText.setText(null);
//			passwordText.setText(null);
//		}
//	}
}