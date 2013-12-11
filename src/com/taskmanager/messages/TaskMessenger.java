package com.taskmanager.messages;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class TaskMessenger {
	
	private static final String TAG = "Messenger";
	
	private String textMessage = "";
	private String phoneNumber;
	private Context context;
	
	public TaskMessenger(Context currentContext, String newPhoneNumber){
		phoneNumber = newPhoneNumber;
		context = currentContext;		
	}
	
	public void setMessage(String newMessage){
		textMessage = newMessage;		
	}
	
	public String getMessage() {
		return textMessage;
	}
	
	
	public boolean isMessageValid(){
		if(textMessage.length() > 0){
			return true;
		}
		return false;
		
	}
	
	public void sendSMS()
    {
		
		
		Log.d(TAG, textMessage);
		
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        Intent SENT_INTENT = new Intent(SENT);
        Intent DELIVERED_INTENT = new Intent(DELIVERED);
        
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
        		SENT_INTENT, 0);
        
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
            DELIVERED_INTENT, 0);
 
        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {            	
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                    	ContentValues values = new ContentValues();
                        values.put("address", phoneNumber);
                        values.put("body", textMessage);
                        context.getContentResolver().insert( Uri.parse("content://sms/sent"), values );
                        Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, textMessage, sentPI, deliveredPI);        
    }
}
