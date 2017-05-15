package com.example.fitnessbuddy;

//		-	Download core library for AndroidPlot 0.6.1 at http://androidplot.com/download/
//		-	Treemap returns data in DESCENDING order -- newest to oldest days
//			-	Insert data into list via iterator and counter
//			-	Add counter to iterator when getting out of treemap
//			-	hasNext() && counter < countMAX
//			-	count_Max = 7 initially but can be set to any number
//					-	depends on what the user CLICKS after first chart
//			-	Then reverse the list so that earliest day is in position 0
//					-	Collections.reverse(Arrays.asList(steps));
//			-	Divide STEPS by 1000 for Y-Axis values


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;


public class StepChart extends Activity implements OnClickListener/*implements AsyncTaskResponse*/ {

	//private Button setdays;
	public Dialog dialog;
	
/**** SET VARIABLE NAMES ****/
	private XYPlot plot;
	//String userEmail = "test@test";
	String userEmail = GlobalUserProfile.user_email;
	//String userEmail = GlobalUserInfo.getEmail();		//	get email from GlobalUserInfo
	TreeMap<String, Integer> resultMap = null;
	int count = 0;										//	count-1 = actual number of data points
	int max_Count = 0;									//	maximum number of data points

	  private EditText textToEnter;
@Override
public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	
	// snippet that prevents users from taking screenshots on ICS+ devices
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                           WindowManager.LayoutParams.FLAG_SECURE);	
    
	//	Get data for charts
		//Log.d("Starting", "Calling Async Task");
		setMaxCount(7);												//	set number of days in the chart
		new getStepChart().execute();								//	async task
	//	Log.d("Finished", "Return from Async Task");
		
		dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("Generate Charts");

       textToEnter=(EditText)dialog.findViewById(R.id.dayset);
        Button set=(Button)dialog.findViewById(R.id.set);
        Button cancel=(Button)dialog.findViewById(R.id.cancel);
        dialog.show();
     
        
        set.setOnClickListener(this);
        cancel.setOnClickListener(this);

}	// END onCreate


/*
//	Go back to tracking page
@Override
public void onBackPressed(){
super.onBackPressed(); 
startActivity(new Intent(ThisActivity.this, NextActivity.class));		//	NEED TO FILL THIS IN
finish();
}*/


//	Async Task - database query and create charts
private class getStepChart extends AsyncTask <Void, TreeMap<String, Integer>, TreeMap<String, Integer>> {
//private class getStepChart extends AsyncTask <Void, Void, Void> {
	
	
	/*  onPreExecute();	 Not Used	*/
	

	@Override
	protected TreeMap<String, Integer> doInBackground (Void... params) {
	//protected Void doInBackground (Void... params) {
		
		TreeMap<String, Integer> tempMap = null;
		
		try {
			tempMap = DatabaseQueries.getStepData(userEmail);
				//resultMap = null;	//	test the TOAST
				//Log.d("tempMap", "Successfully retrieved with " + tempMap.size() + " items");
			setResultMap(tempMap);

		} 
		
			catch (IllegalStateException e) {
				//Log.d("tempMap", "Illegal State Exception");
				e.printStackTrace();
			} catch (IOException e) {
			//	Log.d("tempMap", "IOException");
				e.printStackTrace();
			} catch (JSONException e) {
				//Log.d("tempMap", "JSON Exception");
				e.printStackTrace();
			}
				
		return tempMap;
		//return null;
		
	}


	/*  onProgressUpdate();	 Not Used	*/


	@Override
	protected void onPostExecute(TreeMap<String, Integer> result) {
	//protected void onPostExecute(Void result) {

		//	If query returns results for the user, make chart
		if (result != null) {
				
				//Log.d("Results of Async Task", "size is " + result.size());
				//Log.d("Post Execute", "tempMap is not NULL, about to make chart");
				makeChart(result);
				//Log.d("Post Execute", "tempMap is not NULL, finished making chart");
				
				//	If # data points less than max # days for chart
				if (count < max_Count) {

					Context context = getApplicationContext();
					CharSequence text = "Fewer than " + max_Count + "data points available";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				
				}
				
		}

		//	Else, if something is wrong withDB or no results, message to user
		else {
			
			Context context = getApplicationContext();
			CharSequence text = "No Results Stored!";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
				//Toast.makeText(context, text, duration).show();
		}
		
		//Log.d("post Execute", "out of the function");
		//testResultMap();
		
	}

}	//	END OF AsyncTask Class



//	Need to customize
public void makeChart(TreeMap<String, Integer> theMap) {

	setContentView(R.layout.activity_step_chart);					//	name of .xml file
	plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);				//	Name of plot in .xml file

	//	Get dates from TreeMap and put into array (not used - just placeholder)			
	//	Get step numbers from TreeMap and put into list			
			ArrayList<String> days = new ArrayList<String>();
			List<Number> steps = new ArrayList<Number>();
				//Log.d("Days List", "size " + days.size());
				//Log.d("Steps List", "size " + steps.size());

			Iterator iterator = theMap.entrySet().iterator();  
			count = 0;
				  					
			while (iterator.hasNext() && count < max_Count) {
				Map.Entry mapEntry = (Map.Entry) iterator.next();  		
				days.add((String) mapEntry.getKey());
				Integer tempSteps = (Integer) mapEntry.getValue()/1000;	// so 10,000 steps on axis is 10
				steps.add((Number) tempSteps);
					//Log.d("WHILE loop:  ", "counter is " + count);
					//Log.d("adding steps", "value " + steps.get(count));
				count++; 			
			}

				//Log.d("Exited WHILE loop ", "counter is " + count);
			
			Collections.reverse(steps);
			Collections.reverse(days);
				    
	//	Create series from list:  y-axis, increments on x-axis, title of series (left blank)
			XYSeries series1 = new SimpleXYSeries(steps, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"");
			
			
	// 	Create a formatter to use for drawing a series using LineAndPointRenderer and configure it from xml
	        LineAndPointFormatter series1Format = new LineAndPointFormatter();
	        series1Format.setPointLabelFormatter(new PointLabelFormatter());
	        series1Format.configure(getApplicationContext(),
	               R.xml.line_point_formatter_with_plf1);

	 //	Remove legends and labels
	        plot.getLayoutManager().remove(plot.getLegendWidget());
	        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
	        //plot.getLayoutManager().remove(plot.getRangeLabelWidget());
	        //plot.getLayoutManager().remove(plot.getTitleWidget());
	        
	//	Additional domain formatting
	        plot.setDomainStep(XYStepMode.SUBDIVIDE, steps.size());		//	one tick per day
	        //plot.getGraphWidget().setDomainLabelOrientation(-45);		//	leaving this out
	        
	//	Additional range formatting
	        //plot.setTicksPerRangeLabel(2);						//	reduce the number of range labels - every 2(000)
	        														//		- currently in the xml
	        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);		//	start at 0
	        
	//	Border
	        plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
	        plot.setPlotMargins(0, 0, 0, 0);
	        plot.setPlotPadding(0, 0, 0, 0);
	        plot.setGridPadding(0, 10, 5, 0);
	        
	       /*plot.getGraphWidget().position(
	                0, XLayoutStyle.ABSOLUTE_FROM_LEFT,
	                0.5f, YLayoutStyle.RELATIVE_TO_CENTER,
	                AnchorPosition.LEFT_MIDDLE);*/

	//	Fill in the graph/transparency
	        Paint lineFill = new Paint();
	        lineFill.setAlpha(200);
	        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
	        series1Format.setFillPaint(lineFill);
	        	        
	//	Add a new series to the xyplot
	        plot.addSeries(series1, series1Format);
	        plot.redraw();
	        
	}	// END of makeChart() function
    

	//	Sets the time period for the chart
	void setMaxCount (int i) {
		max_Count = i;	
	}


	void setResultMap (TreeMap<String, Integer> result) {
		//Log.d("setResultMap", "here i am");
		resultMap = result;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.set)
		{
			
		    
			// textToEnter = (EditText) findViewById(R.id.dayset);
			
			
		   String newValue = textToEnter.getText().toString(); 
		   
			//transform editText to string
		   int newMax = Integer.parseInt(newValue);
		   setMaxCount(newMax);
		   new getStepChart().execute();	
		  // System.out.println("ice cubes " + newValue);
		   closeDialog();
			//String stringEditText = newValue;
			//System.out.println("leggo my eggo " + stringEditText);
			//transform string to and int
		/*	int numOfDays = Integer.parseInt(stringEditText);
			System.out.println("leggo my eggo 222 " + numOfDays);
			max_Count = numOfDays;
			System.out.println("leggo my eggo 33333" + max_Count); */
		}
		
		else if(v.getId() == R.id.cancel)
		{
			Intent intent = new Intent(this, TrackDailyUser.class);
			startActivity(intent);
			finish();
		}
	}


	private void closeDialog() {
		// TODO Auto-generated method stub
		dialog.dismiss();
	}
	

	
	/*void testResultMap(){
		Log.d("Testing result map", "size is " + resultMap.size());
	}*/
	
}	//	END OF CLASS