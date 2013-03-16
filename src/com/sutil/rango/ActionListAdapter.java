package com.sutil.rango;

import java.util.List;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.Spinner;

import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * Used to adapt the a list's view
 * */

public class ActionListAdapter extends ArrayAdapter<BaseListElement>{
	
	protected static final String TAG = "ArrayAdapter";
	private List<BaseListElement> listElements;
	private int layoutId;
	private String my_fb_id;
	
	public ActionListAdapter(Context context, int resourceId,
			List<BaseListElement> listElements, int layoutId) {
		super(context, resourceId, listElements);
		this.listElements = listElements;
		this.layoutId = layoutId;
		// Set up as an observer for list item changes to
		// refresh the view
		for (int i = 0; i < listElements.size(); i++) {
			listElements.get(i).setAdapter(this);
		}
		// Get the facebook id of the current uer
		SharedPreferences settings = context.getSharedPreferences("MyUserInfo", 0);
		my_fb_id = settings.getString("my_fb_id", "");
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(this.layoutId, null);
        }

        BaseListElement listElement = listElements.get(position);
        if (listElement != null) {
            view.setOnClickListener(listElement.getOnClickListener());
            ProfilePictureView profilePic = (ProfilePictureView) view.findViewById(R.id.icon);
            TextView text1 = (TextView) view.findViewById(R.id.text1);
            TextView text2 = (TextView) view.findViewById(R.id.text2);
            Button confirm_friend_request = (Button) view.findViewById(R.id.confirm_friend_request);
            Button decline_friend_request = (Button) view.findViewById(R.id.decline_friend_request);
            if (profilePic != null) {
                profilePic.setProfileId(listElement.getProfilePictureView().getProfileId());
            }
            if (text1 != null) {
                text1.setText(listElement.getText1());
            }
            if (text2 != null) {
                text2.setText(listElement.getText2());
            }
            // TODO: REFACTOR final variables
            // Get the facebook profile id
        	final String profile_id = profilePic.getProfileId();
        	final int position_to_remove = position;
            if (confirm_friend_request != null) {
            	confirm_friend_request.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View view) {
    					RestClient.post_friend_request(my_fb_id, profile_id);
    					// remove the list element once confirmed
    					listElements.remove(position_to_remove);
    					notifyDataSetChanged();			// update the view
    				}
    			});
            }
            
            if (decline_friend_request != null) {
            	decline_friend_request.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						RestClient.delete_user_friend(my_fb_id, profile_id);
						// remove the list element once declined
						listElements.remove(position_to_remove);
    					notifyDataSetChanged();			// update the view
					}
            	});
            }
        }
        return view;
    }

	private Activity getActivity() {
		Activity activity = (Activity) getContext();
		return activity;
	}
	
}