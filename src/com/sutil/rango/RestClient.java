package com.sutil.rango;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
 
public class RestClient {
	
	private static final String TAG = "RestClient";
	
	// The host url for the API
	private static String rango_api_host = "http://rangoapp.com/";
	// All the API paths
	// TODO: Might change the API later to ask for the paths available and 
	// create a dynamic map out of it that will make the changes of the API
	// Simpler without having to modify the android app.
	private static Hashtable<String, String> rango_api_paths;
	static {
		rango_api_paths  = new Hashtable<String, String>();
		// User resources
		rango_api_paths.put("get_users", "users.json");
		rango_api_paths.put("get_user", "users/%s.json");	// Pass the :fb_id of the user
		rango_api_paths.put("post_users", "users.json");
		rango_api_paths.put("put_users", "users/%s.json");	// Pass the :fb_id of the user
		rango_api_paths.put("get_users", "users.json");
		// User's friend sub resource
		rango_api_paths.put("get_user_friends", "users/%s/friends.json");	// Pass the :fb_id of the user
		rango_api_paths.put("get_user_friends_requests", "users/%s/friends/requests.json");	// Pass the :fb_id of the user
		rango_api_paths.put("post_user_friends_requests", "users/%s/friends/requests.json");	// Pass the :fb_id of the user
		rango_api_paths.put("delete_user_friends", "users/%s/friends/%s.json");	// Pass the :fb_id of the user and the :fb_id_friend
		// User's gcm sub resource
		rango_api_paths.put("post_users_gcm_ids", "users/%s/gcm_ids.json"); // Pass the :fb_id of the user
	}
 
    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder string_builder = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                string_builder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return string_builder.toString();
    }
    /*
     * Create a http get request from a url
     * returns a json formatted string
     * */
    public static String http_get_request(String url) {
    	DefaultHttpClient http_client = new DefaultHttpClient();
		HttpGet http_get = new HttpGet(url);
		String json_string = "";
		try { 
			HttpResponse http_response_get = http_client.execute(http_get);
			HttpEntity http_entity_get = http_response_get.getEntity();
			InputStream input_stream_get = http_entity_get.getContent();
		
			// Get the json from the input stream
			json_string = convertStreamToString(input_stream_get);
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return json_string;
    }
    
    /*
     * Create a http post request from a url
     * returns a json formatted string
     * */
    public static String http_post_request(String url, List<NameValuePair> nameValuePairs) {
    	DefaultHttpClient http_client = new DefaultHttpClient();
		HttpPost http_post = new HttpPost(url);
		String json_string = "";
		try { 
			// Set the values to be sent by the post
			http_post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			HttpResponse http_response_post = http_client.execute(http_post);
			HttpEntity http_entity_post = http_response_post.getEntity();
			InputStream input_stream_post = http_entity_post.getContent();
		
			// Get the json from the input stream
			json_string = convertStreamToString(input_stream_post);
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
	        Log.e(TAG, e.getMessage());
	        e.printStackTrace();
	    } catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} 
		return json_string;
    }
    
    
    /*
     * Create a http delete request from a url
     * returns a json formatted string
     */
    public static String http_delete_request(String url) {
    	DefaultHttpClient http_client = new DefaultHttpClient();
		HttpDelete http_delete = new HttpDelete(url);
		String json_string = "";
		try { 
			HttpResponse http_response_get = http_client.execute(http_delete);
			HttpEntity http_entity_get = http_response_get.getEntity();
			InputStream input_stream_get = http_entity_get.getContent();
		
			// Get the json from the input stream
			json_string = convertStreamToString(input_stream_get);
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		return json_string;
    }
 
    /* 
     * Get the user's friend list from the server using the REST API
     */
    public static JSONArray get_user_friends(String user_id) {
    	JSONArray friends = null;
    	try {
    		String url = rango_api_host + String.format(rango_api_paths.get("get_user_friends"), user_id);
    		String json_string = http_get_request(url);
			// Parse the string to JSON array
			friends = new JSONArray(json_string);
    	} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    	return friends;
    }
    
    /* 
     * Get the a user's info from the server using the REST API
     */
    public static JSONObject get_user(String user_id) {
    	JSONObject user = null;
    	try {
    		String url = rango_api_host + String.format(rango_api_paths.get("get_user"), user_id);
    		String json_string = http_get_request(url);
			// Parse the string to JSON array
			user = new JSONObject(json_string);
    	} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    	return user;
    }
    
    /*
     * Gets a user's friend requests from the server using the REST API
     * user_id : the facebook id of the user to get the friend requests  
     */
    public static JSONArray get_friend_requests(String user_id) {
    	JSONArray friend_requests = null;
    	try {
    		String url = rango_api_host + String.format(rango_api_paths.get("get_user_friends_requests"), user_id);
    		String json_string = http_get_request(url);
			// Parse the string to JSON array
			friend_requests = new JSONArray(json_string);
    	} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    	return friend_requests;
    }
    
    /*
     * Post a new user's information, the database will create a new user if not existent
     */
    public static void post_user(String fb_id, String email, String first_name, String last_name){
    	String url = rango_api_host + rango_api_paths.get("post_users");
    	JSONObject user = new JSONObject();
    	try {
			user.put("fb_id", fb_id);
			user.put("email", email);
	    	user.put("first_name", first_name);
	    	user.put("last_name", last_name);
	    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    	Log.d(TAG, user.toString());
	    	nameValuePairs.add(new BasicNameValuePair("user", user.toString()));
	    	String json_string = http_post_request(url, nameValuePairs);
	    	Log.d(TAG, "Create new user response: " + json_string);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    }
    
    /*
     * Post the application gcm id to the server using the REST API
     * */
    public static void post_user_gcm_id(String user_id, String gcm_id) {
    	String url = rango_api_host + String.format(rango_api_paths.get("post_users_gcm_ids"), user_id);
    	// Create the object to post
    	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("user_gcm_id", gcm_id));
    	String json_string = http_post_request(url, nameValuePairs);
    	Log.d(TAG, "GCM ID response: " + json_string);
    }
    
    /*
     * Post friend requests
     * the server expects a json object to be posted as follows
     * user = {
     * 	fb_id = "12345678",
     * }
     * */
    public static void post_friend_request(String user_id, String friend_id) {
    	String url = rango_api_host + String.format(rango_api_paths.get("post_user_friends_requests"), user_id);
    	// Create the json object to post
    	JSONObject friend = new JSONObject();
    	try {
			friend.put("fb_id", friend_id);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("user", friend.toString()));
			String json_string = http_post_request(url, nameValuePairs);
			Log.d(TAG, "Friend request response: " + json_string);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    }
    
    /*
     * Delete friends 
     */
    public static void delete_user_friend(String user_id, String friend_id) {
    	String url = rango_api_host + String.format(rango_api_paths.get("delete_user_friends"), user_id, friend_id);
    	String json_string = http_delete_request(url);
    	Log.d(TAG, "Delete Friend response: " + json_string);
    }
    
}