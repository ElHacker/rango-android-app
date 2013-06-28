package com.sutil.rango.libs.restapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An AsyncTask implementation for performing POSTs on the REST API.
 */
public class PostTask extends AsyncTask<String, String, String>{
	private static String TAG = "PostTask";
    private String mRestUrl;
    private RestTaskCallback mCallback;
    private List<NameValuePair> mRequestBody;

    /**
     * Creates a new instance of PostTask with the specified URL, callback, and
     * request body.
     * 
     * @param restUrl The URL for the REST API.
     * @param callback The callback to be invoked when the HTTP request
     *            completes.
     * @param requestBody The body of the POST request.
     * 
     */
    public PostTask(String restUrl, List<NameValuePair> requestBody, RestTaskCallback callback){
        this.mRestUrl = restUrl;
        this.mRequestBody = requestBody;
        this.mCallback = callback;
    }
    
    @Override
    protected void onPreExecute() {
    	mCallback.preTaskExecute();
    }

    @Override
    protected String doInBackground(String... params) {
    	String response = null;
        //Use HTTP client API's to do the POST
    	response = httpPostRequest(mRestUrl, mRequestBody);
        //Return response.
    	return response;
    }

    @Override
    protected void onPostExecute(String result) {
        mCallback.onTaskComplete(result);
        super.onPostExecute(result);
    }
    
    /*
     * Create a http post request from a url
     * returns a json formatted string
     * */
    public static String httpPostRequest(String url, List<NameValuePair> nameValuePairs) {
    	DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		String jsonFormatted = "";
		try { 
			// Set the values to be sent by the post
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			HttpResponse httpResponsePost = httpClient.execute(httpPost);
			HttpEntity httpEntityPost = httpResponsePost.getEntity();
			InputStream inputStreamPost = httpEntityPost.getContent();
		
			// Get the json from the input stream
			jsonFormatted = HttpHelpers.convertStreamToString(inputStreamPost);
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
		return jsonFormatted;
    }
}
