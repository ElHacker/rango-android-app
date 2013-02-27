package com.sutil.rango;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

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
		rango_api_paths.put("post_user_friends", "users/%s/friends/requests.json");	// Pass the :fb_id of the user
		rango_api_paths.put("delete_user_friends", "users/%s/friends/%s.json");	// Pass the :fb_id of the user and the :fb_id_friend
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
     * Get the user's friend list from the server using the REST API
     */
    public static JSONArray get_user_friends(String user_id) {
    	JSONArray json_array = null;
    	try {
    		String url = rango_api_host + String.format(rango_api_paths.get("get_user_friends"), user_id);
    		String json_string = http_get_request(url);
			// Parse the string to JSON array
			json_array = new JSONArray(json_string);
    	} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    	return json_array;
    }
}