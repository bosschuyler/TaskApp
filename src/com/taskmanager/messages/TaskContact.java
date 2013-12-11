package com.taskmanager.messages;




import android.content.ContentResolver;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class TaskContact {
	
	private static final String TAG = "TASK_CONTACT";

	private Context context;
	
	private String contactName;
	private String contactNumber;
	
	private boolean hasPhoneNumber = false;
	
	public TaskContact(Context currentContext, String name){
		context = currentContext;
		contactName = name;
		
		contactNumber = getPhoneByPhoneticName(contactName);
		if(!contactNumber.equals("")){
			Log.d(TAG, "phonetic found");
			hasPhoneNumber = true;	
		} else {
			Log.d(TAG, "no phonetic found");
			
			contactNumber = getPhoneByDisplayName(contactName);
			if(!contactNumber.equals("")) {
				Log.d(TAG, "display found");
				hasPhoneNumber = true;
			} else {
				Log.d(TAG, "no display name found");
			}
		}
		
		Log.d(TAG, "Number: "+contactNumber);
	}
	
	
	
	public String getNumber() {
		if(hasPhoneNumber) {
			return contactNumber;
		}
		return null;
	}
	
	public String getName() {
		return contactName;
	}
	
	private String getPhoneByPhoneticName(String temp) {
	    ContentResolver contentResolver = context.getContentResolver();
	    Uri uri = Phone.CONTENT_URI;
	    String[] projection = new String[] { Phone.NUMBER, Phone.DISPLAY_NAME };
	    String selection = Phone.PHONETIC_NAME + " LIKE ? ";
	    String[] selectionArguments = { "%" + temp + "%" };
	    Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null);
	    if (cursor != null) {
	        while (cursor.moveToFirst()) {
	            return cursor.getString(0);
	        }
	    }
	    return "";
    }
	
	private String getPhoneByDisplayName(String temp) {
	    ContentResolver contentResolver = context.getContentResolver();
	    Uri uri = Phone.CONTENT_URI;
	    String[] projection = new String[] { Phone.NUMBER, Phone.DISPLAY_NAME };
	    String selection = Phone.DISPLAY_NAME + " LIKE ? ";
	    String[] selectionArguments = { "%" + temp + "%" };
	    Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null);
	    if (cursor != null) {
	        while (cursor.moveToFirst()) {
	            return cursor.getString(0);
	        }
	    }
	    return "";
    }
	
	
}
