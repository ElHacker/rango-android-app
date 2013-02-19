package com.sutil.rango;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.util.Log;
 
public class RestClient {
	
	private static final String TAG = "RestClient"; 
 
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
 
    /* This is a test function which will connects to a given
     * rest service and prints it's response to Android Log
     */
    public static void connect(String url) {
    	try {
    		DefaultHttpClient http_client = new DefaultHttpClient();
    		HttpGet http_get = new HttpGet(url);
    		
    		HttpResponse http_response_get = http_client.execute(http_get);
    		HttpEntity http_entity_get = http_response_get.getEntity();
    		InputStream input_stream_get = http_entity_get.getContent();
			
    		// Get the json from the input stream
			String json_string = convertStreamToString(input_stream_get);
			// Parse the string to JSON object
			JSONArray json_array = new JSONArray(json_string);
			Log.d(TAG, json_array.toString());
			
			// RestClient.connect("http://192.168.0.17:3000/users/12345678/friends.json");
			
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    	
    }
 
}