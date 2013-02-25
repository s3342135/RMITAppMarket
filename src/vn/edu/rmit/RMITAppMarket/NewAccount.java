package vn.edu.rmit.RMITAppMarket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.rmit.Tools.Database;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This is the signup form.
 * 
 * @author Vuong Do Thanh Huy
 * 
 */
public class NewAccount extends Activity {

	public static final String regexFullname = "^[A-Za-z ]{5,}$";
	public static final String regexId = "^[sS]{1}[0-9]{7}$";
//	private NetworkConnection networkConnection;
	private Database database;
	private EditText fullnameText;
	private EditText idText;
	private EditText passwordText;
	private Button signUpButton;
	private Button backButton;
	private String errorMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newaccount);

		initComponents();

		fullnameText.setText(null);
		idText.setText(null);
		passwordText.setText(null);

		signUpButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				signup();
			}
		});

		backButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent loginForm = new Intent(NewAccount.this, Login.class);
				startActivity(loginForm);
				finish();
			}
		});

	}

	private void initComponents() {
		fullnameText = (EditText) findViewById(R.id.input_fullname_signup);
		idText = (EditText) findViewById(R.id.input_id_signup);
		passwordText = (EditText) findViewById(R.id.input_passwd_signup);
		signUpButton = (Button) findViewById(R.id.btn_signup);
		backButton = (Button) findViewById(R.id.btn_back);
		errorMessage = "";
//		networkConnection = new NetworkConnection();
		database = Database.createDatabase(getApplicationContext());
	}

	private void alertError(final String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void signup() {

		String fullname = fullnameText.getText().toString().trim();
		String username = idText.getText().toString().trim();
		String password = passwordText.getText().toString().trim();
		
		boolean validFullname = validateFullname(fullname);
		boolean validId = validateId(username);
		boolean validPasswd = validatePassword(password);
		boolean validUserName = database.checkUsername(username);
		
		if (!validFullname) {
			errorMessage += "Fullname must contain only character and space!\n";
		}
		if (!validId) {
			errorMessage += "ID must be Sxxxxxxx or sxxxxxxx!\n";
		}
		if (!validPasswd) {
			errorMessage += "Password must be 6 - 20 characters!\n";
		}
		if (!validUserName) {
			errorMessage += "This account has been registered!";
		}
		if (validFullname && validId && validPasswd && validUserName) {
			// everything OK. Submit to server.
			//networkConnection.signUp(fullname.replace(" ", "%20"), id, password);
			database.signUp(fullname, username, password);
			database.closeDatabase();
			Intent loginForm = new Intent(NewAccount.this, Login.class);
			startActivity(loginForm);
			finish();
		} else {
			alertError(errorMessage);
		}
	}

	private boolean validateFullname(String fullname) {
		Pattern p = Pattern.compile(regexFullname);
		Matcher m = p.matcher(fullname);
		return m.matches();
	}

	private boolean validateId(String id) {
		Pattern p = Pattern.compile(regexId);
		Matcher m = p.matcher(id);
		return m.matches();
	}

	//password must between 6 - 20
	private boolean validatePassword(String password) {
		int l = password.length();

		if ((6 <= l) && (l <= 20)) {
			return true;
		} else {
			return false;
		}
	}

}
