package com.sutil.rango.receivers;

import org.holoeverywhere.app.Activity;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sutil.rango.IncomingCallActivity;
import com.sutil.rango.R;
import com.sutil.rango.RequestsListActivity;
import com.sutil.rango.lib.NotificationSetter;

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
			String action  = intentExtras.getString("action");
			if (action != null) {
				if (action.equals("call")) {
					// Incoming call
					Intent incominCallIntent = new Intent(context, IncomingCallActivity.class);
					incominCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					incominCallIntent.putExtras(intentExtras);
					ctx.startActivity(incominCallIntent);	
				} else if (action.equals("invite")) {
					// Friendship request
					Intent notificationIntent = new Intent(context, RequestsListActivity.class);
					int icon = R.drawable.notification;
					String title = intentExtras.getString("title");
					String message = intentExtras.getString("message");
					String ticker_text = "Invitación de amistad";
					NotificationSetter notification_setter = new NotificationSetter(context);
					notification_setter.setNotification(notificationIntent, title, ticker_text, message, icon);
				}
			}
		}
		setResultCode(Activity.RESULT_OK);
	}
	
}
