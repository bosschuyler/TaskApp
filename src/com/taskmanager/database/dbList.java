package com.taskmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbList extends SQLiteOpenHelper {
	
	static final String DB_NAME = "db_list.db";
	
	static final String LIST_TABLE = "tbl_list";	
	static final String LIST_ITEM_TABLE = "tbl_list_items";
	
	private static dbList dbHelper;
	
	private static SQLiteDatabase db;
	
	public dbList(Context context) {
		super(context, DB_NAME, null, 1);
	}
	
	public static SQLiteDatabase openDB() {
		if(db == null) {
			db = dbHelper.getWritableDatabase();
		}
		return db;
	}
	
	public static void init(Context context) {
		dbHelper = new dbList(context);
	}
		
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+LIST_TABLE+" (_id INTEGER PRIMARY KEY, list_name TEXT)");
		db.execSQL("CREATE TABLE "+LIST_ITEM_TABLE+" (_id INTEGER PRIMARY KEY, list_id INTEGER, list_item_name TEXT)");
	}
	
	public void deleteDB() {
		SQLiteDatabase db = this.getWritableDatabase();	
		db.execSQL("DROP TABLE IF EXISTS "+LIST_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+LIST_ITEM_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+LIST_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+LIST_ITEM_TABLE);
		
		onCreate(db);
		
	}
	
}
