package com.example.fitnessbuddy;

import java.io.*;
import java.util.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import android.util.Log;

//import com.sun.tools.javac.util.Pair;

import java.util.Date;


public class DatabaseQueries {

	//	Pass all arguments as STRINGS
	//	Dates should be STRINGS in "YYYYMMDD" format
	//	If changing functions/map variables to "non-static" need to declare separate object in calling class
	//		-	DatabaseQueries test = new DatabaseQueries();
	//	Information is returned in reverse order 
	//		-	TreeMaps - sort by KEY = date 
	//		-	Because we initialize with (Collections.reverseOrder()), sorted by DESCENDING order, e.g. most recent first
	//	Android manifest needs to allow Internet access
	//		<!-- Permission: Allow application to connect to Internet -->
	//		<uses-permission android:name="android.permission.INTERNET" />
	
	
	
	private static ConnectToHTTP jsonParser = new ConnectToHTTP();
	
	private static final String TAG_SUCCESS = "success";				//	All activities
	private static final String TAG_USERINFO = "userInfo";				//	getUserInfo -- populating user info page
	private static final String TAG_HISTORY = "history";				//	getHistoryPage
	private static final String TAG_STEP_CHART = "stepchart";			//	getStepData
	private static final String TAG_DISTANCE_CHART = "distancechart";	//	getDistanceData
	private static final String TAG_CALORIES_CHART = "calorieschart";	//	getCalorieData
	private static final String TAG_STEPS = "steps";					//	getStepData, getHistoryPage
	private static final String TAG_DISTANCE = "distance";				//	getDistanceData, getHistoryPage
	private static final String TAG_CALORIES = "calories";				//	getCalorieData, getHistoryPage
	private static final String TAG_EMAIL = "email";					//	All activities
	private static final String TAG_FIRST = "first_name";				//	getUserInfo
	private static final String TAG_LAST = "last_name";					//	getUserInfo
	private static final String TAG_BIRTHDAY = "date_of_birth";			//	getUserInfo
	private static final String TAG_HEIGHT = "height";					//	getUserInfo
	private static final String TAG_WEIGHT = "weight";					//	getUserInfo
	private static final String TAG_GENDER = "gender";					//	getUserInfo
	private static final String TAG_GOAL = "goal";						//	getUserInfo
	private static final String TAG_DATE = "date";						//	getHistoryPage
	private static final String TAG_TIME = "time";						//	getHistoryPage
	private static final String TAG_SPEED = "speed";					//	getHistoryPage
	private static final String TAG_GOALMET = "goal_met";				//	getHistoryPage
	private static final String TAG_UPLOADTIME = "upload_time";			//	getUserInfo
	//private static final String TAG_START = "start_time";				//	getHistoryPage
	//private static final String TAG_END = "end_time";					//	getHistoryPage
	    
	static Vector<String> getUserInfoResults = new Vector<String>();
	
	static TreeMap<String, ArrayList<String>> historyMap = new TreeMap<String, ArrayList<String>>(Collections.reverseOrder());
	static TreeMap<Long, ArrayList<String>> historyMapEpoch = new TreeMap<Long, ArrayList<String>>(Collections.reverseOrder());
	static TreeMap<Date, ArrayList<String>> historyMapDate = new TreeMap<Date, ArrayList<String>>(Collections.reverseOrder());
	static TreeMap<Integer, ArrayList<String>> historyMapInteger = new TreeMap<Integer, ArrayList<String>>(Collections.reverseOrder());
	
	static TreeMap<String, Integer> stepMap = new TreeMap<String, Integer>(Collections.reverseOrder());
	static TreeMap<String, Double> distanceMap = new TreeMap<String, Double>(Collections.reverseOrder());
	static TreeMap<String, Double> calorieMap = new TreeMap<String, Double>(Collections.reverseOrder());
    
    static TreeMap<Date, Integer> stepMapDate = new TreeMap<Date, Integer>(Collections.reverseOrder());		//	date may have time stamp as well
    static TreeMap<Date, Double> distanceMapDate = new TreeMap<Date, Double>(Collections.reverseOrder());	//	date may have time stamp as well
    static TreeMap<Date, Double> calorieMapDate = new TreeMap<Date, Double>(Collections.reverseOrder());	//	date may have time stamp as well
    	
    static TreeMap<Long, Integer> stepMapEpoch = new TreeMap<Long, Integer>(Collections.reverseOrder());		//	Epoch
    static TreeMap<Long, Double> distanceMapEpoch = new TreeMap<Long, Double>(Collections.reverseOrder());		//	Epoch
    static TreeMap<Long, Double> calorieMapEpoch = new TreeMap<Long, Double>(Collections.reverseOrder());		//	Epoch
    
    static TreeMap<Integer, Integer> stepMapInteger = new TreeMap<Integer, Integer>(Collections.reverseOrder());
    static TreeMap<Integer, Double> distanceMapInteger = new TreeMap<Integer, Double>(Collections.reverseOrder());
    static TreeMap<Integer, Double> calorieMapInteger = new TreeMap<Integer, Double>(Collections.reverseOrder());

    
	//	Checks to see if user is already in database
	//		-	TAG_SUCCESS = 0 if new user -> will save to database -> go to user information page
	//		-	TAG_SUCCESS = 1 if user already registered in database -> go to main page
	//		-	TAG_SUCCESS = 2 if there is a problem with POST information
    //		-	[TAG_SUCCESS = 3 if new user but problem with creating new entry in database]
	public static int checkLogin (String email) throws IllegalStateException, IOException, JSONException {

		String url = "http://198.252.66.101/~capfit/app/create_new_user.php";
		int success = 0;
		
		//	Building parameters
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("Email", email));
					
		//	Connect to database and get query results as a JSON object
		//		-	Returns {"success": 0/1/2/3, "message":  "[some string]"}
		JSONObject json = jsonParser.executeHttpPost(url, params);
				//JSONObject json = ConnectToHTTP.executeHttpPost(url, params);				
				//System.out.println("Returned JSON Object from Post Request Check Login " + json.toString());		
				//JSONObject json=new JSONObject();
				//json.put("success","1");
				
		//	Check your log cat for JSON reponse
		//Log.d("Check Login: ", json.toString());
			
		//	Find out if user added or there was an error
		try {
			
			success = json.getInt(TAG_SUCCESS);
			if (success == 1) {
				return success;
			}
		
			else {/*do something if there was an error, e.g. status = 0 or 2 or 3*/}
		
		}	catch (JSONException e) {
			e.printStackTrace();
		}

		return success;
		
	}
	
	
	//	Call when user clicks "SAVE" on the User Info Page
	//		-	TAG_SUCCESS = 1 if user information saved
	//		-	TAG_SUCCESS = 0 if user information not saved
	//		-	TAG_SUCCESS = 2 if there is a problem with POST information
	//		-	TAG_SUCCESS = 3 if user not in database (should not happen)
	public static int loadUserInfo (String email, String firstName, String lastName,
								String gender, String birthday, 
								String weight, String height,
								String goal, String uploadTime) throws IllegalStateException, IOException {
		String url = "http://198.252.66.101/~capfit/app/load_user_info.php";
		int success = 0;
		
		//	Building parameters
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Email", email));
			params.add(new BasicNameValuePair("First", firstName));
			params.add(new BasicNameValuePair("Last", lastName));
			params.add(new BasicNameValuePair("Birthday", birthday));
			params.add(new BasicNameValuePair("Height", height));
			params.add(new BasicNameValuePair("Weight", weight));
			params.add(new BasicNameValuePair("Gender", gender));
			params.add(new BasicNameValuePair("Goal", goal));
			params.add(new BasicNameValuePair("Upload", uploadTime));
	
		//Connect to database and get query results as a JSON object
		//	-	Returns {"success":  0/1/2/3, "message":  "[some string]"}
		JSONObject json = jsonParser.executeHttpPost(url, params);
			//System.out.println("Returned JSON Object from Save User Info Post Request " + json.toString());
			//JSONObject json = ConnectToHTTP.executeHttpPost(url, params);
		
		// Check your log cat for JSON response
		Log.d("Check User Info Upload: ", json.toString());	

		try {
			
			success = json.getInt(TAG_SUCCESS);
			
			if (success == 1) {
				return success;
				//System.out.println("User Info Saved");
			}
		
			else {
				//System.out.println("Problem Saving User Info");
			}
		
		}	catch (JSONException e) {
			e.printStackTrace();
		}

		return success;
		
	}
	
	
	//	Call when it is time to update database
	//		-	May need to add startTime and endTime
	//		-	TAG_SUCCESS = 0 if user already has entry for this day; data sent not saved
	//		-	TAG_SUCCESS = 1 if user information saved
	//		-	TAG_SUCCESS = 2 if there is a problem with POST information
	//		-	TAG_SUCCESS = 3 if there was a problem saving information
	//		-	TAG_SUCCESS = 4 if user not registered (should not happen)
	public static int saveUserActivity (String email, String todayDate, String steps, 
			String time, String distance, String speed,
			String calories, String goalMet
			/*, string startTime, string endTime*/) throws IllegalStateException, IOException {
		
		String url = "http://198.252.66.101/~capfit/app/daily_user_upload.php";
		int success = 0;
		
		//	Building parameters
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Email", email));	
			params.add(new BasicNameValuePair("Date", todayDate));
			params.add(new BasicNameValuePair("Steps", steps));
			params.add(new BasicNameValuePair("Time", time));
			params.add(new BasicNameValuePair("Distance", distance));
			params.add(new BasicNameValuePair("Speed", speed));
			params.add(new BasicNameValuePair("Calories", calories));
			params.add(new BasicNameValuePair("Goal", goalMet));
			//params.add(new BasicNameValuePair("Start_Time", startTime));
			//params.add(new BasicNameValuePair("End_Time", endTime));
			
			
		//	Connect to database and get query results as a JSON object
		//		-	Returns {"success":  0/1/2/3, "message":  "[some string]"}
		JSONObject json = jsonParser.executeHttpPost(url, params);
			//System.out.println("Returned JSON Object from Post Request " + json.toString());
			//JSONObject json = ConnectToHTTP.executeHttpPost(url, params);

		// Check your log cat for JSON response
		Log.d("Check User Activity Upload: ", json.toString());
			
		try {
				
			success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					return success;
				}
			
				else {}
			
			}	catch (JSONException e) {
				e.printStackTrace();
			}

		return success;
		
	}

	
	//	Call when User Info Page is pulled up EXCEPT when reached from login screen
	//		(because this means this is a new user and no user info in the database)
	//		-	Fields returned are:  email, first_name, last_name, date_of_birth, height
	//				weight, gender, goal
	//		-	TAG_SUCCESS = 1 if user information retrieved
	//		-	TAG_SUCCESS = 0 if user information not retrieved
	//		-	TAG_SUCCESS = 2 if there is a problem with GET information
	//	-	Vector Order - TAG_SUCCESS, email, first name, last name, birthday, height, weight, gender, goal, upload time
	public static Vector<String> getUserInfo (String email) throws IllegalStateException, IOException, JSONException {
		
		String url = "http://198.252.66.101/~capfit/app/get_user_info.php";	
		getUserInfoResults.clear();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Email", email));
			
			//	Connect to database and get query results as a JSON object
				//	Returns:
				//		A.	{"success":  0/2, "message":  "[some string]"} OR
				//		B.	{	"success": 1,
				//				"user info":
				//				[
				//					{"email": "[email string]" ....}....{"tag":  "string"}
				//				]
			//			}		
			JSONObject json = jsonParser.executeHttpGet(url, params);
				//System.out.println("Returned JSON Object from Get Request " + json.toString());
				//JSONObject json = ConnectToHTTP.executeHttpGet(url, params);
		
		// Check your log cat for JSON reponse
		Log.d("Check User Info Download: ", json.toString());
		
		try {
		
			int success = json.getInt(TAG_SUCCESS);
			String success_array_value = String.valueOf(success);
				//System.out.println("Success " + success);
			getUserInfoResults.add(success_array_value);
				//System.out.println("Success Array Value " + success_array_value);
			
            // successfully received user information and parse into strings
            if (success == 1) {
     	
            	JSONArray userInfoObj = json.getJSONArray(TAG_USERINFO);
            	
                // get first object from JSON Array
                JSONObject info = userInfoObj.getJSONObject(0);
                
                String email_user = info.getString(TAG_EMAIL);
                getUserInfoResults.add(email_user);
                	//System.out.println(email_user);
                String firstName_user = info.getString(TAG_FIRST);
                getUserInfoResults.add(firstName_user);
                	//System.out.println(firstName_user);
                String lastName_user = info.getString(TAG_LAST);
                getUserInfoResults.add(lastName_user);
                	//System.out.println(lastName_user);
                String birthday_user = info.getString(TAG_BIRTHDAY);
                getUserInfoResults.add(birthday_user);
                	//System.out.println(birthday_user);
                String height_user = info.getString(TAG_HEIGHT);
                getUserInfoResults.add(height_user);
                	//System.out.println(height_user);
                String weight_user = info.getString(TAG_WEIGHT);
                getUserInfoResults.add(weight_user);
                	//System.out.println(weight_user);
                String gender_user = info.getString(TAG_GENDER);
                getUserInfoResults.add(gender_user);
                	//System.out.println(gender_user);
                String goal_user = info.getString(TAG_GOAL);
                getUserInfoResults.add(goal_user);
                	//System.out.println(goal_user);
                String uploadTime_user = info.getString(TAG_UPLOADTIME);
                getUserInfoResults.add(uploadTime_user);
                	//System.out.println(uploadTime_user);
                
            }
            
            else {getUserInfoResults = null; }
            
		}	catch (JSONException e) {
			e.printStackTrace();
		}
	
		return getUserInfoResults;
		
	}
	
	
	//	Call when user navigates to History Page
	//		-	Fields returned are date and for each date:  steps, time, distance,
	//				speed, calories, goal_met
	//		-	All data is placed into Map with key = date(string) value = ArrayList(string)
	//		-	TAG_SUCCESS = 1 if history retrieved
	//		-	TAG_SUCCESS = 0 if history not retrieved
	//		-	TAG_SUCCESS = 2 if there is a problem with GET information
	//	Returns Hash/TreeMap = key = date, value = array list with order: steps, time, distance, speed, calories, goal met
	//		-	***IF THERE IS AN ERROR, then key = 0 or 2 and value is an empty array; returns Map = NULL
	public static TreeMap<String, ArrayList<String>> getHistoryPage (String email) throws IllegalStateException, IOException {

		String url = "http://198.252.66.101/~capfit/app/get_history_page_data.php";
		historyMap.clear();
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Email", email));
			
		//Connect to database and get query results as a JSON object
			//	Returns:
			//		A.	{"success":  0/2, "message":  "[some string]"} OR
			//		B.	{	"history":
			//				[
			//					{"date": "[date string]" ....}
			//				],
			//				"success": 1
			//			}
		JSONObject json = jsonParser.executeHttpGet(url, params);
			//System.out.println("Returned JSON Object from Get Request " + json.toString());
			//JSONObject json = ConnectToHTTP.executeHttpGet(url, params);
		
		// Check your log cat for JSON reponse
		Log.d("Check User History Download: ", json.toString());
		
		try {
		
			int success = json.getInt(TAG_SUCCESS);
			String success_array_value = String.valueOf(success);

			// successfully received user information and parse into strings
			if (success == 1) {
        	
				JSONArray myData = json.getJSONArray(TAG_HISTORY);

				for (int i = 0; i < myData.length(); i++) {

					ArrayList<String> valueArray = new ArrayList<String>();
					JSONObject c = myData.getJSONObject(i);
						//System.out.println("JSON Object added to array " + c.toString());

					//	Map key
					String date = c.getString(TAG_DATE);
					long date1 = Long.parseLong(date);				//	converts string to Epoch - key
					Date date2 = new Date(date1);					//	converts Epoch to Java Date - key
					int date3 = Integer.parseInt(date);				//	converts string to integer - key
						//Date date2 = (Date)formatter.parse(date);		//	if date is in MMDDYYYY       		
						//System.out.println("Day " + i + " dates:  " + date + " " + date1 + " " + date2);
					
					//	Populate ArrayList with strings
					String steps = c.getString(TAG_STEPS);
        				valueArray.add(steps);
        				//System.out.println("Steps " + steps);
        			String time = c.getString(TAG_TIME);
        				valueArray.add(time);
        				//System.out.println("Time " + time);
        			String distance = c.getString(TAG_DISTANCE);
        				valueArray.add(distance);
        				//System.out.println("Distance " + distance);
        			String speed = c.getString(TAG_SPEED);
        				valueArray.add(speed);
        				//System.out.println("Speed " + speed);
        			String calories = c.getString(TAG_CALORIES);
        				valueArray.add(calories);
        				//System.out.println("Calories " + calories);
        			String goalMet = c.getString(TAG_GOALMET);
        				valueArray.add(goalMet);
        				//System.out.println("Goal Met " + goalMet);
        				/*String start = c.getString(TAG_START);
        				valueArray.add(start);
        				String end = c.getString(TAG_END);
            			valueArray.add(end);*/
        			
        			//	Add to Map (key = date, value = ArrayList holding all other data); all array values are strings
        			historyMap.put(date, valueArray);			//	(string, string) -- date is string of digits
        				//System.out.println("added " + date + " and Value Array Time is " + valueArray.get(1));
        				//System.out.println("Map Size is " + historyMap.size());
        			historyMapEpoch.put(date1, valueArray);		//	(long, string) -- date is Epoch
        			historyMapDate.put(date2, valueArray);		//	(long, string) -- date is Java Date
        			historyMapInteger.put(date3, valueArray);	//	(Integer, string) -- date is an integer (YYYYMMDD)
        		
				}

					/*System.out.println("Map Size is " + historyMapEpoch.size());
					System.out.println("Using KeySet");
			        for(Long key: historyMapEpoch.keySet()){
			        System.out.println(key  +" :: "+ historyMapEpoch.get(key));
			        }*/
				
				return historyMap;
				
			}
			
			else {
				historyMap = null;
				historyMapEpoch = null;
				historyMapDate = null;
				historyMapInteger = null;
			}
        
		}	catch (JSONException e){
			e.printStackTrace();
		}
	
		return historyMap;
		
	}
	
	
	//	Call when user navigates to Step Charts
	//		-	Fields returned are date and steps for that day
	//		-	All data is placed into Map with key = date(string) value = steps(string)
	//		-	TAG_SUCCESS = 1 if step history retrieved
	//		-	TAG_SUCCESS = 0 if step history not retrieved
	//		-	TAG_SUCCESS = 2 if there is a problem with GET information
	//	Returns Hash/TreeMap:  key = date, value = steps
	//		-	***If error, the key = 0 or 2, value = 0 and returns a NULL Map
	//		-	http://examplesofjava.com/hashmap/hashmap-iterator.html
	public static TreeMap<String, Integer> getStepData (String email) throws IllegalStateException, IOException, JSONException {
		
		String url = "http://198.252.66.101/~capfit/app/get_chart_data_steps.php";
		
		stepMap.clear();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Email", email));

			//Connect to database and get query results as a JSON object
				//	Returns:
				//		A.	{"success":  0/2, "message":  "[some string]"} OR
				//		B.	{	"history":
				//				[
				//					{"date": "[date string]", "steps": "step string"}....
				//				],
				//				"success": 1
				//			}
			JSONObject json = jsonParser.executeHttpGet(url, params);
				//System.out.println("Returned JSON Object from Get Request " + json.toString());
				//JSONObject json = ConnectToHTTP.executeHttpGet(url, params);
						
			// Check your log cat for JSON response
			Log.d("Check User History Download: ", json.toString());
			
			try {

				int success = json.getInt(TAG_SUCCESS);

				// successfully received user information and parse into strings
				if (success == 1) {
	        	
					JSONArray myData = json.getJSONArray(TAG_STEP_CHART);
	        	
					for (int i = 0; i < myData.length(); i++) {

						JSONObject c = myData.getJSONObject(i);

						//	Different date formats
						String date = c.getString(TAG_DATE);			//	Map key = string of digits
							//System.out.println("Date String: " + date);
						long date1 = Long.parseLong(date);				//	converts string to Epoch - key
							//System.out.println("Date Long:  " + date1);
						Date date2 = new Date(date1);					//	converts Epoch to Java Date - key
							//System.out.println("Date Date:  " + date2);
							//Date date2 = (Date)formatter.parse(date);		
						int date3 = Integer.parseInt(date);				//	converts string to integer - key

						//	Different step formats
						String steps = c.getString(TAG_STEPS);		//	Map value
							//System.out.println("Step String:  " + steps);
						int steps1 = Integer.parseInt(steps);		//	Map value
							//System.out.println("Steps Int:  " + steps1);
				
						
						stepMap.put(date, steps1);					//	Add to Map (string, double)
						stepMapEpoch.put(date1, steps1);			//	Add to Map (long, int)
						stepMapDate.put(date2, steps1);				//	Add to Map (date, int)
						stepMapInteger.put(date3, steps1);			//	Add to Map (int, int)

					}
				
				return stepMap;
					
				}
				
				else {
					stepMap = null;
					stepMapEpoch = null;
					stepMapDate = null;
					stepMapInteger = null;
				}

			}	catch (JSONException e){
				e.printStackTrace();
			}	
	
		return stepMap;	
	}

	
	//	Call when user navigates to Distance Charts
	//		-	Fields returned are date and distance for that day
	//		-	All data is placed into Map with key = date(string) value = distance(string)
	//		-	TAG_SUCCESS = 1 if distance history retrieved
	//		-	TAG_SUCCESS = 0 if distance history not retrieved
	//		-	TAG_SUCCESS = 2 if there is a problem with GET information
	//	Returns Hash/Treemap:  key = date, value = distance
	//		-	***If error, the key = 0 or 2, value = 0 and returns NULL map
	public static TreeMap<String, Double> getDistanceData (String email) throws IllegalStateException, IOException {
	
		String url = "http://198.252.66.101/~capfit/app/get_chart_data_distance.php";
		
		distanceMap.clear();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Email", email));

			//Connect to database and get query results as a JSON object
				//	Returns:
				//		A.	{"success":  0/2, "message":  "[some string]"} OR
				//		B.	{	"history":
				//				[
				//					{"date": "[date string]", "distance": "distance string"}....
				//				],
				//				"success": 1
				//			}
			JSONObject json = jsonParser.executeHttpGet(url, params);
				//System.out.println("Returned JSON Object from Get Request " + json.toString());
				//JSONObject json = ConnectToHTTP.executeHttpGet(url, params);	

			// Check your log cat for JSON response
			Log.d("Check User History Download: ", json.toString());
			
			try {

				int success = json.getInt(TAG_SUCCESS);

				// successfully received user information and parse into strings
				if (success == 1) {
	        	
					JSONArray myData = json.getJSONArray(TAG_DISTANCE_CHART);
	        	
					for (int i = 0; i < myData.length(); i++) {			
						
						JSONObject c = myData.getJSONObject(i);
						
						//	Date Formats
						String date = c.getString(TAG_DATE);				//	Map key - string of digits				
						long date1 = Long.parseLong(date);					//	converts string to Epoch - key
						Date date2 = new Date(date1);						//	converts Epoch to Java Date - key
							//Date date2 = (Date)formatter.parse(date);		
						int date3 = Integer.parseInt(date);					//	converts string to integer - key

						//	Distance Formats
						String distance = c.getString(TAG_DISTANCE);		//	Map value
						double distance1 = Integer.parseInt(distance);		//	Map value
						
						distanceMap.put(date, distance1);					//	Add to Map (string, double)
						distanceMapEpoch.put(date1, distance1);				//	Add to Map (long, double)						
						distanceMapDate.put(date2, distance1);				//	Add to Map (date, double)
						distanceMapInteger.put(date3, distance1);			//	Add to Map (int, double)
						
					}
	        
					return distanceMap;
				
				}
	
				else {
					distanceMap = null;
					distanceMapEpoch = null;
					distanceMapDate = null;
					distanceMapInteger = null;
				}
				
			}	catch (JSONException e) {
				e.printStackTrace();
			}
	
			return distanceMap;	
			
	}
	
	
	//	Call when user navigates to Calorie Charts
	//		-	Fields returned are date and calories for that day
	//		-	All data is placed into Map with key = date(string) value = calories(string)
	//		-	TAG_SUCCESS = 1 if calorie history retrieved
	//		-	TAG_SUCCESS = 0 if calorie history not retrieved
	//		-	TAG_SUCCESS = 2 if there is a problem with GET information
	//	Returns Hash/Treemap:  key = date, value = calories
	//		-	***If error, the key = 0 or 2, value = 0 and returns NULL map
	public static TreeMap<String, Double> getCalorieData (String email) throws IllegalStateException, IOException {
		
		String url = "http://198.252.66.101/~capfit/app/get_chart_data_calories.php";

		calorieMap.clear();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Email", email));
			
			//Connect to database and get query results as a JSON object
				//	Returns:
				//		A.	{"success":  0/2, "message":  "[some string]"} OR
				//		B.	{	"history":
				//				[
				//					{"date": "[date string]", "calories": "calories string"}....
				//				],
				//				"success": 1
				//			}
			JSONObject json = jsonParser.executeHttpGet(url, params);
				//System.out.println("Returned JSON Object from Get Request " + json.toString());
				//JSONObject json = ConnectToHTTP.executeHttpGet(url, params);
			
			// Check your log cat for JSON response
			Log.d("Check User History Download: ", json.toString());
			
			try {

				int success = json.getInt(TAG_SUCCESS);

				// successfully received user information and parse into strings
				if (success == 1) {
	        	
					JSONArray myData = json.getJSONArray(TAG_CALORIES_CHART);
	        	
					for (int i = 0; i < myData.length(); i++) {
						
						JSONObject c = myData.getJSONObject(i);
						
						//	Date Formats
						String date = c.getString(TAG_DATE);			//	Map key - string of digits
						long date1 = Long.parseLong(date);				//	converts string to Epoch - key
						Date date2 = new Date(date1);					//	converts Epoch to Java Date - key
						//Date date2 = (Date)formatter.parse(date);		
						int date3 = Integer.parseInt(date);				//	converts string to integer - key
						
						//	Calories Formats
						String calories = c.getString(TAG_CALORIES);	//	Map value
						double calories1 = Integer.parseInt(calories);	//	Map value

						calorieMap.put(date, calories1);				//	Add to Map (string, double)		
						calorieMapEpoch.put(date1, calories1);			//	Add to Map (long, double)
						calorieMapDate.put(date2, calories1);			//	Add to Map (date, double)
						calorieMapInteger.put(date3, calories1);		//	Add to Map (int, double)
											
					}
	        
					return calorieMap;
					
				}
				
				else {
					calorieMap = null;
					calorieMapEpoch = null;
					calorieMapDate = null;
					calorieMapInteger = null;
				}
				
			}	catch (JSONException e){
				e.printStackTrace();
			}

		return calorieMap;
			
	}
		
	
}	//	END OF CLASS





//	DRAFT CODE - NOT USED
/*for (int i = 0; i < json.length(); i++) {
JSONObject jsonObject = json.getJSONObject(i);
}
private static final String TAG_EMAIL = "email";
private static final String TAG_FIRST = "first_name";
private static final String TAG_LAST = "last_name";
private static final String TAG_BIRTHDAY = "date_of_birth";
private static final String TAG_HEIGHT = "height";
private static final String TAG_WEIGHT = "weight";
private static final String TAG_GENDER = "gender";
private static final String TAG_GOAL = "goal";
private static final String TAG_DATE = "date";
private static final String TAG_STEPS = "steps";
private static final String TAG_TIME = "time";
private static final String TAG_SPEED = "speed";
private static final String TAG_GOALMET = "goal_met";*/


/*

ArrayList<HashMap<String, String>> historyList;	//	Not used	

String, ArrayList<String>>();
static HashMap<Long, ArrayList<String>> historyMapEpoch = new HashMap<Long, ArrayList<String>>();
static HashMap<Date, ArrayList<String>> historyMapDate = new HashMap<Date, ArrayList<String>>();
static HashMap<Integer, ArrayList<String>> historyMapInteger = new HashMap<Integer, ArrayList<String>>();

static HashMap<String, String> stepMap = new HashMap<String, String>();
static HashMap<String, String> distanceMap = new HashMap<String, String>();
static HashMap<String, String> calorieMap = new HashMap<String, String>();

static HashMap<Date, Integer> stepMapDate = new HashMap<Date, Integer>();		//	date may have time stamp as well
static HashMap<Date, Double> distanceMapDate = new HashMap<Date, Double>();	//	date may have time stamp as well
static HashMap<Date, Double> calorieMapDate = new HashMap<Date, Double>();		//	date may have time stamp as well
	
static HashMap<Long, Integer> stepMapEpoch = new HashMap<Long, Integer>();		//	Epoch
static HashMap<Long, Double> distanceMapEpoch = new HashMap<Long, Double>();	//	Epoch
static HashMap<Long, Double> calorieMapEpoch = new HashMap<Long, Double>();		//	Epoch

static HashMap<Integer, Integer> stepMapInteger = new HashMap<Integer, Integer>();
static HashMap<Integer, Double> distanceMapInteger = new HashMap<Integer, Double>();
static HashMap<Integer, Double> calorieMapInteger = new HashMap<Integer, Double>();
*/