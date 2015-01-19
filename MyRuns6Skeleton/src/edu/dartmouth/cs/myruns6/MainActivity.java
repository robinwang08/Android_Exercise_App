/**
 * ActivityMainPortal.java
 * 
 * Created by Xiaochao Yang on Dec 9, 2011 10:16:59 PM
 * 
 */

package edu.dartmouth.cs.myruns6;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;


//Code and examples are available on the Android developer site:
//http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/FragmentTabs.html
//http://developer.android.com/guide/topics/ui/actionbar.html#Tabs
//
//If you get stacked tab views after rotating the phone, please read here:
//http://stackoverflow.com/questions/9819404/fragment-handling-screen-orientation-with-tabs-in-actionbar



// The main activity of the application. 
// Three tab fragments reside in this activity.
public class MainActivity extends Activity {

	private static final String TAB_INDEX_KEY = "tab_index"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String str;

		// Notice that setContentView() is not used, because we use the root
		// android.R.id.content as the container for each fragment

		// setup action bar for tabs
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// actionBar.setDisplayShowTitleEnabled(false);
		// actionBar.setDisplayShowHomeEnabled(false);

		// 1st tab "Start"
		// Get the resource string for the title of the tab
		str = getString(R.string.ui_main_tab_start_title);
		// Create a new tab with title name and 
		// specify which fragment to connect to
		Tab tab = actionBar
				.newTab()
				.setText(str)
				.setTabListener(
						new TabListener<StartTabFragment>(this, str,
								StartTabFragment.class));
		// Add the tab.
		actionBar.addTab(tab);


		// 2nd tab "History". Setting same as the 1st tab
		str = getString(R.string.ui_main_tab_history_title);
		tab = actionBar
				.newTab()
				.setText(str)
				.setTabListener(
						new TabListener<HistoryTabFragment>(this, str,
								HistoryTabFragment.class));
		actionBar.addTab(tab);

		// 3rd tab "History". Setting same as the 1st tab
		str = getString(R.string.ui_main_tab_settings_title);
		tab = actionBar
				.newTab()
				.setText(str)
				.setTabListener(
						new TabListener<SettingsTabFragment>(this, str,
								SettingsTabFragment.class));
		actionBar.addTab(tab);

		// Load the previously saved tab index before the activity goes into background 
		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(
					TAB_INDEX_KEY, 0));
		}

		//GCM registration
		// Make sure the device has the proper dependencies.
		// Your code.
		GCMRegistrar.checkDevice(this);
		
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
		// Your code.
			//GCMRegistrar.checkManifest(this);
		//final String regId = GCMRegistrar.getRegistrationId(this);
		String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			// Your code.
			//My Sender ID
			regId = "60149575056";
			GCMRegistrar.register(this, regId);
		} 
		else {
			Log.v("GCMRegistration", "Already registered");
		}

	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the tab index before the activity goes into background.
		// Referred by string key TAB_INDEX_KEY
		outState.putInt(TAB_INDEX_KEY, getActionBar().getSelectedNavigationIndex());
	}

	// Pretty standard stuff for TabListener.
	public static class TabListener<T extends Fragment> implements
	ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isDetached()) {
				FragmentTransaction ft = mActivity.getFragmentManager()
						.beginTransaction();
				ft.detach(mFragment);
				ft.commit();
			}
		}

		/* The following are each of the ActionBar.TabListener callbacks */
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

}
