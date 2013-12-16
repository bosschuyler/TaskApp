package com.taskmanager.activities;

import java.util.ArrayList;
import java.util.Locale;

import com.taskmanager.adapters.TaskItemListAdapter;
import com.taskmanager.database.TaskList;
import com.taskmanager.database.TaskListItem;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayTaskItemList extends Activity {

	//TaskListItems array containing all the strings.
	public static ArrayList<TaskListItem> ItemList = new ArrayList<TaskListItem>();
	public static TaskItemListAdapter adapter;
	
	private static int listID;
	private static int VOICE_RECOGNITION = 1000;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_items);
		
		adapter = new TaskItemListAdapter(ItemList, this.getApplicationContext());
		ListView taskItems = (ListView) this.findViewById(R.id.list);
		
		taskItems.setAdapter(adapter);
		
		Bundle extras = getIntent().getExtras(); 
		listID = extras.getInt("list_id");
		
		TaskList tasklist = TaskList.find(listID);
		
		String listName = tasklist.getName();
		
		listName = listName.substring(0,1).toUpperCase(Locale.US) + listName.substring(1);
		
		TextView title = (TextView) this.findViewById(R.id.list_title);
		title.setText(listName);	
		
		loadList();
		
		Toast t = Toast.makeText(getApplicationContext(),
                "this is the listID: "+listID,
                Toast.LENGTH_SHORT);
        t.show();
	}
	
	public static void addToList() {
		ItemList.clear();
		ArrayList<TaskListItem> Lists = TaskListItem.getItemsByListID(listID);
		
		for(int i=0;i<Lists.size();i++)
		{
			TaskListItem tempList = Lists.get(i);
			ItemList.add(tempList);
		}
	
	}
	
	public void loadList() {
		addToList();
		adapter.notifyDataSetChanged();	
	}
	
	public void speak(View view) {
		Intent getSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	
		getSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		 
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
            	ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                
                String speechString = text.get(0);
                                
                Toast t = Toast.makeText(getApplicationContext(),
                		speechString,
                        Toast.LENGTH_SHORT);
                t.show();
                
                String[] parts = speechString.split("add");
                
                for(int i=0;i<parts.length;i++)
        		{
                	parts[i] = parts[i].trim();
                	
                	if(parts[i] != null && parts[i].length() != 0) {
                		TaskListItem tempItem = new TaskListItem();
                    	
                    	tempItem.setName(parts[i]);
                        tempItem.setListID(listID);
                        tempItem.save();
            		
                        ItemList.add(tempItem);                	
                	}                	
        		}
                
                //TaskListItems.add();
                adapter.notifyDataSetChanged();
            }   		 
	       
		}	
	}	
}
