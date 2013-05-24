package com.sutil.rango.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.R;
import com.sutil.rango.WalkieTalkieActivity;
import com.sutil.rango.adapters.ActionListAdapter;
import com.sutil.rango.lib.BaseListElement;
import com.sutil.rango.lib.DatabaseHandler;
import com.sutil.rango.models.Call;

public class CallsListFragment extends ListFragment {
	TextView showMessage;
	Context context;
	DatabaseHandler db;
	
	private final String TAG = "CallsListFragment"; 
	
	private ListView listView;
	private List<BaseListElement> listElements;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
		    new Session.StatusCallback() {
			    @Override
			    public void call(Session session, 
			            SessionState state, Exception exception) {
			        onSessionStateChange(session, state, exception);
		    }
		};
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		context = getActivity();
		// Create database handler
		db = new DatabaseHandler(context);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.calls_list_fragment, container, false);
	    listView = (ListView) view.findViewById(android.R.id.list);
		// Set up the list view items, based on a list of
		// BaseListElement items
		listElements = new ArrayList<BaseListElement>();
		createCallsLog();

	    return  view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.d(TAG, "Facebook Session Opened");
		}
	}
	
	// Get calls log from database
	private void createCallsLog() {
		List<Call> callsLog = db.getAllCalls();
		for (Call call: callsLog) {
			ProfilePictureView profilePic = new ProfilePictureView(context);
			profilePic.setCropped(true);
			profilePic.setProfileId(call.getFbId());
			String fullName = call.getFirstName() + " " + call.getLastName();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String dateTime = call.getDate().toString() + "\t" + sdf.format(call.getTime());
			CallListElement callListElement = new CallListElement(profilePic, fullName, dateTime);
			listElements.add(callListElement);
		}
		// Set the list view adapter
		listView.setAdapter(new ActionListAdapter(getActivity(),
					android.R.id.list, listElements, R.layout.calls_list_item));
		listView.refreshDrawableState();
	}
	
	// Represents an element of the callss list, supports profile picture
	// name, and dateTime
	private class CallListElement extends BaseListElement {

	    public CallListElement(ProfilePictureView profilePictureView, String name, String dateAndTime) {
	        super(profilePictureView,
	              name,
	              dateAndTime);
	    }

	    @Override
		public View.OnClickListener getOnClickListener() {
	        return new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	Log.d(TAG, "ID: " + getProfilePictureView().getProfileId());
	                // Get the fb_id of the clicked element of list
	            	String target_id = getProfilePictureView().getProfileId();
	            	// Get the name and description of the clicked element
	            	String target_name = getText1();
	            	// Create an intent to start the walkie talkie activity
	            	Intent intent = new Intent(context, WalkieTalkieActivity.class);
	            	// Data to send to activity 
	            	Bundle bundle = new Bundle();
	            	SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
	     			String my_fb_id = settings.getString("my_fb_id", "");
	            	bundle.putString("my_id", my_fb_id);	// Set the my_fb_id
	            	bundle.putString("target_id", target_id);	// Set the target_id
	            	bundle.putString("target_name", target_name);
	            	intent.putExtras(bundle);	// Set the data to the intent
	            	startActivity(intent);		// Start the walkie talkie activity
	            }
	        };
	    }
	}
}
