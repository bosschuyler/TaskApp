package com.taskmanager.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskList {

	private static String tableName = "tbl_list";
	
	private String list_name;
	private Integer _id;
		
	public TaskList() {}	
	
	public static ArrayList<TaskList> getAll() {
		SQLiteDatabase db = dbList.openDB();
		ArrayList<TaskList> allLists = new ArrayList<TaskList>();
		
		Cursor c = db.rawQuery("SELECT * FROM "+tableName, null);
		if(c.getCount() > 0) {
			c.moveToFirst();
			do {
				TaskList tempList = new TaskList();
				tempList.setName(c.getString(c.getColumnIndex("list_name")));
				tempList.setID(c.getInt(0));
				
				allLists.add(tempList);
			}while(c.moveToNext());		
		}
		
		return allLists;
	}
	
	public void setID(Integer ID) {
		// TODO Auto-generated method stub
		_id = ID;
	}

	public static TaskList getListByID(Integer ID) {
		TaskList tempList = null;
		return tempList;
	}
	
	public void setName(String name) {
		list_name = name;
		
	}
	
	public String getName() {
		return list_name;	
	}
	
	public Integer getID() {
		return _id;
	}
	
	public boolean delete() {
		SQLiteDatabase db = dbList.openDB();
		if(db.delete(tableName, "_id = "+_id, null) > 0) {
			return true;
		} else {
			return false;		
		}
	}
	
	public static TaskList find(Integer listID) {
		SQLiteDatabase db = dbList.openDB();
		
		Cursor c = db.rawQuery("SELECT * FROM "+tableName+" WHERE _id="+listID+" LIMIT 1", null);
		
		TaskList tempList = new TaskList();
		
		if(c.getCount() > 0) {
			c.moveToFirst();
			tempList.setName(c.getString(c.getColumnIndex("list_name")));
			tempList.setID(c.getInt(0));
		}
		
		return tempList;	
	}
	
	public static TaskList getListByName(String listName) {
		SQLiteDatabase db = dbList.openDB();
		
		Cursor c = db.rawQuery("SELECT * FROM "+tableName+" WHERE list_name='"+listName+"' LIMIT 1", null);
		
		TaskList tempList = new TaskList();
		
		if(c.getCount() > 0) {
			c.moveToFirst();
			tempList.setName(c.getString(c.getColumnIndex("list_name")));
			tempList.setID(c.getInt(0));
		}
		
		return tempList;
	}
	
	
	public void save() {
		SQLiteDatabase db = dbList.openDB();
		
		ContentValues listData = new ContentValues();
		listData.put("list_name", list_name);
		
		if(_id == null) {
			_id = (int) db.insert(tableName, "_id", listData);
		} else {
			db.update(tableName, listData, "_id="+_id, null);
		}	
	}
	
	public static boolean emptyTable() {
		SQLiteDatabase db = dbList.openDB();
		if(db.delete(tableName, null, null) > 0) {
			return true;
		} else {
			return false;		
		}	
	}
	
	public boolean addList() {
		return true;
	}

}
