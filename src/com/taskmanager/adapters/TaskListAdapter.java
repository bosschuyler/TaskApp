package com.taskmanager.adapters;

import com.taskmanager.database.*;
import com.taskmanager.activities.*;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TaskListAdapter extends ArrayAdapter<TaskList>{

	private List<TaskList> Lists;
	private Context context;
	
	public TaskListAdapter(List<TaskList> objects, Context context) {
		super(context, R.layout.list_view, objects);
		// TODO Auto-generated constructor stub
		this.Lists = objects;
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
	     
	    // First let's verify the convertView is not null
	    if (convertView == null) {
	        // This a new view we inflate the new layout
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.list_view, parent, false);
	    }
	    
        // Now we can fill the layout with the right values
        TextView listName = (TextView) convertView.findViewById(R.id.listName);
        Button deleteList = (Button) convertView.findViewById(R.id.deleteList);
        TaskList tempList = Lists.get(position);
 
        listName.setText(tempList.getName());        
        
        
        deleteList.setOnClickListener(new deleteListener(position));
	    
	    return convertView;
	}
	
	@SuppressLint("ShowToast")
	public void deleteItem(int itemNumber) {
		TaskList tempList = Lists.get(itemNumber);
		tempList.delete();
		Lists.remove(itemNumber);					
		Toast temp = Toast.makeText(this.getContext(),"Deleting: "+tempList.getName(), Toast.LENGTH_SHORT);
		temp.show();
		this.notifyDataSetChanged();	
	}	
	
	public class deleteListener implements View.OnClickListener {
		private int rowNumber;		
		
		public deleteListener(int position) {
			rowNumber = position;
		}
		
		@Override
		public void onClick(View v) {
			deleteItem(rowNumber);
		}	
	}	
}
