package com.sutil.rango;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService{
	
	private static final String TAG = "GCMIntentService"; 
	
	/*
	 * Called when the device tries to register or unregister, but GCM returned an error.
	 * Typically, there is nothing to be done other than evaluating 
	 * the error (returned by errorId) and trying to fix the problem.
	 * */
	@Override
	protected void onError(Context context, String regId) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Called when your server sends a message to GCM, 
	 * and GCM delivers it to the device. If the message has a payload, 
	 * its contents are available as extras in the intent.
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, bundle.toString());
		CharSequence title = bundle.getCharSequence("title");
		CharSequence message = bundle.getCharSequence("message");
		NotificationManager notificationManager;
		notificationManager = (NotificationManager) context.getSystemService("notification");
		Notification notification;
		int icon = R.drawable.notification;
		// Text to display in the status bar when the notification is launched
		String tickerText = "Notification from rango";
		// The extended status bar orders notification in time order
		long when = System.currentTimeMillis();
		
		notification = new Notification(icon,tickerText,when);
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		
		notification.setLatestEventInfo(context, title, message, pendingIntent);
		notification.defaults = Notification.DEFAULT_ALL;
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;		// Remove when clicked
//							| Notification.FLAG_INSISTENT;		// Repeat notification until canceled
		
		notificationManager.notify(1010, notification);
	}

	/*
	 * Called after a registration intent is received, passes the registration ID assigned by GCM to that 
	 * device/application pair as parameter. Typically, you should send the 
	 * regid to your server so it can use it to send messages to this device.
	 * */
	@Override
	protected void onRegistered(Context context, String regId) {
		RestClient.post_user_gcm_id("712276985", regId);
	}

	/*
	 * Called after the device has been unregistered from GCM. 
	 * Typically, you should send the regid to the server so it unregisters the device.
	 */
	@Override
	protected void onUnregistered(Context context, String regId) {
		// TODO Auto-generated method stub
		
	}

}
