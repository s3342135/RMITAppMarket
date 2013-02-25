package vn.edu.rmit.RMITAppMarket;

import java.io.File;

import vn.edu.rmit.Tools.ApplicationHandler;
import vn.edu.rmit.Tools.Database;
import vn.edu.rmit.Tools.GridCellAdapter;
import vn.edu.rmit.Tools.NetworkConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * This is the main program.
 * 
 * @author Vuong Do Thanh Huy
 * 
 */
public class RMITAppMarket extends Activity {

	public static final int MENU_UPDATE = 1;
	private NetworkConnection networkConnection;
	private GridView gridView;
	private Database database;
	private ApplicationHandler applicationHandler;
	private static File externalStorage;
	private String absolutePath;
	private ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainprogram);

		// initialize the data
		init();

		// load data from database
		loadDataFromDatabase();

		// initialize GridView
		initializeGridView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.closeDatabase();
		finish();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, MENU_UPDATE, Menu.NONE, "Update").setIcon(
				R.drawable.update_icon);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENU_UPDATE:
			new BackgroundThread().execute();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void init() {

		// initialize components
		database = Database.createDatabase(getApplicationContext());
		applicationHandler = ApplicationHandler.getApplicationHandler();

		// create external storage folder to store image

		externalStorage = Environment.getExternalStorageDirectory();
		if (!externalStorage.exists()) {
			externalStorage.mkdirs();
		}
		absolutePath = externalStorage.getAbsolutePath();

		gridView = (GridView) findViewById(R.id.grid_view);

		loadingDialog = new ProgressDialog(RMITAppMarket.this);
		loadingDialog.setTitle("Update");
		loadingDialog.setMessage("Updating data from server, please wait");
		networkConnection = new NetworkConnection();
	}

	public void loadDataFromDatabase() {

		Cursor cursor = database.getAllData();

		String[] appNames = new String[cursor.getCount()];
		String[] appDesc = new String[cursor.getCount()];
		float[] ratingStar = new float[cursor.getCount()];
		Uri[] imageUris = new Uri[cursor.getCount()];
		String[] imageNames = new String[cursor.getCount()];
		String[] installPath = new String[cursor.getCount()];
		int count = 0;

		android.util.Log.i("Main", "loadDataFromDatabase(); Data query: "
				+ cursor.getCount());

		while (cursor.moveToNext()) {
			String imageFileName = cursor.getString(2).split("/")[2];

			File imageFile = new File(absolutePath + File.separator
					+ imageFileName);

			appNames[count] = cursor.getString(0);
			appDesc[count] = cursor.getString(1);
			ratingStar[count] = Float.parseFloat(cursor.getString(3));
			imageNames[count] = imageFileName;
			imageUris[count] = Uri.parse(imageFile.getAbsolutePath());
			installPath[count] = cursor.getString(4);
			count++;
		}
		cursor.close();

		// initialize application
		applicationHandler.setApplicationNames(appNames);
		applicationHandler.setApplicationDescription(appDesc);
		applicationHandler.setRatingStar(ratingStar);
		applicationHandler.setImageNames(imageNames);
		applicationHandler.setApplicationImageUris(imageUris);
		applicationHandler.setInstallPath(installPath);
	}

	public void initializeGridView() {

		android.util.Log.i("Main", "initializeGridView");

		gridView.setAdapter(new GridCellAdapter(this, getLayoutInflater(),
				R.layout.grid_cell, applicationHandler.getApplicationNames()));

		gridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Intent i = new Intent(RMITAppMarket.this, Application.class);

				i.putExtra(Application.APPLICATION_IMAGE_INDEX, position);
				i.putExtra(Application.APPLICATION_NAME,
						applicationHandler.getApplicationNames()[position]);
				i.putExtra(Application.RATING_STAR,
						applicationHandler.getRatingStar()[position] + "");
				i.putExtra(
						Application.APPLICATION_DESCRIPTION,
						applicationHandler.getApplicationDescription()[position]);
				i.putExtra(Application.INSTALL_PATH,
						applicationHandler.getInstallPath()[position]);
				android.util.Log.i("GridView", "Start activity");

				startActivity(i);
			}
		});
	}

	public void updateDatabase() {

		Log.i("updataDatabase", "clearImageInExternalStorageFolder");
		Cursor result = database.getAllImagePath();

		while (result.moveToNext()) {
			String imageFileName = result.getString(0).split("/")[2];
			File imageFile = new File(absolutePath + File.separator
					+ imageFileName);
			if (imageFile.exists()) {
				imageFile.delete();
			}
		}
		result.close();

		Log.i("updataDatabase", "syncDataWithServer");
		// remove old data
		database.removeAllData();

		// download android_AppList database
		networkConnection.downloadAppList();

		String[] appList = networkConnection.getAppList();
		String[] appNames = new String[appList.length];
		String[] appDesc = new String[appList.length];
		float[] ratingStar = new float[appList.length];
		Uri[] imageUris = new Uri[appList.length];
		String[] imageNames = new String[appList.length];
		String[] installPath = new String[appList.length];

		for (int i = 0; i < appList.length; i++) {

			String[] bits = appList[i].split("--");
			// database.insertNewData(appNameInput, appDescInput,
			// appAvatarInput, appStarInput, appPathInput, updateAvail)
			Log.i("Bits length", bits.length + "");
			database.insertNewApp(bits[0], bits[1], bits[2], bits[3], bits[4],
					bits[5]);

			// save image to external storage
			String imageFileName = bits[2].split("/")[2];

			networkConnection.downloadImage(bits[2], absolutePath,
					imageFileName);
			Log.i("Main", "download and save image to external directory: "
					+ imageFileName);

			// load image to from external storage
			File imageFile = new File(absolutePath + File.separator
					+ imageFileName);
			appNames[i] = bits[0];
			appDesc[i] = bits[1];
			ratingStar[i] = Float.parseFloat(bits[3]);
			imageUris[i] = Uri.parse(imageFile.getAbsolutePath());
			imageNames[i] = imageFileName;
			installPath[i] = bits[4];
		}

		// initialize application
		applicationHandler.setApplicationNames(appNames);
		applicationHandler.setApplicationDescription(appDesc);
		applicationHandler.setRatingStar(ratingStar);
		applicationHandler.setImageNames(imageNames);
		applicationHandler.setApplicationImageUris(imageUris);
		applicationHandler.setInstallPath(installPath);
	}

	private class BackgroundThread extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			updateDatabase();
			return (null);
		}

		@Override
		protected void onPostExecute(Void result) {
			loadingDialog.dismiss();
			database.closeDatabase();

			Intent intent = new Intent(RMITAppMarket.this, RMITAppMarket.class);
			RMITAppMarket.this.startActivity(intent);
			RMITAppMarket.this.finish();
		}
	}
}
