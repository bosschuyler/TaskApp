package com.taskmanager.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskListItem {

	private static String tableName = "tbl_list_items";
	
	private String list_item_name;
	private Integer list_id;
	private Integer _id;
		
	public TaskListItem() {}
	
	public static ArrayList<TaskListItem> getAll() {
		SQLiteDatabase db = dbList.openDB();
		ArrayList<TaskListItem> allLists = new ArrayList<TaskListItem>();
		
		Cursor c = db.rawQuery("SELECT * FROM "+tableName, null);
		if(c.getCount() > 0) {
			c.moveToFirst();
			do {
				TaskListItem tempList = new TaskListItem();
				tempList.setName(c.getString(c.getColumnIndex("list_item_name")));
				tempList.setID(c.getInt(0));
				tempList.setListID(c.getInt(1));
				
				allLists.add(tempList);
			}while(c.moveToNext());	
		}
		return allLists;
	}
	
	public static ArrayList<TaskListItem> getItemsByListID(Integer ListID) {
		SQLiteDatabase db = dbList.openDB();
		ArrayList<TaskListItem> taskItems = new ArrayList<TaskListItem>();
		Cursor c = db.rawQuery("SELECT * FROM "+tableName+" WHERE list_id='"+ListID+"'", null);
		if(c.getCount() > 0) {
			c.moveToFirst();
			do {
				TaskListItem tempList = new TaskListItem();
				tempList.setName(c.getString(c.getColumnIndex("list_item_name")));
				tempList.setID(c.getInt(0));
				tempList.setListID(c.getInt(1));			
				taskItems.add(tempList);
			}while(c.moveToNext());			
		}
		
		return taskItems;		
	}
	
	public static ArrayList<TaskListItem> getItemsByName(Integer ListID, String Name) {
		SQLiteDatabase db = dbList.openDB();
		ArrayList<TaskListItem> taskItems = new ArrayList<TaskListItem>();
		Cursor c = db.rawQuery("SELECT * FROM "+tableName+" WHERE list_id='"+ListID+"' AND list_item_name = '"+Name+"'", null);
		if(c.getCount() > 0) {
			c.moveToFirst();
			do {
				TaskListItem tempList = new TaskListItem();
				tempList.setName(c.getString(c.getColumnIndex("list_item_name")));
				tempList.setID(c.getInt(0));
				tempList.setListID(c.getInt(1));			
				taskItems.add(tempList);
			}while(c.moveToNext());		
		}
		
		return taskItems;		
	}
	
	public void setListID(Integer ListID) {
		list_id = ListID;
	}
	
	public void setID(Integer ID) {
		// TODO Auto-generated method stub
		_id = ID;
	}

	public static TaskListItem getListByID(Integer ID) {
		TaskListItem tempList = null;
		return tempList;
	}
	
	public void setName(String name) {
		list_item_name = name;
		
	}
	
	public String getName() {
		return list_item_name;
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
	
	public void save() {
		SQLiteDatabase db = dbList.openDB();
		
		ContentValues listData = new ContentValues();
		listData.put("list_item_name", list_item_name);
		listData.put("list_id", list_id);
		
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
