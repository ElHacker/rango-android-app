package com.sutil.rango;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.ListFragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import com.example.holotest.R;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FragmentOne extends ListFragment {
	TextView showMessage;
	Context context;
	
	public static final String[] TITLES = new String[] { "Android", "iPhone",
	        "Ubuntu"};
	
	public static final String[] DESCRIPTIONS = new String[] { 
		"Google's mobile OS", 
		"Apple's mobile OS",
    	"Linux's mobile OS"};
	
	public static final Integer[] IMAGES = {
		R.drawable.ic_launcher,
		R.drawable.ic_launcher,
		R.drawable.ic_launcher
	};
	
	List<RowItem> rowItems;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		context = getActivity();
		
		rowItems = new ArrayList<RowItem>();
		for(int i = 0; i < TITLES.length; i++) {
			RowItem rowItem = new RowItem(IMAGES[i], TITLES[i], DESCRIPTIONS[i]);
			rowItems.add(rowItem);
		}
		context = getActivity();
		CustomListViewAdapter adapter = new CustomListViewAdapter(this.context,
				R.layout.list_item, rowItems);
		setListAdapter(adapter);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.fragment_one, container, false);
	    return  v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onListItemClick(ListView l, View view, int position,
			long id) {
		Log.d("FragmenOne", "CLICK");
		Toast toast = Toast.makeText(this.context,
				"Item " + (position + 1) + ": " + rowItems.get(position),
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
	}
}
