package com.taskmanager.activities;

import com.taskmanager.adapters.TaskItemListAdapter;
import com.taskmanager.adapters.TaskListAdapter;
import com.taskmanager.database.*;
import com.taskmanager.messages.TaskContact;
import com.taskmanager.messages.TaskMessenger;
import com.taskmanager.speech.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

public class DisplayTaskLists extends Activity{
	
	private static int VOICE_RECOGNITION = 1000;
	private static int ADD_LIST = 20;
	
	private static String TAG = "Taskmanager";
	
	private boolean isListening = false;
	
	private static String COMMAND_REGEX = "open|close|add|add to list|delete list|delete item|create list|close";
	
	private boolean isPendingMessage = false;
	private boolean messageStarted = false;
	private TaskMessenger pendingMessage;
	
	private TaskSpeech taskSpeech;
	
	private TaskList openList;
	private PopupWindow popupWindow;	
	private PopupWindow menu;
	
	//TaskListItems array containing all the strings.
	ArrayList<TaskList> TaskListItems = new ArrayList<TaskList>();
	ArrayList<TaskListItem> ItemList = new ArrayList<TaskListItem>();
	
	TaskListAdapter adapter;
	TaskItemListAdapter itemAdapter;
	
	private SpeechRecognizer taskSpeechRecognizer;
	private Intent taskRecognizerIntent;
		
	public void onPause() {
		super.onPause();
		Log.d(TAG, "Paused");
	}
	
	public void onDestroy() {
		Log.d(TAG, "Destroyed");
		VoiceHandler.getInstance().stopListening();
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbList.init(getApplicationContext());
		taskSpeech = new TaskSpeech(getApplicationContext());
		
		loadListView();
						
		Log.d(TAG, "speech recognition available: " + SpeechRecognizer.isRecognitionAvailable(getBaseContext()));
		VoiceHandler.getInstance().init(getApplicationContext());
	}

	
	public void loadListView() {
		setContentView(R.layout.activity_task);
		
		adapter = new TaskListAdapter(TaskListItems, this.getApplicationContext());
		ListView taskItems = (ListView) this.findViewById(R.id.list);
		
		taskItems.setOnItemClickListener(new OnItemClickListener() {
		   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
			   
			   openList = TaskListItems.get(position);
			   
			   openList(openList.getID());
			   /*
			    * Intent taskItems = new Intent(view.getContext(), DisplayTaskItemList.class);
			    * taskItems.putExtra("list_id", tempList.getID());
			   startActivity(taskItems);*/
		   } 
		});
		
		
		taskItems.setAdapter(adapter);
		loadList();
	}
	
	
	public void toggleRecognizer(View view) {
		VoiceHandler instance = VoiceHandler.getInstance();
		Toast alert = null;		
		Button toggleListening = (Button)this.findViewById(R.id.toggleListening);
		
		if(instance.isListening() == false) {
			alert = Toast.makeText(this, "I am listening now.", Toast.LENGTH_SHORT);
			toggleListening.setText("Start Listening");
			instance.startListening();
		} else {
			alert = Toast.makeText(this, "I am no longer listening.", Toast.LENGTH_SHORT);			
			toggleListening.setText("Stop Listening");
			instance.stopListening();
		}
		alert.show();
	}
	
	
	
	/**
	 * Grabs the lists in the db in the form of an array of objects.
	 * Loop the array and add the names to the listview.
	 * 
	 */	
	public void loadList() {
		TaskListItems.clear();		
		ArrayList<TaskList> Lists = TaskList.getAll();
		
		for(int i=0;i<Lists.size();i++)
		{
			TaskList tempList = Lists.get(i);
			TaskListItems.add(tempList);
		}
		adapter.notifyDataSetChanged();	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_task, menu);
		return true;
	}
	
	public void add_list(View view) {
		Intent addList = new Intent(this, NewTaskList.class);
		startActivityForResult(addList, ADD_LIST);
	}
	
	public void back(View view) {
		closeList();
	}
	
	public void speak(View view) {
		Intent getSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	
		getSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		 
        try {
        	startActivityForResult(getSpeech, VOICE_RECOGNITION);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }
	}
	
	public void onConfigurationChanged() {}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == VOICE_RECOGNITION) {
			
		
            if (resultCode == RESULT_OK && null != data) {
            	
            	
            	
            	TaskList tempList = new TaskList();
            	            	
                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                     
                tempList.setName(text.get(0));
                tempList.save();
                
                TaskListItems.add(tempList);
                
                //TaskListItems.add();
                adapter.notifyDataSetChanged();
                
                
                
                /*
                LayoutInflater inflater = (LayoutInflater)
            	this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                
                
                //create a popUp.
                PopupWindow voiceResults = new PopupWindow(inflater.inflate(R.layout.activity_task, null, false), 
          		       100, 
          		       100, 
          		       true);
               
                //create a linear layout object
                LinearLayout resultsLayout = new LinearLayout(this);
                
                //create a text element and set the content.
                TextView resultText = new TextView(this);
                resultText.setText(text.get(0));
                
                //add the text element to the linear layout.                
                resultsLayout.addView(resultText);
                
                //set the layout for the PopUp Window.
                voiceResults.setContentView(resultsLayout);
                
                //set where to put the popup.
                voiceResults.showAtLocation(this.findViewById(R.id.mainLayout), Gravity.BOTTOM, 10, 10);*/
                
                
            }   		 
	        
			
		}
		
		if(requestCode == ADD_LIST) {
			Toast t = Toast.makeText(this, "whoot", Toast.LENGTH_SHORT);
			t.show();
			loadList();
		
		}
		
	
	}
	
	
	public void addToList(String ItemName) {
		
	}
	
	public void openListByName(String listName) {
		openList = TaskList.getListByName(listName);
		
		Toast test = Toast.makeText(this, "List: "+openList.getName(), Toast.LENGTH_SHORT);
		test.show();
		
		if(openList.getID() != null) {
			openList(openList.getID());
		} else {
			Toast alert = Toast.makeText(this, "List was not found.", Toast.LENGTH_SHORT);
			alert.show();
		}
	}
	
	public void closeList() {
		loadListView();
	}
	
	
	public void openList(int listID) {
		/*Intent taskItems = new Intent(getBaseContext(), DisplayTaskItemList.class);		
		taskItems.putExtra("list_id", listID);
		startActivity(taskItems);*/
		setContentView(R.layout.activity_task_items);
		
		String listName = openList.getName();
		
		listName = listName.substring(0,1).toUpperCase(Locale.US) + listName.substring(1);
		
		TextView title = (TextView) this.findViewById(R.id.list_title);
		title.setText(listName);
				
		itemAdapter = new TaskItemListAdapter(ItemList, this.getApplicationContext());
				
		ListView taskItems = (ListView) this.findViewById(R.id.list);		
		taskItems.setAdapter(itemAdapter);
		
		loadItemList();
		
	}
	
	public void loadItemList() {
		ItemList.clear();
		ArrayList<TaskListItem> Lists = TaskListItem.getItemsByListID(openList.getID());
		
		for(int i=0;i<Lists.size();i++)
		{
			TaskListItem tempList = Lists.get(i);
			ItemList.add(tempList);
		}
		itemAdapter.notifyDataSetChanged();	
	}
	
	
	public void processCommand(String command, String commandOptions) {
		if(command.equals("open")){
			if(commandOptions != null && commandOptions.length() != 0){
				openListByName(commandOptions);
			}		
		}
		
		if(command.equals("close")){
			closeList();	
		}
		
		if(command.equalsIgnoreCase("delete list")){
			TaskList list = TaskList.getListByName(commandOptions);	
			if(list.getID() != null){
				list.delete();
				loadList();
				Toast alert = Toast.makeText(getBaseContext(), "Deleted: "+list.getName(), Toast.LENGTH_SHORT);
				alert.show();
			} else {
				Toast alert = Toast.makeText(getBaseContext(), "No lists by name: "+commandOptions, Toast.LENGTH_SHORT);
				alert.show();					
			}
		}
		
		if(command.equalsIgnoreCase("delete item")){
			if(openList != null){
				ArrayList<TaskListItem> items = TaskListItem.getItemsByName(openList.getID(), commandOptions);
				String deletedItemNames = "";
				if(items.size() > 0){
					for(int i=0; i<items.size(); i++){
						items.get(i).delete();
						if(items.size() > (i+1)){
							deletedItemNames += items.get(i).getName()+", ";
						} else {
							deletedItemNames += items.get(i).getName();
						}
					}	
					loadItemList();
					Toast alert = Toast.makeText(getBaseContext(), "Deleted: "+deletedItemNames, Toast.LENGTH_SHORT);
					alert.show();
				} else {
					Toast alert = Toast.makeText(getBaseContext(), "No items found in this list by name: "+commandOptions, Toast.LENGTH_SHORT);
					alert.show();					
				}
			} else {
				Toast alert = Toast.makeText(getBaseContext(), "No list is currently open", Toast.LENGTH_SHORT);
				alert.show();				
			}
		}
		
		if(command.equals("create list")) {
			TaskList tempList = new TaskList();
        	     
            tempList.setName(commandOptions);
            tempList.save();
            
            TaskListItems.add(tempList);
            
            //TaskListItems.add();
            adapter.notifyDataSetChanged();
		}
		
		if(command.equals("add")){
			if(openList != null){
				TaskListItem tempItem = new TaskListItem();
            	            	
            	tempItem.setName(commandOptions);
                tempItem.setListID(openList.getID());
                tempItem.save();
                
                openList(openList.getID());                
			} else {
				Toast alert = Toast.makeText(getBaseContext(), "No list is currently open", Toast.LENGTH_SHORT);
				alert.show();				
			}
		}
	}
	
	public void findCommands(String parseString) {
		
		if(parseString.matches("send (.*) a text")){
			String temp = "";
			Matcher matcher = Pattern.compile("(?<=send ).*?(?= a text)").matcher(parseString);
			if(matcher.find()){
				temp = matcher.group();
				
				Log.d(TAG, "Send what to: "+temp);				
								
				TaskContact contact = new TaskContact(getBaseContext(), temp.trim());
				String phoneNumber = "";
				
				if((phoneNumber = contact.getNumber()) != null) {
					pendingMessage = new TaskMessenger(getBaseContext(), phoneNumber);
					isPendingMessage = true;
					taskSpeech.speak("What would you like to send?");
					
					LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
					View popupView = layoutInflater.inflate(R.layout.text_message_popup, null);
				    
					TextView recipText = (TextView) popupView.findViewById(R.id.text_recipient);
					recipText.setText(contact.getName() + " :"+phoneNumber);
					
					popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
				    popupWindow.showAtLocation(this.findViewById(R.id.mainLayout), Gravity.BOTTOM, 10, 10);	
					
				    
				   
				    
					Toast alert = Toast.makeText(getBaseContext(), "What would you like to send?", Toast.LENGTH_SHORT);
					alert.show();	
				} else {
					taskSpeech.speak("No phone number found.");
				}				
			}			
		}
		
		if(isPendingMessage){
			
			EditText messageText = (EditText) popupWindow.getContentView().findViewById(R.id.text_message);
			
			if(messageStarted){
				String tempMessage = pendingMessage.getMessage();
				String addMessage = "";
				if(parseString.matches("stop the message")){
					Matcher matcher = Pattern.compile(".*(?= end message)").matcher(parseString);
					if(matcher.find()){
						addMessage = matcher.group();
					}
					messageStarted = false;
					
					Log.d(TAG, "message ended");
					
					Toast alert = Toast.makeText(getBaseContext(), "message ended", Toast.LENGTH_SHORT);
					alert.show();
				} else {
					addMessage = parseString;	

					Toast alert = Toast.makeText(getBaseContext(), "Adding to current message", Toast.LENGTH_SHORT);
					alert.show();
				}
				if(tempMessage.length() > 0){
					tempMessage += " ";
				}
				tempMessage += addMessage;		
				
				pendingMessage.setMessage(tempMessage);
				
				Log.d(TAG, "current message:" + tempMessage);
				
				messageText.setText(pendingMessage.getMessage());
				
			} else {
				if(parseString.matches("restart message")){
					pendingMessage.setMessage("");
					Toast alert = Toast.makeText(getBaseContext(), "message restarted", Toast.LENGTH_SHORT);
					alert.show();
					Log.d(TAG, "Message Reset");
					
					messageText.setText(pendingMessage.getMessage());
				} else 	if(parseString.matches("start message")){
					String temp = "";
					Matcher matcher = Pattern.compile("(?<=start message ).*").matcher(parseString);
					if(matcher.find()){
						temp = matcher.group();
						pendingMessage.setMessage(temp);
					}
					
					messageStarted = true;
					Toast alert = Toast.makeText(getBaseContext(), "starting message", Toast.LENGTH_SHORT);
					alert.show();
					Log.d(TAG, "starting message: "+temp);
					
					messageText.setText(pendingMessage.getMessage());
				} else if (pendingMessage.isMessageValid() && !messageStarted && parseString.matches("send message")){
					Log.d(TAG, "Sending Message");		
					Toast alert = Toast.makeText(getBaseContext(), "sending message", Toast.LENGTH_SHORT);
					alert.show();
					pendingMessage.sendSMS();			
					
					popupWindow.dismiss();
				} else {
					Log.d(TAG, "No command found");					
				}				
			} 
			
			
			
		}
		
		String[] pieces = parseString.split("(?=("+COMMAND_REGEX+"))");
		
		for(int i=0;i<pieces.length;i++)
		{	
			String temp = pieces[i].trim();
			
			if(!temp.equals("")){
				String command;
				Matcher matcher = Pattern.compile("(?i)("+COMMAND_REGEX+")").matcher(temp);
				if(matcher.find()){
					command = matcher.group();
					
					String[] subPieces = temp.split("(?i)"+COMMAND_REGEX);
					if(subPieces.length > 1) {
						String commandOptions = subPieces[1].trim();
											
						processCommand(command, commandOptions);
					} else {
						Log.d(TAG, "No command options given.");
						
						/*Toast alert = Toast.makeText(getBaseContext(), "I don't know what to do with this command" + temp, Toast.LENGTH_SHORT);
						alert.show();*/
					}
				} else {
					Log.d(TAG, "Command does not exist");
					
					/*Toast alert = Toast.makeText(getBaseContext(), "This is not a command: "+temp, Toast.LENGTH_SHORT);
					alert.show();*/
				}
			}
		}
		
		
		
		/*Matcher matcher = Pattern.compile("^(open|add)(?:(?!(open|add)).)*").matcher(parseString);
		
		while (matcher.find()) {
			Toast alert = Toast.makeText(getBaseContext(), matcher.group(), Toast.LENGTH_SHORT);
			alert.show();	
		}*/
	
		
	}
	
	
	
}
