package vn.edu.rmit.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * We implement a database to store app list locally.
 * 
 * @author Vuong Do Thanh Huy
 * 
 */
public class Database {

	public static final String DATABASE_NAME = "RMITAppMarket";
	public static final String APP_TABLE_NAME = "android_AppList";
	public static final String ACCOUNT_TABLE_NAME = "android_Account";
	public static final String APP_NAME = "appName";
	public static final String APP_DESC = "appDesc";
	public static final String APP_AVATAR = "appAvatar";
	public static final String APP_STAR = "appStar";
	public static final String APP_PATH = "appPath";
	public static final String UPDATE_AVAIL = "updateAvail";
	public static final String FULLNAME = "fullname";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	private static DatabaseModel dbModel = null;

	public static Database createDatabase(final Context context) {
		return new Database(context, DATABASE_NAME, null, 1);
	}

	private Database(final Context context, final String name,
			final CursorFactory factory, final int version) {
		dbModel = new DatabaseModel(context, name, factory, version);
	}

	/*
	 * Set data into database
	 */
	public void insertNewApp(final String appNameInput,
			final String appDescInput, final String appAvatarInput,
			final String appStarInput, final String appPathInput,
			final String updateAvail) {

		ContentValues cv = new ContentValues();
		cv.put(APP_NAME, appNameInput);
		cv.put(APP_DESC, appDescInput);
		cv.put(APP_AVATAR, appAvatarInput);
		cv.put(APP_STAR, appStarInput);
		cv.put(APP_PATH, appPathInput);
		cv.put(UPDATE_AVAIL, updateAvail);
		dbModel.getWritableDatabase().insert(APP_TABLE_NAME, APP_NAME, cv);
	}

	public boolean signUp(final String fullname, final String username,
			final String password) {

		ContentValues cv = new ContentValues();
		cv.put(FULLNAME, fullname);
		cv.put(USERNAME, username);
		cv.put(PASSWORD, password);
		dbModel.getWritableDatabase().insert(ACCOUNT_TABLE_NAME, fullname, cv);
		return true;
	}

	public boolean checkUsername(final String username) {

		String SQL = "SELECT username FROM android_Account WHERE username=?";

		try {
			Cursor result = dbModel.getReadableDatabase().rawQuery(SQL,
					new String[] { username });
			boolean check = false;
			if (result.getCount() == 0) {
				check = true;
			}
			result.close();
			return check;
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean signIn(final String username, final String password) {
		String SQL = "SELECT username, password FROM android_Account WHERE username=? AND password=?";
		try {
			Cursor result = dbModel.getReadableDatabase().rawQuery(SQL, new String[]{username, password});
			boolean check = false;
			if (result.getCount() == 1) {
				check = true;
			}
			result.close();
			return check;
		} catch (Exception ex) {
			return false;
		}
	}

	/*
	 * Return a cursor pointing to all records in db.
	 * 
	 * @return Cursor
	 */
	public Cursor getAllData() {
		return dbModel.getReadableDatabase().query(
				APP_TABLE_NAME,
				new String[] { APP_NAME, APP_DESC, APP_AVATAR, APP_STAR,
						APP_PATH, UPDATE_AVAIL }, null, null, null, null, null);
	}

	public Cursor getAllImagePath() {
		return dbModel.getReadableDatabase().query(APP_TABLE_NAME,
				new String[] { APP_AVATAR }, null, null, null, null, null);
	}

	public Cursor getId() {
		return dbModel.getReadableDatabase().query(APP_TABLE_NAME,
				new String[] { "id" }, null, null, null, null, null);
	}

	public Cursor getSpecificData(final String findWhere,
			final String[] findWhat) {
		return dbModel.getReadableDatabase().query(
				APP_TABLE_NAME,
				new String[] { APP_NAME, APP_DESC, APP_AVATAR, APP_STAR,
						APP_PATH, UPDATE_AVAIL }, findWhere + "=?", findWhat,
				null, null, null);
	}

	public void removeAllData() {
		dbModel.removeAllData();
	}

	public void closeDatabase() {
		dbModel.close();
	}

}
