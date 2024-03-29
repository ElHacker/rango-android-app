package com.sutil.rango.fragments;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.R;
import com.sutil.rango.WalkieTalkieActivity;
import com.sutil.rango.adapters.ActionListAdapter;
import com.sutil.rango.libs.BaseListElement;
import com.sutil.rango.libs.RestClient;
import com.sutil.rango.libs.restapi.GetResponseCallback;
import com.sutil.rango.libs.restapi.RestfulClient;

public class FriendsListFragment extends ListFragment {
	TextView showMessage;
	Context context;
	
	private final String TAG = "FriendsListFragment"; 
	
	private ListView listView;
	private ProgressBar loadingProgress;
	private Toast networkErrorToast; 
	
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
		
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.friends_list_fragment, container, false);
	    
	    listView = (ListView) view.findViewById(android.R.id.list);
	    loadingProgress = (ProgressBar) view.findViewById(R.id.loadingProgress);
	    networkErrorToast = Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT);
	    
		// Set up the list view items, based on a list of
		// BaseListElement items
		listElements = new ArrayList<BaseListElement>();
		
		// Get my friends list
		SharedPreferences settings = getSharedPreferences("MyUserInfo", 0);
		String currentFbId = settings.getString("my_fb_id", "");
		loadFriendList(currentFbId);
		
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
	
	// Calls the restful client to load the current user's friend list
	private void loadFriendList(String userId) {
			
			RestfulClient.getUserFriends(userId, new GetResponseCallback<JSONArray>() {
				@Override
				public void onPreGet() {
					loadingProgress.setVisibility(View.VISIBLE);						
				}
				
				@Override
				public void onDataReceived(JSONArray friends) {
					try {
						for(int i = 0; i < friends.length(); i++) {
							JSONObject friend = friends.getJSONObject(i);
							ProfilePictureView profilePic = new ProfilePictureView(context);
							profilePic.setCropped(true);
							profilePic.setProfileId(friend.getString("fb_id"));
							String friend_full_name = friend.getString("first_name") + " " + friend.getString("last_name");
							// Create a list element with profile picture, name and description
							PeopleListElement peopleListElement = new PeopleListElement(
									profilePic, friend_full_name, "");
							listElements.add(peopleListElement);
						}
						
						loadingProgress.setVisibility(View.GONE);
						// Set the list view adapter
						listView.setAdapter(new ActionListAdapter(getActivity(),
									android.R.id.list, listElements, R.layout.friends_list_item));
						
					} catch (Exception e) {
						Log.e(TAG, "Exception: " + e.getMessage());
						e.printStackTrace();
						// Network error
						networkErrorToast.show();
					}
				}
	
			});
		}
		

	
	// Represents an element of the friends list, supports profile picture
	// name, and a brief description
	private class PeopleListElement extends BaseListElement {

	    public PeopleListElement(ProfilePictureView profilePictureView, String name, String description) {
	        super(profilePictureView,
	              name,
	              description);
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
