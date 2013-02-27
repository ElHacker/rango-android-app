package com.sutil.rango;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class FriendsListFragment extends ListFragment {
	TextView showMessage;
	Context context;
	
	private final String TAG = "FriendsListFragment"; 
	
	private ListView listView;
	private List<BaseListElement> listElements;
	
	// TODO: CHECK IF IS POSSIBLE TO REMOVE FROM GLOBAL
	private String my_fb_id = "";
	
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
	    View view = inflater.inflate(R.layout.fragment_one, container, false);
	    
	    listView = (ListView) view.findViewById(android.R.id.list);
	    
		// Set up the list view items, based on a list of
		// BaseListElement items
		listElements = new ArrayList<BaseListElement>();
		// Add an item for the friend picker
		// listElements.add(new PeopleListElement(0));

		
		// Check for an open session
		Session session = Session.getActiveSession();
		// get connected user's info
		makeFacebookMeRequest(session);		
		
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
	
	// Make an API call to get user data and define a
	private void makeFacebookMeRequest(final Session session) { 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            // If the response is successful
	            if (session == Session.getActiveSession()) {
	                if (user != null) {
	                	my_fb_id = user.getId();
	                    // Get the user's friend list from rango servers
	                	makeFriendListRequest(my_fb_id);
	                }
	            }
	            if (response.getError() != null) {
	                // Handle errors, will do so later.
	            }
	        }
	    });
	    request.executeAsync();
	}
	
	// Create the friend list pulling the data from Rango server
	private void makeFriendListRequest(String user_id) { 
		try {
			JSONArray json_friends = RestClient.get_user_friends(user_id);
			for(int i = 0; i < json_friends.length(); i++) {
				JSONObject friend = json_friends.getJSONObject(i);
				ProfilePictureView profilePic = new ProfilePictureView(context);
				profilePic.setCropped(true);
				profilePic.setProfileId(friend.getString("fb_id"));
				String friend_full_name = friend.getString("first_name") + " " + friend.getString("last_name");
				// Create a list element with profile picture, name and description
				PeopleListElement peopleListElement = new PeopleListElement(
						profilePic, friend_full_name, "Mi amigo");
				listElements.add(peopleListElement);
			}
			// Set the list view adapter
			listView.setAdapter(new ActionListAdapter(getActivity(), 
						android.R.id.list, listElements));
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	// Create the friend list pulling the data from Facebook
	private void makeFriendListRequest(final Session session) {
		Request request = Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
			
			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				if (session == Session.getActiveSession()) {
					if (users != null) {
						for (int i = 0; i < users.size(); i++) {
							GraphUser friend = users.get(i);
							ProfilePictureView profilePic = new ProfilePictureView(context);
							profilePic.setCropped(true);
							profilePic.setProfileId(friend.getId());
							PeopleListElement peopleListElement = new PeopleListElement(
									profilePic, friend.getName(), "Mi amigo");
							listElements.add(peopleListElement);
						}
						// Set the list view adapter
						listView.setAdapter(new ActionListAdapter(getActivity(), 
									android.R.id.list, listElements));
					}else {
						Log.e(TAG, "Error retrieving friends");
					} 
				}
			}
		});
		request.executeAsync();
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
	    protected View.OnClickListener getOnClickListener() {
	        return new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	Log.d(TAG, "ID: " + getProfilePictureView().getProfileId());
	                // Get the fb_id of the clicked element of list
	            	String target_id = getProfilePictureView().getProfileId();
	            	// Create an intent to start the walkie talkie activity
	            	Intent intent = new Intent(context, WalkieTalkieActivity.class);
	            	// Data to send to activity 
	            	Bundle bundle = new Bundle();
	            	bundle.putString("my_id", my_fb_id);	// Set the my_fb_id
	            	bundle.putString("target_id", target_id);	// Set the target_id
	            	intent.putExtras(bundle);	// Set the data to the intent
	            	startActivity(intent);		// Start the walkie talkie activity
	            }
	        };
	    }
	}
}
