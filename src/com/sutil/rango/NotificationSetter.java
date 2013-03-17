package com.sutil.rango;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationSetter {
	private NotificationManager notification_manager;
	private Context context;
	
	public NotificationSetter(Context context) {
		this.context = context;
		notification_manager = (NotificationManager) context.getSystemService("notification");
	}
	
	public void clearNotification(int notification_id) {
		notification_manager.cancel(notification_id);
	}
	
	/*
	 * setNotification: triggers a notification in the status bar
	 * @notification_intent: has the intent to be called when the notification is clicked
	 * @title: Text to display in the notification
	 * @ticker_text: Text to display in the status bar when the notification is launched
	 * @message: Text to display as the message of the notification
	 * @drawable_icon: id of the drawable to display as the notification icon
	 * returns the id of the created notification
	 */
	public int setNotification(Intent notification_intent, String title, String ticker_text, String message, int drawable_icon) {
		// The extended status bar orders notification in time order
		long when = System.currentTimeMillis();
		int notification_id = (int) System.currentTimeMillis();
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notification_intent, 0);
		
		
		Notification notification = new Notification(drawable_icon, ticker_text, when);
		notification.defaults = Notification.DEFAULT_ALL;
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;	// Remove when clicked
		notification.setLatestEventInfo(context, title, message , pendingIntent);
		
		notification_manager.notify(notification_id, notification);
		return notification_id;
	}
}
