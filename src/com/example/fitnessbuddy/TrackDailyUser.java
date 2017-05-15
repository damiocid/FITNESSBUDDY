package com.example.fitnessbuddy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TrackDailyUser extends Activity implements SensorEventListener, OnClickListener  
{
	String FILENAME = "myfile";
	String read_data = "empty";
	private int seconds = 0;
	private int minutes = 0;
	private int hours = 0;
	private int currentSec = 0;
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy"); 
	Calendar calendar;
	
   double userWeight = 150;
   
   private Button history, logout, update, generate, reset;
   
   private Handler theHandler;
   
   boolean activityRunning;
   int subtractor = 0;
   
	private TextView textView;
	private TextView stepCounter;
	private SensorManager sensorManager;
	
	DecimalFormat df = new DecimalFormat("#.##");
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_daily_user);
        
        theHandler = new Handler();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_daily_user);

        stepCounter = (TextView) findViewById(R.id.steps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        
        update = (Button) findViewById(R.id.updateProfile);
		update.setOnClickListener(this);
		
		 try {
			    FileInputStream fis = openFileInput(FILENAME);
			    byte[] dataArray = new byte[fis.available()];
			    while (fis.read(dataArray) != -1) {
			     read_data = new String(dataArray);
			    }
			    fis.close();

			 

			   } catch (FileNotFoundException e) {

			    e.printStackTrace();
			   } catch (IOException e) {

			    e.printStackTrace();
			   }
		 
		 System.out.println("read_data " + read_data);
		 //load into globaluserprofile
		  String[] userProfileStack = read_data.split(";"); 	
		   GlobalUserProfile.user_email = userProfileStack[0];
		  GlobalUserProfile.user_first_name = userProfileStack[1];
		  GlobalUserProfile.user_last_name = userProfileStack[2];
		  GlobalUserProfile.user_birthdate = userProfileStack[3];
		  GlobalUserProfile.user_height = userProfileStack[4];
		  GlobalUserProfile.user_weight = userProfileStack[5];
		  GlobalUserProfile.user_gender = userProfileStack[6];
		  GlobalUserProfile.user_goal = userProfileStack[7];
		  GlobalUserProfile.user_upload = userProfileStack[8];
		
		checkTheTime();

    }

	public void onSensorChanged(SensorEvent event)
    {
    	/******************* display steps *********************/
    	if (activityRunning)
    	{   System.out.println("memmemme " + read_data);
    		stepCounter.setText(String.valueOf( Math.abs((int) (event.values[0] - subtractor))));
    	}

		 /**************   miles calculation *******************/
		 textView = (TextView) findViewById(R.id.miles);
		 String stringValueSteps = stepCounter.getText().toString();
		 double tempDistance = Double.parseDouble(stringValueSteps) /2000;
		 textView.setText( String.format( "%.2f", tempDistance ));
		 
		 /**************  calories calucation *****************/
		 textView = (TextView) findViewById(R.id.calories);
		 double tempCalories = (.75 * userWeight) * tempDistance;
		 textView.setText( String.format( "%.2f", tempCalories ));
		 
		 /************  time calcuation ********************/
		 
		 calendar = Calendar.getInstance();
	   	  currentSec = calendar.get(Calendar.SECOND);
	   	  textView = (TextView) findViewById(R.id.time);
	   	  if(currentSec == seconds)
	   	  {
	   		  //do nothing
	   	  }
	   	  
	   	  else if(currentSec != seconds)
	   	  {
	   		  seconds = currentSec;
	   		  
	   		  if(seconds != 0)
	   		  {
	   			  textView.setText(String.valueOf(hours)+":"+ String.valueOf(minutes) +":"+ String.valueOf(seconds));
	   		  }
	   		  
	   		  else if(seconds == 0)
	   		  {
	   			  minutes++;
	   			  textView.setText(String.valueOf(hours)+":"+ String.valueOf(minutes) +":"+ String.valueOf(seconds));
	   			  
	   			  if(minutes == 60)
	   			  {
	   				  minutes = 0;
	   				  hours++;
	   	   			  textView.setText(String.valueOf(hours)+":"+ String.valueOf(minutes) +":"+ String.valueOf(seconds));
	
	   			  }
	   		  }
	   	  }
	    	

	
		 /*************   speed calcuation ******************/
		    //   miles/hours  ex 2.2miles/.58 hours == 3.79 MPH
	        
	        //convert everything to seconds
	        //convert minutes to seconds
	        int minsToSecs = 60*minutes;
	        
	        //convert hours to seconds
	        int hoursToSecs = 3600 *hours;
	        
	        int totalSecs = hoursToSecs+minsToSecs+seconds;
	        double convertedHours = totalSecs * 0.000277778;
	        double MPH = tempDistance/convertedHours;
	        
	        textView = (TextView) findViewById(R.id.speed);
	        String tempString = Double.toString(MPH);
//	        tempString = tempString.substring(0,4);
			 textView.setText(tempString); 
		  
		 
			  
		    

		     GlobalUserDailyStats.theValue = (int) event.values[0];
		 }
	 
	 
	 protected void onResume() 
	 {
		 super.onResume();
	     activityRunning = true;
	     Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
	     if (countSensor != null) 
	     {
	    	 sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
	     } 
	} 
	 
	  protected void onPause() 
	  {
		  super.onPause();
	      activityRunning = false;
	  } 
	  
	  @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) 
	    {
		  
	    } 


	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.updateProfile)
		{
			Intent intent = new Intent(this, CreateNewUserProfile.class);
			startActivity(intent);
		}
		
		else if(v.getId() == R.id.logout)
		{
			//Intent intent = new Intent(this, Gpluslogin.class);
			//startActivity(intent);
		}
		
		else if(v.getId() == R.id.createHistory)
		{
			//Intent intent = new Intent(this, Create_User_History.class);
			//startActivity(intent);
		}
		
		else if(v.getId() == R.id.reset)
		{/*
			//reset steps to 0
			textView = (TextView) findViewById(R.id.steps);
			textView.setText("0");
			
			//set substractor == current step counter
			String tempSubtractor = textView.getText().toString();
			subtractor = GlobalUserDailyStats.theValue;
			
			//reset calories to 0
			textView = (TextView) findViewById(R.id.calories);
			textView.setText("0");
			
			//reset distance to 0
			textView = (TextView) findViewById(R.id.miles);
			textView.setText("0");
			
			//reset speed to 0
			textView = (TextView) findViewById(R.id.speed);
			textView.setText("0");
			
			//reset time to 0
			textView = (TextView) findViewById(R.id.time);
			textView.setText("0");*/
		} 
		
		else if(v.getId() == R.id.generateSteps)
		{
			//Intent intent = new Intent(this, StepChart.class);
		//	startActivity(intent);
			
			//myTask3 task = new myTask3();
			//task.execute(); 
		}
		
	} 
	
	private class myTask2 extends AsyncTask<Void, Void, Void>
	{ 
		//@Override
		protected Void doInBackground(Void... params) 
		{
			try {
				try {
					
					//read from internal stoprage and then upload to database
					DatabaseQueries.saveUserActivity(GlobalUserProfile.user_email,
							GlobalUserDailyStats.user_date, 
							GlobalUserDailyStats.daily_steps,
							GlobalUserDailyStats.daily_time, 
							GlobalUserDailyStats.daily_distance, "NULL", 
							GlobalUserDailyStats.daily_calories, 
							GlobalUserDailyStats.daily_goal_met);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//DatabaseQueries.saveUserActivity("rd.vb.rd@gamil.com", "ldl", "lala", "lala", "lala", "lalal", "lala", "lala");
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}  
					
		 @Override
		   protected void onPostExecute(Void result) {
		       super.onPostExecute(result);
		       
		   }
	
		 
 } 
	

	private class myTask3 extends AsyncTask<Void, Void, Void>
	{ 
		//@Override
		protected Void doInBackground(Void... params) 
		{
			
			return null;
		}  
					
		 @Override
		   protected void onPostExecute(Void result) {
		       super.onPostExecute(result);
		       Intent intent = new Intent(TrackDailyUser.this, StepChart.class);
				startActivity(intent);
		   }
	
		 
	}
	
	public void checkTheTime()
	{
		
		theHandler.postDelayed(runme, 10000);

		 SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy"); 
		 Calendar calendar = Calendar.getInstance();

		 int currentDayOfMonth = calendar.get(Calendar.MONTH);
		 int currentDayOfWeek = calendar.get(Calendar.DAY_OF_MONTH);
		 int currentDayOfYear = calendar.get(Calendar.YEAR);

		 String uploadDate = dateFormat.format(calendar.getTime());
		 
		 String tempUploadDate1 = uploadDate.substring(0, 4);
		 String tempUploadDate2 = uploadDate.substring(4);
		 
		 uploadDate = tempUploadDate2+tempUploadDate1;

	       dateFormat = new SimpleDateFormat("HH::mm::ss");  
			 calendar = Calendar.getInstance();
	      
	      int currentHour = calendar.get(Calendar.HOUR);
	      int currentMin = calendar.get(Calendar.MINUTE);
	      String hourString = Integer.toString(currentHour);
	      String minuteString = Integer.toString(currentMin);
	      String hourmin = hourString+":"+minuteString;

	      
	      String[] userUploadParse = GlobalUserProfile.user_upload.split(":");
	      String userUploadHour = userUploadParse[0];
	      String userUploadMin = userUploadParse[1];
	     
	      
	      String[] hourMinParse = hourmin.split(":");
	      String uploadHour = hourMinParse[0];
	      String uploadMin = hourMinParse[1];
	      
	      int uhour = Integer.parseInt(userUploadHour.trim());
	      int umin = Integer.parseInt(userUploadMin.trim());
	      int shour = Integer.parseInt(uploadHour.trim());
	      int smin = Integer.parseInt(uploadMin.trim());
	      
	      if( uhour == shour && umin == smin)
		   {
	    	  //populate GlobalUserDialyStats
	    	  GlobalUserDailyStats.user_date = uploadDate;
	    	  TextView tempTextView;
	    	  String tempString;
	    	  
	    	  tempTextView = (TextView) findViewById(R.id.steps);
	    	  tempString = tempTextView.getText().toString();
			  GlobalUserDailyStats.daily_steps = tempString;
			   
			  tempTextView = (TextView) findViewById(R.id.time);
	    	  tempString = tempTextView.getText().toString();
			  GlobalUserDailyStats.daily_time = tempString;
			  
			  
			  tempTextView = (TextView) findViewById(R.id.miles);
	    	  tempString = tempTextView.getText().toString();
			  GlobalUserDailyStats.daily_distance = tempString;
			  
			  tempTextView = (TextView) findViewById(R.id.calories);
	    	  tempString = tempTextView.getText().toString();
			  GlobalUserDailyStats.daily_calories = tempString;
			  
			  //caculate goal met
			  tempTextView = (TextView) findViewById(R.id.steps);
	    	  tempString = tempTextView.getText().toString();
	    	  int goalDone = Integer.parseInt(tempString);
	    	  int goalTyped = Integer.parseInt(GlobalUserProfile.user_goal);
	    	  
			  if(goalDone >= goalTyped)
			  {
				  GlobalUserDailyStats.daily_goal_met = "T";
			  }
			  
			  else
				  GlobalUserDailyStats.daily_goal_met = "F";
			  

			   MediaPlayer mPlayer = MediaPlayer.create(this, R.raw.uploadtone);
			   mPlayer.start();
			   
		
			   	Context context = getApplicationContext();
			   	CharSequence text = "UPLOADING YOUR DAILY STATS";
			   	int duration = Toast.LENGTH_SHORT;

			   	Toast toast = Toast.makeText(context, text, duration);
			   	toast.show();   
			   	
			   	//save daily activties to datbase if use did something today
			   	myTask2 task = new myTask2();
				task.execute(); 	
				
				//reset everthing to 0
				TextView textView = (TextView) findViewById(R.id.steps);

				//convert textview to string
				String text2 = textView.getText().toString();
				

				//reset steps to 0
				int convertedSteps = Integer.parseInt(text2);
				subtractor = GlobalUserDailyStats.theValue; 
				textView.setText("0");
				
				//reset distance to 0
				textView = (TextView) findViewById(R.id.miles);
				textView.setText("0");
				
				//reset calories to 0
				textView = (TextView) findViewById(R.id.calories);
				textView.setText("0");
				
				//reset speed to 0
				textView = (TextView) findViewById(R.id.speed);
				textView.setText("0");
				
				//reset time to 0
				textView = (TextView) findViewById(R.id.time);
				textView.setText("0");
		   } 
	}
	
	private Runnable runme = new Runnable()
	{
		@Override
		public void run()
		{
			checkTheTime();
		}
	}; 
}

	