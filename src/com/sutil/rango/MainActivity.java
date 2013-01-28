package com.sutil.rango;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.example.holotest.R;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

public class MainActivity extends Activity {
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	TextView tabCenter;
	TextView tabText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	
	    mViewPager = new ViewPager(this);
	    mViewPager.setId(R.id.pager);
	    setContentView(mViewPager);
	    
	    // Create action bar
	    ActionBar bar = getSupportActionBar();
	    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
	    bar.setTitle("Rango");
	    
	
	    mTabsAdapter = new TabsAdapter(this, mViewPager);
	    
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_one_text),FragmentOne.class, null);
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_two_text), FragmentTwo.class, null);
	    mTabsAdapter.addTab(bar.newTab().setText(R.string.tab_three_text), FragmentThree.class, null);
	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
}