package com.example.fitnessbuddy;

/*	TAKEN FROM 
http://www.coderzheaven.com/2011/12/26/show-data-in-columns-in-a-tableview-dynamically-in-android/
http://wowjava.wordpress.com/2011/04/01/dynamic-tablelayout-in-android/
http://blog.stylingandroid.com/archives/432
http://technotzz.wordpress.com/2011/11/04/android-dynamically-add-rows-to-table-layout/
http://sdroid.blogspot.com/2011/01/fixed-header-in-tablelayout.html
http://www.entreotrascosas.net/creating-dynamic-scrollable-fixed-header-tables-in-android/
http://www.it-tweaks.com/2013/10/16/android-fixed-header-and-add-table-rows-dynamically/
*/
/*AndroidPlot api must be downloaded and added to buildpath*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class Create_User_History extends Activity {
		
	/*********	CLASS VARIABLES  *********/
	TableLayout tl_header;
    TableLayout tl;	//	table layout for data
    TableRow tr;
	TextView dateTV, stepsTV, distanceTV, caloriesTV, timeTV;	//	the columns in each row

	String userEmail = GlobalUserProfile.user_email;
	//String userEmail = GlobalUserInfo.getEmail();				//	get email from GlobalUserInfo	
	TreeMap<String, ArrayList<String>> resultMap = null;


	
	/*********	METHODS  *********/
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__user__history);					//	name of .xml file
//        tl_header = (TableLayout) findViewById(R.id.headertable);	//	name of header table in the table layout
        tl = (TableLayout) findViewById(R.id.historytable);			//	name of data table in the table layout
        new historyRetrieve().execute();							//	execute the async task
        Log.d("Finished", "Return from Async Task");
    }

	//	Go back to tracking page
	/*@Override
	public void onBackPressed(){
		super.onBackPressed(); 
    	startActivity(new Intent(ThisActivity.this, NextActivity.class));		//	NEED TO FILL THIS IN
    	finish();
	}*/


	//	asyncTask ==> get data and populate table
	//	<params passed to the task, progress passed to "onProgressUpdate", output - what returns "doInBackground()">	
	private class historyRetrieve extends AsyncTask <Void, Void, Void> {
	
			/*  onPreExecute(Void... params);	 Not Used	*/


			@Override
			protected Void doInBackground (Void... params) {

				try {
					resultMap = DatabaseQueries.getHistoryPage(userEmail);
					//resultMap = null;
					Log.d("resultMap", "Successfully retrieved");
				} 
				
					catch (IllegalStateException e) {
						Log.d("resultMap", "Illegal State");
						e.printStackTrace();
						}	 
					catch (IOException e) {
						Log.d("resultMap", "IO Exception");
						e.printStackTrace();
					}
					
				return null;	
					
			}

			/*	onProgressUpdate(); 	Not Used	*/
			
			@Override
			protected void onPostExecute(Void v) {
				
			//	If query returns results for the user, dynamically add data rows to table
			//		-	Header rows already set in the chart
				if (resultMap != null) {
					Log.d("Add Data", "resultMap is not NULL, about to add DATA");
					addData();
					Log.d("Add Data", "resultMap is not NULL, finished adding DATA");
				}
				
			//	Else, message to user if something is wrong with DB or no results
				else { 
					Context context = getApplicationContext();
					CharSequence text = "No Results Stored!";
					int duration = Toast.LENGTH_SHORT;

					Toast.makeText(context, text, duration).show();
						/*Toast toast = Toast.makeText(context, text, duration);
						toast.show();*/					
				}
				
				//return null;
				
			}


	}	//	END OF AsyncTask Class
	
    
 
    /** This function add the data to the table up to 365 days **/
    public void addData() {
 
    	Log.d("addData", "entering the function");
    	
 			Set<Entry<String, ArrayList<String>>> set = resultMap.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iterator = set.iterator();
			//ArrayList<String> array = null;  
			int counter = 0;
			Log.d("addData", "created variables");
			
  			while (iterator.hasNext() && counter < 366) {
  
  				Log.d("addData", "while loop " + counter);
 
  				/**	A.	Set up to get the values from TreeMap returned from query **/
					
					//	1.	Get the next value in the TreeMap (already sorted by key in descending order)
  					Map.Entry mentry = (Map.Entry)iterator.next();

					//	2.	Fix date string so that format is "MM/DD"
					//		-	since maximum size of chart is 365 days, we don't need year
					String keyDate = (String) mentry.getKey();
					StringBuffer sbuffer = new StringBuffer(keyDate);
					sbuffer.delete(0, 2);
					sbuffer.insert(2,  "/");
					keyDate = sbuffer.toString();
			    	Log.d("addData", keyDate);

			
					//	3.	Put the key values into the array
					//		-	array positions:  steps[0], time[1], distance[2], speed[3], calories[4], goal met[5]
					ArrayList<String> array = (ArrayList<String>) mentry.getValue();
					Log.d("Checking array ", "size is " + array.size());
					Log.d("Checking array - position 0:  ", array.get(0));	

  			
	            /** B.	Create a TableRow dynamically **/
    	        tr = new TableRow(this);
        	    tr.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, 1));       	    
        	    Log.d("addData", "created new table row " + counter);
 
        	    
            	/** 1.	Creating a TextView to add DATE to the row **/
				dateTV = new TextView(this);
				 dateTV.setGravity(Gravity.CENTER);
				Log.d("1", "new textview");				
		        dateTV.setText(keyDate /*DATE*/);
        	    Log.d("2", "set text");
        	    if (array.get(5).equals("T")) {dateTV.setTextColor(Color.GREEN);}
        	    	else {dateTV.setTextColor(Color.RED);}
				Log.d("3", "color");				
         	   	dateTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
				Log.d("4", "typeface");				
            	dateTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 0.2f));
				Log.d("5", "layout params");				
            	//dateTV.setPadding(5, 5, 5, 5);
            	tr.addView(dateTV);  // Adding textView to tablerow.
        	    Log.d("addData", "Added DATE TextView " + counter);
        	    
            	
            	/** 2.	Creating a TextView to add STEPS to the row **/
            	stepsTV = new TextView(this);
            	stepsTV.setGravity(Gravity.CENTER);
				stepsTV.setText(array.get(0) /*STEPS*/);
				if (array.get(5).equals("T") ) {stepsTV.setTextColor(Color.GREEN);}
        	    	else {stepsTV.setTextColor(Color.RED);}
 				stepsTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 0.2f));
            	//stepsTV.setPadding(5, 5, 5, 5);
            	stepsTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            	tr.addView(stepsTV); // Adding textView to tablerow.
            	tr.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        	    Log.d("addData", "Added STEPS TextView " + counter);
        	    
            	
            	/** 3.	Creating a TextView to add DISTANCE to the row **/
            	distanceTV = new TextView(this);
            	distanceTV.setGravity(Gravity.CENTER);
            	distanceTV.setText(array.get(2) /*DISTANCE*/);
            	if (array.get(5).equals("T") ) {distanceTV.setTextColor(Color.GREEN);}
        	    	else {distanceTV.setTextColor(Color.RED);}
            	distanceTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 0.2f));
           		//distanceTV.setPadding(5, 5, 5, 5);
            	distanceTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            	tr.addView(distanceTV); // Adding textView to tablerow.
            	Log.d("addData", "Added DIST TextView " + counter);
 
            	
				/** 4.	Creating a TextView to add CALORIES the row **/
    	        caloriesTV = new TextView(this);
    	        caloriesTV.setGravity(Gravity.CENTER);
        	    caloriesTV.setText(array.get(4) /*CALORIES*/);
        	    if (array.get(5).equals("T") ) {caloriesTV.setTextColor(Color.GREEN);}
        	    	else {caloriesTV.setTextColor(Color.RED);}
            	caloriesTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 0.2f));
            	//caloriesTV.setPadding(5, 5, 5, 5);
            	caloriesTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            	tr.addView(caloriesTV); // Adding textView to tablerow.
            	Log.d("addData", "Added CALS TextView " + counter);
            	
            	
            	/** 5.	Creating a TextView to add TIME to the row **/
    	        timeTV = new TextView(this);
    	        timeTV.setGravity(Gravity.CENTER);
        	    timeTV.setText(array.get(1) /*TIME*/);
        	    if (array.get(5).equals("T") ) {timeTV.setTextColor(Color.GREEN);}
        	    	else {timeTV.setTextColor(Color.RED);}
            	timeTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 0.2f));
            	//timeTV.setPadding(5, 5, 5, 5);
            	timeTV.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            	tr.addView(timeTV); // Adding textView to tablerow.
        	    Log.d("addData", "Added TIME TextView " + counter);
  
        	    
       		 	/** C.	Add the TableRow of ALL DATA to the TableLayout **/
				tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT, 1));
                counter++;                
        	    Log.d("addData", "Added Table Row " + counter--);


        }	// end WHILE        
        
  	    Log.d("addData", "exiting the function");	
  			
    }	//	End addData()	
}