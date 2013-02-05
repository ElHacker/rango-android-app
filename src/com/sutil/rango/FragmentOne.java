package com.sutil.rango;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
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

public class FragmentOne extends ListFragment {
	TextView showMessage;
	Context context;
	
	private final String TAG = "FragmentOne"; 
	
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
		makeFriendListRequest(session);
		
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
									profilePic, friend.getName(), "Mi amigo", 0);
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
	
	private class PeopleListElement extends BaseListElement {

	    public PeopleListElement(ProfilePictureView profilePictureView, String name, String description, int requestCode) {
	        super(profilePictureView,
	              name,
	              description,
	              requestCode);
	    }

	    @Override
	    protected View.OnClickListener getOnClickListener() {
	        return new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	                // Do nothing for now
	            }
	        };
	    }
	}
}
