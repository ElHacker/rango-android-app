package com.sutil.rango.libs.restapi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RestfulClient {
	
	private static final String TAG = "RestfulClient";
	
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
	
	/* 
     * Get the user's friend list from the server using the REST API
     */
	public static void getUserFriends(String userId, final GetResponseCallback<JSONArray> callback) {
		String restUrl = rango_api_host + String.format(rango_api_paths.get("get_user_friends"), userId);
		new GetTask(restUrl, new RestTaskCallback () {
			
			@Override
			public void preTaskExecute() {
				callback.onPreGet();
			}
			
			@Override
			public void onTaskComplete(String response) {
				JSONArray friends = null;
				try {
					friends = new JSONArray(response);
					callback.onDataReceived(friends);
				} catch (JSONException e) {
					Log.e(TAG, "" + e.getMessage());
					e.printStackTrace();
				}
			}
		}).execute();
	}
	
	/* 
     * Get the a user's info from the server using the REST API
     */
	public static void getUser(String userId, final GetResponseCallback<JSONObject> callback) {
		String restUrl = rango_api_host + String.format(rango_api_paths.get("get_user"), userId);
		new GetTask(restUrl, new RestTaskCallback () {
			
			@Override
			public void preTaskExecute() {
				callback.onPreGet();
			}
			
			@Override
			public void onTaskComplete(String response) {
				JSONObject user = null;
				try {
					user = new JSONObject(response);
					callback.onDataReceived(user);
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}
		}).execute();
	}
	
	/*
     * Gets a user's friend requests from the server using the REST API
     * userId : the facebook id of the user to get the friend requests
     */
	public static void getFriendRequests(String userId, final GetResponseCallback<JSONArray> callback) {
		String restUrl = rango_api_host + String.format(rango_api_paths.get("get_user_friends_requests"), userId);
		new GetTask(restUrl, new RestTaskCallback() {
			
			@Override
			public void preTaskExecute() {
				callback.onPreGet();
			}
			
			@Override
			public void onTaskComplete(String response) {
				JSONArray friendRequests = null;
				try {
					friendRequests = new JSONArray(response);
					callback.onDataReceived(friendRequests);
				} catch(JSONException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}
		}).execute();
	}
	
	 /*
     * Post a new user's information, the database will create a new user if not existent
     */
	public static void postUser(String fbId, String email, String firstName, String lastName, final PostCallback callback) {
		String restUrl = rango_api_host + rango_api_paths.get("post_users");
		try {
			JSONObject user = new JSONObject();
			user.put("fb_id", fbId);
			user.put("email", email);
	    	user.put("first_name", firstName);
	    	user.put("last_name", lastName);
	    	List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
	    	Log.d(TAG, user.toString());
	    	requestBody.add(new BasicNameValuePair("user", user.toString()));
	    	new PostTask(restUrl, requestBody, new RestTaskCallback() {
	    		
	    		@Override
	    		public void preTaskExecute() {
	    			callback.onPrePost();
	    		}
				
				@Override
				public void onTaskComplete(String response) {
					Log.d(TAG, "Create new user response: " + response);
					callback.onPostSuccess(response);					
				}
			}).execute();
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
     * Post the application gcm id to the server using the REST API
     */
	public static void postUserGcmId(String userId, String gcmId, final PostCallback callback) {
		String restUrl = rango_api_host + String.format(rango_api_paths.get("post_users_gcm_ids"), userId);
		// Create the object to post
		List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
		requestBody.add(new BasicNameValuePair("user_gcm_id", gcmId));
		new PostTask(restUrl, requestBody, new RestTaskCallback() {
			
			@Override
			public void preTaskExecute() {
				callback.onPrePost();
			}
			
			@Override
			public void onTaskComplete(String response) {
		    	Log.d(TAG, "GCM ID response: " + response);
				callback.onPostSuccess(response);
			}
		}).execute();
	}
	
	 /*
     * Post friend requests
     * the server expects a json object to be posted as follows
     * user = {
     * 	fb_id = "12345678",
     * }
     */
	public static void postFriendRequest(String userId, String friendId, final PostCallback callback) {
		String restUrl = rango_api_host + String.format(rango_api_paths.get("post_user_friends_requests"), userId);
    	try {
    		// Create the json object to post
    		JSONObject friend = new JSONObject();
			friend.put("fb_id", friendId);
			List<NameValuePair> requestBody = new ArrayList<NameValuePair>();
			requestBody.add(new BasicNameValuePair("user", friend.toString()));
			new PostTask(restUrl, requestBody, new RestTaskCallback() {
				@Override
				public void preTaskExecute() {
					callback.onPrePost();
				}
				
				@Override
				public void onTaskComplete(String response) {
					Log.d(TAG, "Friend request response: " + response);
					callback.onPostSuccess(response);
				}
			}).execute();
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
     * Delete friends
     */
	public static void deleteUserFriend(String userId, String friendId, final DeleteCallback callback) {
		String restUrl = rango_api_host + String.format(rango_api_paths.get("delete_user_friends"), userId, friendId);
		new DeleteTask(restUrl, new RestTaskCallback() {
			
			@Override
			public void preTaskExecute() {
				callback.onPreDelete();
			}
			
			@Override
			public void onTaskComplete(String response) {
		    	Log.d(TAG, "Delete Friend response: " + response);
				callback.onDeleteSuccess(response);
			}
		}).execute();
	} 
}