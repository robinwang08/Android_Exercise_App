package edu.dartmouth.cs.myruns6;

import android.os.Bundle;
import android.preference.PreferenceFragment;

// Code examples can be found here:
// 
// http://developer.android.com/reference/android/preference/PreferenceFragment.html
// http://developer.android.com/reference/android/preference/PreferenceActivity.html

// The class is extended from PreferenceFragment
public class SettingsTabFragment extends PreferenceFragment {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        // Everything is set already in the xml, so no code here.
        // See res/xml/preference.xml for details
        addPreferencesFromResource(R.xml.preference);
    }
}
