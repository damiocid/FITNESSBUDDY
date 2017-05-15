package com.example.fitnessbuddy;

//Taken from http://webdesignergeeks.com/mobile/android/android-login-authentication-with-remote-db/
//and http://www.androidhive.info/2012/05/how-to-connect-android-with-php-mysql/
//Also see http://www.vogella.com/tutorials/ApacheHttpClient/article.html

import java.io.*;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;  
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;  
//import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;  
//import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
//import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.*;

import android.util.Log;


public class ConnectToHTTP {

public static final int HTTP_TIMEOUT = 60 * 1000; 	// time it takes for client to time out in milliseconds  
private static HttpClient mHttpClient;  	// single instance of our HttpClient
static InputStream is = null;
static JSONObject jObj = null;
static JSONArray jArray = null;
static String json = "";

//	Constructor
public ConnectToHTTP() {}

//	Get our single instance of our HttpClient object, returns an HttpClient object with connection parameters set
//		-	see http://itellity.wordpress.com/2014/01/21/handling-deprecated-warnings-in-apaches-httpclient-and-httpparams/
private static HttpClient getHttpClient() {
	
	if (mHttpClient == null) {
			mHttpClient = new DefaultHttpClient();  
			final HttpParams params = mHttpClient.getParams();  
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);  
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);  
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);  
	}
		
	return mHttpClient;  
}  


// 	Performs an HTTP Post request to the specified url with the specified parameters.	//public static JSONArray executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws IllegalStateException, IOException {  
public JSONObject executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws IllegalStateException, IOException {  

	try {  
	
		//	Connect to server and get response
		HttpClient client = getHttpClient();
		HttpPost request = new HttpPost(url);  
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);  //encodes name/value pairs
		request.setEntity(formEntity);	//sends parameters to the post request
		HttpResponse response = client.execute(request);

		//	Process response
		HttpEntity entity = response.getEntity();
		
		is = entity.getContent();
	
	}
	catch (UnsupportedEncodingException e) {e.printStackTrace();}
	catch (ClientProtocolException e) {e.printStackTrace();}
	catch (IOException e) {e.printStackTrace();}
	
	try {
		
		//	Wrap a buffered reader around input stream
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;

		//	Read lines
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		
		is.close();
		json = sb.toString();
		Log.d("json buffered string ", json);
		//System.out.println("json buffered string " + json);
	}	catch (Exception e) {
		Log.e("Buffer Error", "Error converting result " + e.toString());
	}
	
	try {
        jObj = new JSONObject(json);
    } catch (JSONException e) {
        Log.e("JSON Parser", "Error parsing data " + e.toString());
    }
	
	return jObj;
	
}


// 	Performs an HTTP GET request to the specified url with the specified parameters.
public JSONObject executeHttpGet(String url, ArrayList<NameValuePair> postParameters) throws IllegalStateException, IOException {  

	try {  
	
		//	Connect to server and get response
		HttpClient client = getHttpClient();
		String paramString = URLEncodedUtils.format(postParameters, "utf-8");
		url += "?" + paramString;
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		
		//	Process response
		HttpEntity entity = response.getEntity();
		is = entity.getContent();
		
	}
	
	catch (UnsupportedEncodingException e) {e.printStackTrace();}
	catch (ClientProtocolException e)  {e.printStackTrace();}
	catch (IOException e) {e.printStackTrace();}
	
	try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null){
			sb.append(line + "\n");
		}
		
		is.close();
		json = sb.toString();
		Log.d("json buffered string ", json);
		
	}	catch (Exception e) {
		Log.e("Buffer Error", "Error converting result " + e.toString());
	}
	
		//response.close();
	
	try {
        jObj = new JSONObject(json);
    } catch (JSONException e) {
        Log.e("JSON Parser", "Error parsing data " + e.toString());
    }
	
	return jObj;
	
}

}	// end of class
