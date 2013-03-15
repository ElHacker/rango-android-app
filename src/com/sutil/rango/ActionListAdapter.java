package com.sutil.rango;

import java.util.List;

import org.holoeverywhere.widget.Spinner;

import com.facebook.widget.ProfilePictureView;
import com.sutil.rango.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * Used to adapt the a list's view
 * */

public class ActionListAdapter extends ArrayAdapter<BaseListElement>{
	
	private List<BaseListElement> listElements;
	private int layoutId;
	
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
            Spinner spinner = (Spinner) view.findViewById(R.id.friend_requests_spin);
            if (profilePic != null) {
                profilePic.setProfileId(listElement.getProfilePictureView().getProfileId());
            }
            if (text1 != null) {
                text1.setText(listElement.getText1());
            }
            if (text2 != null) {
                text2.setText(listElement.getText2());
            }
            if (spinner != null) {
            	// Create an ArrayAdapter using the string array and a default spinner layout
            	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), 
            				R.array.friend_request_options, android.R.layout.simple_spinner_item);
            	// Specify the layout to use when the list of choices appears
            	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            	// Apply the adapter to the spinner
            	spinner.setAdapter(adapter);
            }
        }
        return view;
    }

	private Activity getActivity() {
		Activity activity = (Activity) getContext();
		return activity;
	}
	
}
