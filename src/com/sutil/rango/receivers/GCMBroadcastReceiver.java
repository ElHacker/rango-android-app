package com.sutil.rango.receivers;

import org.holoeverywhere.app.Activity;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sutil.rango.IncomingCallActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMBroadcastReceiver extends BroadcastReceiver {
	
	static final String TAG = "GCMBroadcastReceiver";
	Context ctx;

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		ctx = context;
		String messageType = gcm.getMessageType(intent);
		String intentExtrasMsg = intent.getExtras().toString();
		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			Log.d(TAG, "Send error: " + intentExtrasMsg);
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
			Log.d(TAG, "Deleted messages on server: " + intentExtrasMsg);
		} else {
			Log.d(TAG, "Message Received: " + intentExtrasMsg);
			Bundle intentExtras = intent.getExtras();
			Intent incominCallIntent = new Intent(context, IncomingCallActivity.class);
			incominCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			incominCallIntent.putExtras(intentExtras);
			ctx.startActivity(incominCallIntent);
		}
		setResultCode(Activity.RESULT_OK);
	}
	
}
