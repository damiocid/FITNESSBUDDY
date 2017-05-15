package com.example.fitnessbuddy;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class CreateNewUserProfile extends Activity 
{
	private static final String TAG="hello world";
	private Button go_back_to_menu; 
	private Button save_user_info;
	String FILENAME = "myfile";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_user_profile);
		
		save_user_info = (Button) findViewById(R.id.save_user_info);
		save_user_info.setOnClickListener(sendListener);
		
		go_back_to_menu = (Button) findViewById(R.id.go_back_to_tracking);
		go_back_to_menu.setOnClickListener(sendListener);

	}


	public OnClickListener sendListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{	Log.d(TAG,"bitch ass 1");
			// TODO Auto-generated method stub
			if(v.getId() == R.id.save_user_info)
			{		
				boolean emptyField = false;			    
			    EditText tempEditText;
			    String newValue;
			    
			    tempEditText = (EditText) findViewById(R.id.first_name_field);
			    newValue = tempEditText.getText().toString(); 		
			    if (newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;	
				}
				
				else
				{
					GlobalUserProfile.user_first_name = newValue;
				}
			
				tempEditText = (EditText) findViewById(R.id.last_name_field);
				newValue = tempEditText.getText().toString(); 
				if (newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;	
				}
				
				else
				{
					GlobalUserProfile.user_last_name = newValue;
				}
				
				tempEditText = (EditText) findViewById(R.id.gender_field);
				newValue = tempEditText.getText().toString(); 
				if (newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;	
				}			
				
				else
				{
					GlobalUserProfile.user_gender = newValue;
				}
				
				tempEditText = (EditText) findViewById(R.id.dob_field);
				newValue = tempEditText.getText().toString(); 
				if (newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;	
				}
				
				else
				{
					GlobalUserProfile.user_birthdate = newValue;
				}
				
				tempEditText = (EditText) findViewById(R.id.weight_field);
				newValue = tempEditText.getText().toString(); 
				if (newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;	
				}
				
				else
				{
					GlobalUserProfile.user_weight = newValue;
				}
				
				
				
				tempEditText = (EditText) findViewById(R.id.height_field);
				newValue = tempEditText.getText().toString(); 
				if (newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;	
				}
				
				else
				{
					GlobalUserProfile.user_height = newValue;
				}
		
				tempEditText = (EditText) findViewById(R.id.goal_field);
				newValue = tempEditText.getText().toString(); 
				if (newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;
				} 
				
				else
				{
					GlobalUserProfile.user_goal = newValue;
				}
				
				tempEditText = (EditText) findViewById(R.id.upload_field);
				newValue = tempEditText.getText().toString();
				if(newValue.matches("") || newValue.matches(" "))
				{
					emptyField = true;
				}
				
				else
				{
					GlobalUserProfile.user_upload = newValue;
				}
				
				if(emptyField == true)
				{
					Context context = getApplicationContext();
					CharSequence text = "YOU CANNOT HAVE EMPTY FIELDS!";
					int duration = Toast.LENGTH_LONG;
	
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
					
					toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);	
				} 
			
				
				
				//push onto internal storage here
				else if(emptyField == false)
				{
					
					
					
					myTask task = new myTask();
					task.execute();
						
					// success message
					Toast.makeText(getBaseContext(), "it's saved successfilly",
							Toast.LENGTH_SHORT).show(); 

					finish();
				}
			}
			
			else 
			{
				finish();
			}
		}
	};
	

	private class myTask extends AsyncTask<Void, Void, Void>
	{//String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

		//String tempEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

		//@Override
		protected Void doInBackground(Void... params) {
		
			try {Log.d(TAG,"bitch ass 6"); Log.d(TAG, "yoyo mama ");
			DatabaseQueries.loadUserInfo(GlobalUserProfile.user_email, GlobalUserProfile.user_first_name, 
					GlobalUserProfile.user_last_name, GlobalUserProfile.user_gender, GlobalUserProfile.user_birthdate,
					GlobalUserProfile.user_weight, GlobalUserProfile.user_height, 
					 GlobalUserProfile.user_goal,
					GlobalUserProfile.user_upload); 
			
			//DatabaseQueries.saveUserActivity("rd.vb.rd@gmail.com", "fififi", "lala", "lala", "lala", "lalal", "lala", "lala");


			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
					
			return null;
		}
		
		 @Override
		   protected void onPostExecute(Void result) {
		       super.onPostExecute(result);
		       
		     //build one string here
				String oneLineUserProfile = 
						GlobalUserProfile.user_email + ";" +
						GlobalUserProfile.user_first_name + ";" +
					    GlobalUserProfile.user_last_name + ";" +
						GlobalUserProfile.user_gender + ";" +
					    GlobalUserProfile.user_birthdate + ";" +
						GlobalUserProfile.user_weight + ";" +
					    GlobalUserProfile.user_height + ";" +
						GlobalUserProfile.user_goal + ";" +
					    GlobalUserProfile.user_upload;
				
				

		        try {
		            FileOutputStream fos = openFileOutput(FILENAME,
		              Context.MODE_PRIVATE);
		            fos.write(oneLineUserProfile.getBytes());
		            fos.close();
		        } catch (FileNotFoundException e) {

		            e.printStackTrace();
		           } catch (IOException e) {

		            e.printStackTrace();
		           }

		        System.out.println("user email: " + GlobalUserProfile.user_email);
				  System.out.println(" user first: " + GlobalUserProfile.user_first_name);
				  System.out.println("user last: " + GlobalUserProfile.user_last_name);
				  System.out.println("user birthdate: "+ GlobalUserProfile.user_birthdate);
				  System.out.println("user height: " + GlobalUserProfile.user_height);
				  System.out.println("user weight: " + GlobalUserProfile.user_weight);
				  System.out.println("user gender: " + GlobalUserProfile.user_gender);
				  System.out.println("user goal " + GlobalUserProfile.user_goal);
				  System.out.println("user upload " + GlobalUserProfile.user_upload);
		   }
		
		
	}
	
	
	
}



















