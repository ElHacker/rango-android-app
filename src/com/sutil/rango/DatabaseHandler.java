package com.sutil.rango;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHandler extends SQLiteOpenHelper{
	// All static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "callsLogManager";
	// Contacts table name
	private static final String TABLE_CALLS_LOG = "callsLog";
	
	// Contacts Table columns names
	private static final String KEY_ID = "id";
	private static final String COL_FB_ID = "fb_id";
	private static final String COL_FIRST_NAME = "first_name";
	private static final String COL_LAST_NAME = "last_name";
	private static final String COL_DATE = "date";
	private static final String COL_TIME = "time";
	
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Creating tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CALLS_LOG + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ COL_FB_ID + " TEXT,"
				+ COL_FIRST_NAME + " TEXT,"
				+ COL_LAST_NAME + " TEXT,"
				+ COL_DATE + " LONG,"
				+ COL_TIME + " LONG"
				+ ")";
		Log.d("SQL", CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE);
	}
	
	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLS_LOG);
		// Create tables again
		onCreate(db);
	}
	
	// Adding new contact
	void addCall(Call call) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		// Call fb_id
		values.put(COL_FB_ID, call.getFbId());
		// Call Name
		values.put(COL_FIRST_NAME, call.getFirstName());
		values.put(COL_LAST_NAME, call.getLastName());
		// Call Date
		values.put(COL_DATE, String.valueOf(call.getDate().getTime()));
		// Call Time
		values.put(COL_TIME, String.valueOf(call.getTime().getTime()));
		
		// Inserting Row
		db.insert(TABLE_CALLS_LOG, null, values);
		db.close();	// Closing database connection
	}
	
	// Get single call
	Call getCall(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CALLS_LOG, new String[] {
			KEY_ID,
			COL_FB_ID,
			COL_FIRST_NAME,
			COL_LAST_NAME,
			COL_DATE,
			COL_TIME
		},
		KEY_ID + "=?", new String[] { String.valueOf(id) }, 
		null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		} else {
			return null;
		}
		Call call = new Call(Integer.parseInt(cursor.getString(0)), 
				cursor.getString(1), cursor.getString(2), cursor.getString(3), 
				new Date(cursor.getLong(4)), new Time(cursor.getLong(5)));
		db.close();
		cursor.close();
		return call;
	}
	
	// Get all calls
	// Sorted by date and time in descending order
	public List<Call> getAllCalls() {
		List<Call> callList = new ArrayList<Call>();
		// Select all Query
		String selectQuery = "SELECT * FROM " + TABLE_CALLS_LOG + 
				" ORDER BY " + COL_DATE + " DESC, " + COL_TIME + " DESC";
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		// Looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Call call = new Call();
				
				call.setId(Integer.parseInt(cursor.getString(0)));
				call.setFbId(cursor.getString(1));
				call.setFirstName(cursor.getString(2));
				call.setLastName(cursor.getString(3));
				call.setDate(new Date(cursor.getLong(4)));
				call.setTime(new Time(cursor.getLong(5)));
				
				// Adding call to list
				callList.add(call);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return callList;
	}
	
	// Updating single call
	public int updateCall(Call call) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		// Call fb_id
		values.put(COL_FB_ID, call.getFbId());
		// Call Name
		values.put(COL_FIRST_NAME, call.getFirstName());
		values.put(COL_LAST_NAME, call.getLastName());
		// Call Date
		values.put(COL_DATE, String.valueOf(call.getDate().getTime()));
		// Call Time
		values.put(COL_TIME, String.valueOf(call.getTime().getTime()));
		
		// updating row
		return db.update(TABLE_CALLS_LOG, values, KEY_ID + "=?", new String[] {
				String.valueOf(call.getId())
		});
	}
	
	// Deleting single call
	public void deleteCall(Call call) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CALLS_LOG, KEY_ID + "=?", new String[] {
				String.valueOf(call.getId())
		});
		db.close();
	}
	
	// Getting calls Count
	public int getCallsCount() {
		String countQuery = "SELECT * FROM " +
				TABLE_CALLS_LOG;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		return cursor.getCount();
	}
}
