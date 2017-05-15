package com.example.fitnessbuddy;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class CheckExists extends Activity {

	private static final String TAG = "cat";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_exists);
		
		myTask task = new myTask();
		task.execute();	
		
	}
	
	private class myTask extends AsyncTask<Void, Void, Void>
	{//String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
		int userExists = -111111;
		//String tempEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
		
		protected Void doInBackground(Void... params) {

			try {
				userExists = DatabaseQueries.checkLogin(GlobalUserProfile.user_email);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 //user already in database; now go to main tracking page
			if(userExists == 0) 
			{
				finish();
			}
			//user not in database. add email then have then enter their info
			else if(userExists != 0)
		    {
		    	Intent createNew = new Intent(CheckExists.this, CreateNewUserProfile.class);
		    	startActivity(createNew);
		    	//finish();
			 } 
				    
		

			
				
				
				
			return null;
		}
		
		@Override
		 protected void onPostExecute(Void result) {
		       super.onPostExecute(result);
		   }
	}
	
		
		
		
	}

