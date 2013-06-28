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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An AsyncTask implementation for performing DELETEs on the REST API.
 */
public class DeleteTask extends AsyncTask<String, String, String>{
	private static String TAG = "DeleteTask";
    private String mRestUrl;
    private RestTaskCallback mCallback;

    /**
     * Creates a new instance of DeleteTask with the specified URL, callback, and
     * request body.
     * 
     * @param restUrl The URL for the REST API.
     * @param callback The callback to be invoked when the HTTP request
     *            completes.
     * 
     */
    public DeleteTask(String restUrl, RestTaskCallback callback){
        this.mRestUrl = restUrl;
        this.mCallback = callback;
    }
    
    @Override
    protected void onPreExecute() {
    	mCallback.preTaskExecute();
    }

    @Override
    protected String doInBackground(String... params) {
    	String response = null;
        // Use HTTP client API's to do the DELETE
    	response = httpDeleteRequest(mRestUrl);
        // Return response.
    	return response;
    }

    @Override
    protected void onPostExecute(String result) {
        mCallback.onTaskComplete(result);
        super.onPostExecute(result);
    }

    /*
     * Create a http delete request from a url
     * returns a json formatted string
     */
    public static String httpDeleteRequest(String url) {
    	DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(url);
		String jsonFormatted = "";
		try { 
			HttpResponse httpResponse = httpClient.execute(httpDelete);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();
		
			// Get the json from the input stream
			jsonFormatted = HttpHelpers.convertStreamToString(inputStream);
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
		return jsonFormatted;
    }
}
