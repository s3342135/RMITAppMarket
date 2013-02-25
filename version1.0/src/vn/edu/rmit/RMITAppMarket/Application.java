package vn.edu.rmit.RMITAppMarket;

import java.io.File;

import vn.edu.rmit.Tools.ApplicationHandler;
import vn.edu.rmit.Tools.NetworkConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Application extends Activity {

	public static final String APPLICATION_IMAGE_INDEX = "Image index";
	public static final String APPLICATION_NAME = "Application Name";
	public static final String RATING_STAR = "Rating star";
	public static final String APPLICATION_DESCRIPTION = "Application Description";
	public static final String INSTALL_PATH = "Install Path";
	private NetworkConnection networkConnection;
	private ApplicationHandler imageHandling;
	private int REQUEST_CODE = 101;
	private String extertalStoragePath;
	private Intent intent;
	private int imageIndex;
	private String appName;
	private String appStar;
	private String appDesc;
	private Uri imageUri;
	private String imageDescription;
	private String installPath;
	private Button installButton;
	private ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_layout);

		// get data from intent
		networkConnection = new NetworkConnection();
		intent = getIntent();

		imageIndex = intent.getIntExtra(APPLICATION_IMAGE_INDEX, 0);
		appName = intent.getStringExtra(APPLICATION_NAME);
		appStar = intent.getStringExtra(RATING_STAR);
		appDesc = intent.getStringExtra(APPLICATION_DESCRIPTION);
		installPath = intent.getStringExtra(INSTALL_PATH);

		// set information for image
		imageHandling = ApplicationHandler.getApplicationHandler();

		imageUri = imageHandling.getImageUriAtPosition(imageIndex);
		imageDescription = imageHandling.getImageNameAtPosition(imageIndex);

		extertalStoragePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		// initialize data
		init();
		loadingDialog = new ProgressDialog(Application.this);
		loadingDialog.setTitle("Loading");
		loadingDialog.setMessage("Loading, please wait");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		deleteApkFile();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				deleteApkFile();
			}
		}
	}

	public void deleteApkFile() {
		File file = new File(extertalStoragePath + File.separator + appName
				+ ".apk");
		if (file.exists()) {
			file.delete();
		}
		android.util.Log.i("Application", "Delete file " + appName + ".apk");
	}

	public void init() {
		// set image
		ImageView image = (ImageView) findViewById(R.id.application_image);
		image.setImageURI(imageUri);
		image.setContentDescription(imageDescription);

		// set application name
		TextView applicationName = (TextView) findViewById(R.id.application_name);
		applicationName.setText(appName);

		// set rating bar
		RatingBar ratingBar = (RatingBar) findViewById(R.id.star_rate);
		ratingBar.setRating(Float.parseFloat(appStar));

		// set description
		TextView applicationDescription = (TextView) findViewById(R.id.application_description);
		applicationDescription.setText(appDesc);

		installButton = (Button) findViewById(R.id.install_button);
		installButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				BackgroundThread backgroundThread = new BackgroundThread();
				backgroundThread.execute();
			}
		});

		Button backButton = (Button) findViewById(R.id.application_back_button);
		backButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				deleteApkFile();
				finish();
			}
		});
	}

	public void installApp(Uri appFile) {
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(appFile,
				"application/vnd.android.package-archive");
		Log.i("Application", "installApp() runned");
		startActivityForResult(installIntent, REQUEST_CODE);
	}

	private class BackgroundThread extends AsyncTask<Void, Void, Uri> {

		@Override
		protected void onPreExecute() {
			installButton.setEnabled(false);
			loadingDialog.show();
		}

		@Override
		protected Uri doInBackground(Void... arg0) {

			File appFile = new File(extertalStoragePath + File.separator
					+ appName + ".apk");
			if (!appFile.exists()) {
				networkConnection.downloadApp(
						installPath, extertalStoragePath, appName + ".apk");
			}
			return Uri.fromFile(appFile);
		}

		@Override
		protected void onPostExecute(Uri result) {
			loadingDialog.dismiss();
			installButton.setEnabled(true);
			installApp(result);
		}
	}
}
