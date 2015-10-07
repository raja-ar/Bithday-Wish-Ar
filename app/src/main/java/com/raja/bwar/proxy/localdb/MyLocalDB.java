/*
 * author: Prajwol, Sara
 */
package com.raja.bwar.proxy.localdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.raja.bwar.proxy.model.Filter;
import com.raja.bwar.proxy.model.MyFriend;
import com.raja.bwar.util.MyUtils;
import com.raja.bwar.util.Report;

import java.util.ArrayList;
import java.util.List;

import static com.raja.bwar.util.MyUtils.TAG;

public class MyLocalDB {
	private static final String DATABASE_NAME = "bwar";
	private static final String TABLE_FRIEND = "friend";
	private static final String TABLE_FRIEND_TEMP = "friend_temp";
	private static final String TABLE_SETTINGS = "settings";
	private static final int DATABASE_VERSION = 4;

	public static final String KEY_ID = "friendID";
	public static final int ID_COLUMN = 0;
	// The name and column index of each column in friend's table
	public static final String KEY_FBID = "facebookID";
	public static final int FBID_COLUMN = 1;
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 2;
	public static final String KEY_BIRTHDAY = "birthday";
	public static final int BIRTHDAY_COLUMN = 3;
	public static final String KEY_PIC = "picture";
	public static final int PIC_COLUMN = 4;
	public static final String KEY_BDAYMESSAGE = "message";
	public static final int BDAYMESSAGE_COLUMN = 5;
	public static final String KEY_AUTOPOST = "autopost";
	public static final int AUTOPOST_COLUMN = 6;

	// The name and column index of each column in setting's table
	public static final String KEY_SETTINGS_ID = "settingsId";
	public static final String KEY_SETTINGS_VALUE = "settingsValue";
	public static final int SETTINGS_KEY_COLUMN = 0;
	public static final int SETTINGS_VALUE_COLUMN = 1;

	private static final String SETTINGS_CREATE = "create table "
			+ TABLE_SETTINGS + " ( " + KEY_SETTINGS_ID + " varchar(255), "
			+ KEY_SETTINGS_VALUE + " varchar(255)" + " );";

	private static final String FRIENDS_CREATE = "create table " + TABLE_FRIEND
			+ " (" + KEY_ID + " integer primary key autoincrement, " + KEY_FBID
			+ " int, " + KEY_NAME + " varchar(50)," + KEY_BIRTHDAY
			+ " varchar(25), " + KEY_PIC + " varchar(255)," + KEY_BDAYMESSAGE
			+ " varchar(255), " + KEY_AUTOPOST + " varchar(10)" + " );";

	private static final String FRIENDS_TEMP_CREATE = "create table "
			+ TABLE_FRIEND_TEMP + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_FBID + " int, "
			+ KEY_NAME + " varchar(50)," + KEY_BIRTHDAY + " varchar(25), "
			+ KEY_PIC + " varchar(255)," + KEY_BDAYMESSAGE + " varchar(255), "
			+ KEY_AUTOPOST + " varchar(10)" + " );";

	private SQLiteDatabase localDB;
	// private final Context context;
	private LocalDBHelper dbHelper;

	public MyLocalDB(Context context) {
		// this.context = context;
		this.dbHelper = new LocalDBHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public void open() throws SQLException {
		localDB = dbHelper.getWritableDatabase();
	}

	private static class LocalDBHelper extends SQLiteOpenHelper {

		public LocalDBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "DB create");
			db.execSQL(FRIENDS_CREATE);
			db.execSQL(SETTINGS_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrading from " + oldVersion + " to " + newVersion
					+ ", which may destroy all the old data.");

			// Drop the old table
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_TEMP);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);

			// Create a new one
			onCreate(db);
		}
	}

	// Local storage should have one table for key-value pair
	public String getSettings(String settingsKey) {
		String settingsValue = "";
		try {
			Cursor settings = localDB.query(true, TABLE_SETTINGS, null,
					KEY_SETTINGS_ID + " = ?", new String[] { settingsKey },
					null, null, null, null);
			if ((settings.getCount() == 0) || !settings.moveToFirst()) {
				return "";
			}
			settingsValue = settings.getString(SETTINGS_VALUE_COLUMN);
		} catch (SQLiteException ex) {
			Log.d(TAG, ex.getMessage());
		}
		Log.v(TAG, "setting: " + settingsValue);
		return settingsValue;
	}

	public void setSettings(String key, String value) {
		Log.d(TAG, key + ", " + value);
		try {
			localDB.execSQL("delete from " + TABLE_SETTINGS + " where "
					+ KEY_SETTINGS_ID + " = '" + key + "';");
			localDB.execSQL("insert into " + TABLE_SETTINGS + " values ('"
					+ key + "', '" + value + "');");
		} catch (SQLiteException ex) {
			Log.d(TAG, ex.getMessage());
		}
	}

	public List<MyFriend> getAllFriends() {
		return getFriendsFilteredBy(null);
	}

	public MyFriend getFriendByFbID(String facebookId) {

		Cursor friendResults = localDB.query(false, TABLE_FRIEND, null,
				KEY_FBID + "=" + facebookId, null, null, null, null, null);

		if ((friendResults.getCount() == 0) || !friendResults.moveToFirst()) {
			return null;
		}

		MyFriend friend = new MyFriend();
		friend.setBdayMessage(friendResults.getString(BDAYMESSAGE_COLUMN));
		friend.setAutoPost(Boolean.valueOf(friendResults
				.getString(AUTOPOST_COLUMN)));
		return friend;
	}

	public List<MyFriend> getFriendsFilteredBy(Filter filterBy) {
		List<MyFriend> friends = new ArrayList<MyFriend>();
		String[] resultColumns = new String[] { KEY_ID, KEY_FBID, KEY_NAME,
				KEY_BIRTHDAY, KEY_PIC, KEY_BDAYMESSAGE, KEY_AUTOPOST };

		String selection = null;
		String[] selectionArgs = null;
		String orderBy = KEY_NAME;

		if (filterBy != null) {
			switch (filterBy) {
			case MONTH:
				selection = KEY_BIRTHDAY + " LIKE ?";
				selectionArgs = new String[1];
				selectionArgs[0] = MyUtils.getCurrentMonth() + "%";
				orderBy = KEY_BIRTHDAY;
				Log.v(TAG, "localdb.month- " + selectionArgs[0]);
				break;
			case WEEK:
				selection = KEY_BIRTHDAY + " >= ? and " + KEY_BIRTHDAY
						+ " <= ?";
				selectionArgs = new String[2];
				selectionArgs[0] = MyUtils.getCurrentMonth() + "/"
						+ MyUtils.getCurrentWeekDays()[0];
				selectionArgs[1] = MyUtils.getCurrentMonth() + "/"
						+ MyUtils.getCurrentWeekDays()[1];
				orderBy = KEY_BIRTHDAY;
				Log.v(TAG, "localdb.week- " + selectionArgs[0] + ","
						+ selectionArgs[1]);
				break;
			case DAY:
				selection = KEY_BIRTHDAY + " LIKE ?";
				selectionArgs = new String[1];
				selectionArgs[0] = MyUtils.getCurrentMonth() + "/"
						+ MyUtils.getCurrentWeekDays()[0] + "%";
				selectionArgs[0] = "12/23%";
				Log.v(TAG, "localdb.day- " + selectionArgs[0]);
				break;
			default:
				break;
			}
		}
		/*
		 * TESTING else{ selection = "name LIKE ?"; selectionArgs = new
		 * String[1]; selectionArgs[0] = "%aad%"; }
		 */

		Cursor allRows = localDB.query(false, TABLE_FRIEND, resultColumns,
				selection, selectionArgs, null, null, orderBy, null);

		if (allRows.moveToFirst()) {
			do {
				String fbId = allRows.getString(FBID_COLUMN);
				String name = allRows.getString(NAME_COLUMN);
				String birthday = allRows.getString(BIRTHDAY_COLUMN);
				String picture = allRows.getString(PIC_COLUMN);
				String message = allRows.getString(BDAYMESSAGE_COLUMN);
				boolean isAutoPost = allRows.getString(AUTOPOST_COLUMN).equals(
						"false") ? false : true;
				MyFriend friend = new MyFriend(fbId, name, birthday, picture,
						message, isAutoPost);
				friends.add(friend);
			} while (allRows.moveToNext());
		}
		return friends;
	}

	public Report syncFriends(List<MyFriend> friends) {
		Log.v(TAG, "mylocaldb.syncfriends Sync started!" + friends.size());
		localDB.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_TEMP);
		localDB.execSQL(FRIENDS_TEMP_CREATE);

		for (MyFriend friend : friends) {
			insertFriend(friend, TABLE_FRIEND_TEMP);
		}

		localDB.execSQL("update " + TABLE_FRIEND_TEMP
				+ " set message = (select message from " + TABLE_FRIEND
				+ " where " + TABLE_FRIEND + ".facebookID = "
				+ TABLE_FRIEND_TEMP + ".facebookID)");

		localDB.execSQL("update " + TABLE_FRIEND_TEMP
				+ " set message = ' ' where message is null;");

		localDB.execSQL("update " + TABLE_FRIEND_TEMP
				+ " set autopost = (select autopost from " + TABLE_FRIEND
				+ " where " + TABLE_FRIEND + ".facebookID = "
				+ TABLE_FRIEND_TEMP + ".facebookID)");

		localDB.execSQL("update " + TABLE_FRIEND_TEMP
				+ " set autopost = 'false' where autopost is null;");

		localDB.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND);
		localDB.execSQL("ALTER TABLE " + TABLE_FRIEND_TEMP + " RENAME to "
				+ TABLE_FRIEND);

		// friends.clear();
		// friends = getAllFriends();

		Log.v(TAG, "mylocaldb.syncfriends Sync finished." + friends.size());
		return new Report(true, "Friends Synced");
	}

	public Report storeFriends(List<MyFriend> friends) {
		int count = 0;
		for (MyFriend friend : friends) {
			if (storeFriend(friend).isSuccess == true) {
				count++;
			}
		}

		if (count == friends.size()) {
			return new Report(true, count + " friends stored.");
		} else {
			return new Report(false, "Only " + count + " friends stored.");
		}
	}

	public Report storeFriend(MyFriend friend) {
		Cursor friendResults = localDB.query(false, TABLE_FRIEND, new String[] {
				KEY_ID, KEY_FBID }, KEY_FBID + "=" + friend.getFbID(), null,
				null, null, null, null);
		Report report = null;
		// If friend doesn't exist, insert it
		if ((friendResults.getCount() == 0) || !friendResults.moveToFirst()) {
			report = insertFriend(friend);
			return report;
		}
		// If friend exists, update it
		else {
			long rowId = friendResults.getLong(ID_COLUMN);
			String message = friend.getBdayMessage();
			boolean isAutoPost = friend.isAutoPost();
			report = saveMessage(rowId, message, isAutoPost);
			// report = updateFriend(rowId, friend);
			return report;
		}
	}

	// Insert friend in database

	public Report insertFriend(MyFriend friend) {
		return insertFriend(friend, TABLE_FRIEND);
	}

	public Report insertFriend(MyFriend friend, String tableName) {
		ContentValues newFriend = new ContentValues();
		newFriend.put(KEY_FBID, friend.getFbID());
		newFriend.put(KEY_NAME, friend.getName());
		newFriend.put(KEY_BIRTHDAY, friend.getBday());
		newFriend.put(KEY_PIC, friend.getPic());
		newFriend.put(KEY_BDAYMESSAGE, friend.getBdayMessage());
		boolean isAutoPost = friend.isAutoPost();
		if (isAutoPost) {
			newFriend.put(KEY_AUTOPOST, "true");
		} else
			newFriend.put(KEY_AUTOPOST, "false");

		try {
			localDB.insert(tableName, null, newFriend);
		} catch (SQLiteException ex) {
			return new Report(false, ex.getMessage());
		}

		return new Report(true, "Friend inserted successfully!");
	}

	// Update friend in database
	public Report updateFriend(long rowId, MyFriend friend) {

		ContentValues updatedFriend = new ContentValues();

		updatedFriend.put(KEY_FBID, friend.getFbID());
		updatedFriend.put(KEY_NAME, friend.getName());
		updatedFriend.put(KEY_BIRTHDAY, friend.getBday());
		updatedFriend.put(KEY_PIC, friend.getPic());
		updatedFriend.put(KEY_BDAYMESSAGE, friend.getBdayMessage());
		boolean isAutoPost = friend.isAutoPost();
		if (isAutoPost) {
			updatedFriend.put(KEY_AUTOPOST, "true");
		} else
			updatedFriend.put(KEY_AUTOPOST, "false");

		try {
			String where = KEY_ID + "=" + rowId;
			localDB.update(TABLE_FRIEND, updatedFriend, where, null);
		} catch (SQLiteException ex) {
			return new Report(false, ex.getMessage());
		}
		return new Report(true, "Friend is updated successfully!");
	}

	public Report saveMessage(long rowId, String message, boolean isAutoPost) {

		ContentValues updatedFriend = new ContentValues();
		updatedFriend.put(KEY_BDAYMESSAGE, message);

		if (isAutoPost) {
			updatedFriend.put(KEY_AUTOPOST, "true");
		} else
			updatedFriend.put(KEY_AUTOPOST, "false");

		try {
			String where = KEY_ID + "=" + rowId;
			localDB.update(TABLE_FRIEND, updatedFriend, where, null);
		} catch (SQLiteException ex) {
			return new Report(false, ex.getMessage());
		}
		return new Report(true, "Friend is updated successfully!");
	}

	public Report saveMessageByFbID(String ID, String message,
			boolean isAutoPost) {

		ContentValues updatedFriend = new ContentValues();
		updatedFriend.put(KEY_BDAYMESSAGE, message);
		updatedFriend.put(KEY_AUTOPOST, isAutoPost ? "true" : "false");

		try {
			String where = KEY_FBID + " = ?";
			localDB.update(TABLE_FRIEND, updatedFriend, where,
					new String[] { ID });
		} catch (SQLiteException ex) {
			return new Report(false, ex.getMessage());
		}
		return new Report(true, "Friend is updated successfully!");
	}

	public Report removeFriend(long rowId) {

		String where = KEY_ID + "=" + rowId;
		try {
			localDB.delete(TABLE_FRIEND, where, null);
		} catch (SQLiteException ex) {
			return new Report(false, ex.getMessage());
		}
		return new Report(false, "Friend is removed successfully!");
	}

	public void close() {
		localDB.close();
	}
}
