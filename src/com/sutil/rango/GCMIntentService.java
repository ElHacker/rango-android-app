package com.sutil.rango;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService{
	
	private static final String SENDER_ID = "108747417910";
	
	public GCMIntentService(String senderId) {
		super(senderId);
		Log.d("GCMIntentService", senderId);
	}
	
	public GCMIntentService() {
		super(SENDER_ID);
		Log.d("GCMIntentService", SENDER_ID);
	}
	
	private static final String TAG = "GCMIntentService"; 
	
	/*
	 * Called when the device tries to register or unregister, but GCM returned an error.
	 * Typically, there is nothing to be done other than evaluating 
	 * the error (returned by errorId) and trying to fix the problem.
	 * */
	@Override
	protected void onError(Context context, String gcm_reg_id) {
		Log.e(TAG, "GCM ERROR: " + gcm_reg_id);		
	}

	/*
	 * Called when your server sends a message to GCM, 
	 * and GCM delivers it to the device. If the message has a payload, 
	 * its contents are available as extras in the intent.
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d(TAG, "NEW MESSAGE RECEIVED");
		createNotification(context, intent);
	}

	/*
	 * Called after a registration intent is received, passes the registration ID assigned by GCM to that 
	 * device/application pair as parameter. Typically, you should send the 
	 * regid to your server so it can use it to send messages to this device.
	 * */
	@Override
	protected void onRegistered(Context context, String gcm_reg_id) {
		// Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	    	SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
 			String my_fb_id = settings.getString("my_fb_id", "");
 			// Set the id for posting the reg id
        	RestClient.post_user_gcm_id(my_fb_id, gcm_reg_id);
	    }
	}

	/*
	 * Called after the device has been unregistered from GCM. 
	 * Typically, you should send the regid to the server so it unregisters the device.
	 */
	@Override
	protected void onUnregistered(Context context, String gcm_reg_id) {
		Log.d(TAG, "Unregistered: " + gcm_reg_id);		
	}
	
	private void createNotification(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, bundle.toString());
		String title = bundle.getString("title");
		String source_fb_id = bundle.getString("from_fb_id");
		// Get user information
		JSONObject source_user = RestClient.get_user(source_fb_id);
		Log.d(TAG, source_user.toString());
		String message;
		Intent notificationIntent;
		try {
			message = "Incomming call from: " + source_user.getString("first_name");
			notificationIntent = new Intent(context, WalkieTalkieActivity.class);
			Bundle notificationBundle = new Bundle();
			SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
 			String my_fb_id = settings.getString("my_fb_id", "");
			notificationBundle.putString("my_id", my_fb_id);	// Set the my_fb_id
			notificationBundle.putString("target_id", source_user.getString("fb_id"));	// Set the target_id from source
			String source_full_name = source_user.getString("first_name") + " " + source_user.getString("last_name"); 
			notificationBundle.putString("target_name", source_full_name);
			// TODO: get description from server
			notificationBundle.putString("target_desc", "Mi amigo");//source_user.getString("description"));
			notificationIntent.putExtras(notificationBundle);
			
		} catch (JSONException e) {
			// If exception get the default message
			message = bundle.getString("message");
			// default intent
			notificationIntent = intent;
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		NotificationManager notificationManager;
		notificationManager = (NotificationManager) context.getSystemService("notification");
		Notification notification;
		int icon = R.drawable.notification;
		// Text to display in the status bar when the notification is launched
		String tickerText = "Notification from rango";
		// The extended status bar orders notification in time order
		long when = System.currentTimeMillis();
		
		notification = new Notification(icon,tickerText,when);
		
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		
		notification.setLatestEventInfo(context, title, message , pendingIntent);
		notification.defaults = Notification.DEFAULT_ALL;
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;		// Remove when clicked
//							| Notification.FLAG_INSISTENT;		// Repeat notification until canceled
		// TODO: change the notification id to a variable one
		notificationManager.notify(1010, notification);
	}
	
}
