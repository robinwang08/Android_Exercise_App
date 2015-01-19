/**
 * Robin Wang
 * 
 * CS 65 Lab 3
 * 
 * ManualInputActivity.java
 * 
 * Created by Xiaochao Yang on Sep 13, 2011 10:38:16 PM
 * 
 */

package edu.dartmouth.cs.myruns6;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

// Extra credit: display distance unit (kilometers or miles) based on the preference 

public class ManualInputActivity extends ListActivity {

	// Exercise entry
	public ExerciseEntryHelper mEntry;

	public static final int LIST_ITEM_ID_DATE = 0;
	public static final int LIST_ITEM_ID_TIME = 1;
	public static final int LIST_ITEM_ID_DURATION = 2;
	public static final int LIST_ITEM_ID_DISTANCE = 3;
	public static final int LIST_ITEM_ID_CALORIES = 4;
	public static final int LIST_ITEM_ID_HEARTRATE = 5;
	public static final int LIST_ITEM_ID_COMMENT = 6;

	// skeleton
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Setting the UI layout
		setContentView(R.layout.manualinput);

		// Initialize the ExerciseEntryHelper()
		mEntry = new ExerciseEntryHelper();

		// Get the extra information passed from the launching activity
		Bundle extras = null;
		Intent i = getIntent();
		extras = i.getExtras();

		// set InputType and ActivityType from the extras.
		
		mEntry.setInputType(extras.getInt(Globals.KEY_INPUT_TYPE));
		mEntry.setActivityType(extras.getInt(Globals.KEY_ACTIVITY_TYPE));
	}

	// "Save" button is clicked
	public void onSaveClicked(View v) {

		// Insert the exercise entry into database
		
		long entryNo = mEntry.insertToDB(this);
		
		// Pop up a toast
		String strEntryNo=String.valueOf(entryNo);
		String display = ("Entry #" + strEntryNo + " saved.");
		Toast.makeText(getApplicationContext(),display,Toast.LENGTH_SHORT).show();
		
		// Close the activity
		finish();
		
	}

	// "Cancel" button is clicked
	public void onCancelClicked(View v) {
		// Pop up a toast, discard the input and close the activity directly
		Toast.makeText(getApplicationContext(),
				getString(R.string.ui_entry_cancel),
				Toast.LENGTH_SHORT).show();
		
		finish();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		//What is l v position and id??
		super.onListItemClick(l, v, position, id);
		
		int dialogId = 0;
		// Figuring out what dialog to show based on the position clicked
		switch (position) {
		case LIST_ITEM_ID_DATE:
			dialogId = MyRunsDialogFragment.DIALOG_ID_MANUAL_INPUT_DATE;
			break;	
		case LIST_ITEM_ID_TIME:
			dialogId = MyRunsDialogFragment.DIALOG_ID_MANUAL_INPUT_TIME;
			break;
		case LIST_ITEM_ID_DURATION:
			dialogId = MyRunsDialogFragment.DIALOG_ID_MANUAL_INPUT_DURATION;
			break;
		case LIST_ITEM_ID_DISTANCE:
			dialogId = MyRunsDialogFragment.DIALOG_ID_MANUAL_INPUT_DISTANCE;
			break;
		case LIST_ITEM_ID_CALORIES:
			dialogId = MyRunsDialogFragment.DIALOG_ID_MANUAL_INPUT_CALORIES;
			break;
		case LIST_ITEM_ID_HEARTRATE:
			dialogId = MyRunsDialogFragment.DIALOG_ID_MANUAL_INPUT_HEARTRATE;
			break;
		case LIST_ITEM_ID_COMMENT:
			dialogId = MyRunsDialogFragment.DIALOG_ID_MANUAL_INPUT_COMMENT;
			break;
		default:
			dialogId = MyRunsDialogFragment.DIALOG_ID_ERROR;
		}
		displayDialog(dialogId);
	}

	
	// Display dialog based on id. See MyRunsDialogFragment for details
	public void displayDialog(int id) {

		DialogFragment fragment = MyRunsDialogFragment
				.newInstance(id);
		fragment.show(getFragmentManager(),
				getString(R.string.dialog_fragment_tag_general));
		//I used a general tag for the dialog fragment
	}

	// ********************************
	// The following are functions called after dialog is clicked.
	// Called from MyRunsDialogFragment side. mEntry is handled here in a
	// cleaner and more separated way.
	// value are parsed and set in mEntry for later database insertion.

	public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
		mEntry.setDate(year, monthOfYear, dayOfMonth);

	}

	public void onTimeSet(int hourOfDay, int minute) {
		mEntry.setTime(hourOfDay, minute, 0);
		Log.w(ManualInputActivity.class.getName(),
				"time=entering the onTimeset");
	}

	public void onDurationSet(String strDurationInMinutes) {

		int durationInSeconds;
		try {
			durationInSeconds = (int) (Double.parseDouble(strDurationInMinutes) * 60);
		} catch (NumberFormatException e) {
			durationInSeconds = 0;
		}

		mEntry.setDuration(durationInSeconds);
	}

	public void onDistanceSet(String strDistance) {

		int distanceInMeters;
		try {
			distanceInMeters = (int) (Double.parseDouble(strDistance) * 1000 * Globals.KM2MILE_RATIO);
		} catch (NumberFormatException e) {
			distanceInMeters = 0;
		}
		mEntry.setDistance(distanceInMeters);

	}

	public void onCaloriesSet(String strCalories) {

		int calories;
		try {
			calories = (Integer.parseInt(strCalories));
		} catch (NumberFormatException e) {
			calories = 0;
		}
		mEntry.setCalories(calories);
	}

	public void onHeartrateSet(String strHeartrate) {
		int heartrate;
		try {
			heartrate = (Integer.parseInt(strHeartrate));
		} catch (NumberFormatException e) {
			heartrate = 0;
		}
		mEntry.setHeartrate(heartrate);
	}

	public void onCommentSet(String comment) {
		mEntry.setComment(comment);
	}
}
