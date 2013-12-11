package com.taskmanager.activities;

import com.taskmanager.database.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewTaskList extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_list);
		
		Button buttonOne = (Button) findViewById(R.id.list_save);
		buttonOne.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    	addList();
		    }
		});
	}
	
	
	public void addList() {
		EditText text = (EditText)findViewById(R.id.list_name);
		
		
		TaskList newList = new TaskList();
		newList.setName(text.getText().toString());
		newList.save();
		
		finish();
	}
	
	
}
