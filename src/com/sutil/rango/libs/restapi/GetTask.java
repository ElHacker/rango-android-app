package com.sutil.rango.libs.restapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An AsyncTask implementation for performing GETs on the REST API.
 */
public class GetTask extends AsyncTask<String, String, String>{

	private static final String TAG = "GetTask";
    private String mRestUrl;
    private RestTaskCallback mCallback;

    /**
     * Creates a new instance of GetTask with the specified URL and callback.
     * 
     * @param restUrl The URL for the REST API.
     * @param callback The callback to be invoked when the HTTP request
     *            completes.
     * 
     */
    public GetTask(String restUrl, RestTaskCallback callback){
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
        //Use HTTP Client APIs to make the call.
        response = httpGetRequest(mRestUrl);
        //Return the HTTP Response body here.
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        mCallback.onTaskComplete(result);
        super.onPostExecute(result);
    }
    
    /*
     * Create a http get request from a url
     * returns a json formatted string
     * */
    private static String httpGetRequest(String url) {
    	DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		String jsonFormatted = "";
		try { 
			HttpResponse httpResponseGet = httpClient.execute(httpGet);
			HttpEntity httpEntityGet = httpResponseGet.getEntity();
			InputStream inputStreamGet = httpEntityGet.getContent();
		
			// Get the json from the input stream
			jsonFormatted = HttpHelpers.convertStreamToString(inputStreamGet);
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