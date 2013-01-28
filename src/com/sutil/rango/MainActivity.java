package com.sutil.rango;

import java.util.ArrayList;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.example.holotest.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
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

public static class TabsAdapter extends FragmentPagerAdapter implements
        	ActionBar.TabListener, ViewPager.OnPageChangeListener {
	    private final Context mContext;
	    private final ActionBar mActionBar;
	    private final ViewPager mViewPager;
	    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	
	    static final class TabInfo {
	        private final Class<?> clss;
	        private final Bundle args;
	
	        TabInfo(Class<?> _class, Bundle _args) {
	            clss = _class;
	            args = _args;
	        }
	    }
	
	    public TabsAdapter(Activity activity, ViewPager pager) {
	        super(activity.getSupportFragmentManager());
	        mContext = activity;
	        mActionBar = activity.getSupportActionBar();
	        mViewPager = pager;
	        mViewPager.setAdapter(this);
	        mViewPager.setOnPageChangeListener(this);
	    }
	
	    public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
	        TabInfo info = new TabInfo(clss, args);
	        tab.setTag(info);
	        tab.setTabListener(this);
	        mTabs.add(info);
	        mActionBar.addTab(tab);
	        notifyDataSetChanged();
	    }
	
	    @Override
	    public int getCount() {
	        return mTabs.size();
	    }
	
	    @Override
	    public Fragment getItem(int position) {
	        TabInfo info = mTabs.get(position);
	        return Fragment.instantiate(mContext, info.clss.getName(),
	                info.args);
	    }
	
	    @Override
	    public void onPageScrolled(int position, float positionOffset,
	            int positionOffsetPixels) {
	    }
	
	    @Override
	    public void onPageSelected(int position) {
	        mActionBar.setSelectedNavigationItem(position);
	    }
	
	    @Override
	    public void onPageScrollStateChanged(int state) {
	    }
	
	    @Override
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        Object tag = tab.getTag();
	        for (int i = 0; i < mTabs.size(); i++) {
	            if (mTabs.get(i) == tag) {
	                mViewPager.setCurrentItem(i);
	            }
	        }
	    }
	
	    @Override
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	    }
	
	    @Override
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	    }
	}
}