package vn.edu.rmit.Tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * This class create a custom SQLite Model. Used in Database.java
 * 
 * @author Vuong Do Thanh Huy
 * 
 */
public final class DatabaseModel extends SQLiteOpenHelper {

	private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS android_AppList";
	private static final String SQL_DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS android_Account";
	private static final String SQL_DELETE_ALL_DATA = "DELETE FROM android_AppList";
	private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS android_AppList"
			+ "(id integer primary key autoincrement,"
			+ "appName text NOT NULL,"
			+ "appDesc text NOT NULL,"
			+ "appAvatar text NOT NULL,"
			+ "appStar integer NOT NULL,"
			+ "appPath text NOT NULL," 
			+ "updateAvail text NOT NULL)";
	private static final String SQL_ACCOUNT_DATABASE = "CREATE TABLE IF NOT EXISTS android_Account"
			+ "(id integer primary key autoincrement," +
			"fullname text NOT NULL," +
			"username text NOT NULL," +
			"password text NOT NULL)";

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DatabaseModel(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_DROP_TABLE);
		db.execSQL(SQL_CREATE_TABLE);
		db.execSQL(SQL_DROP_ACCOUNT_TABLE);
		db.execSQL(SQL_ACCOUNT_DATABASE);
		
		android.util.Log.d("DatabaseModel",
				"DatabaseModel onCreate function created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		android.util.Log.d("DatabaseModel",
				"DatabaseModel onUpgrade function runs");
		onCreate(db);
	}

	public void removeAllData() {
		// remove all data in android_AppList
		getWritableDatabase().execSQL(SQL_DELETE_ALL_DATA);
	}
}
