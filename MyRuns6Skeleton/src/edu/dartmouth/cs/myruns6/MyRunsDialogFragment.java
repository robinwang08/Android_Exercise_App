package edu.dartmouth.cs.myruns6;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

// Ref: http://developer.android.com/reference/android/app/DialogFragment.html
// The difference from the sample code is that we use Items, not OK/Cancel buttons

// Handling all the customized dialog boxes in our project.
// Differentiated by dialog id.
public class MyRunsDialogFragment extends DialogFragment {

	// Different dialog IDs
	public static final int DIALOG_ID_ERROR = -1;
	public static final int DIALOG_ID_PHOTO_PICKER = 1;
	public static final int DIALOG_ID_MANUAL_INPUT_DATE = 2;
	public static final int DIALOG_ID_MANUAL_INPUT_TIME = 3;
	public static final int DIALOG_ID_MANUAL_INPUT_DURATION = 4;
	public static final int DIALOG_ID_MANUAL_INPUT_DISTANCE = 5;
	public static final int DIALOG_ID_MANUAL_INPUT_CALORIES = 6;
	public static final int DIALOG_ID_MANUAL_INPUT_HEARTRATE = 7;
	public static final int DIALOG_ID_MANUAL_INPUT_COMMENT = 8;

	// For photo picker selection:
	public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
	public static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;

	private static final String DIALOG_ID_KEY = "dialog_id";

	public static MyRunsDialogFragment newInstance(int dialog_id) {

		// New instance of a DialogFragment
		MyRunsDialogFragment frag = new MyRunsDialogFragment();
		Bundle args = new Bundle();
		args.putInt(DIALOG_ID_KEY, dialog_id);
		frag.setArguments(args);
		return frag;
	}

	// Create the dialogs appropriately
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int dialog_id = getArguments().getInt(DIALOG_ID_KEY);

		final Activity parent = getActivity();

		// For initializing date/time related dialogs
		final Calendar now;

		// Did not use these
		// int hour, minute, year, month, day;

		now = Calendar.getInstance();

		// For text input field; Did not use
		// final EditText textEntryView;

		// Setup the appropriate dialog appearance and onClick Listeners
		switch (dialog_id) {

		case DIALOG_ID_PHOTO_PICKER:
			// Build picture picker dialog for choosing from camera or gallery
			AlertDialog.Builder builder = new AlertDialog.Builder(parent);
			builder.setTitle(R.string.ui_profile_photo_picker_title);
			// Set up click listener, firing intents open camera or gallery
			// based on
			// choice.
			builder.setItems(R.array.ui_profile_photo_picker_items,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							// Item can be: ID_PHOTO_PICKER_FROM_CAMERA
							// or ID_PHOTO_PICKER_FROM_GALLERY
							((ProfileActivity) parent)
									.onPhotoPickerItemSelected(item);
						}
					});
			return builder.create();

		case DIALOG_ID_MANUAL_INPUT_DATE:

			DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {

					((ManualInputActivity) parent).onDateSet(year, monthOfYear,
							dayOfMonth);
				}
			};

			DatePickerDialog dater = new DatePickerDialog(getActivity(),
					mDateListener, now.get(Calendar.YEAR),
					now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

			return dater;

		case DIALOG_ID_MANUAL_INPUT_TIME:

			TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {

				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

					((ManualInputActivity) parent).onTimeSet(hourOfDay, minute);
				}
			};

			TimePickerDialog timey = new TimePickerDialog(getActivity(),
					mTimeListener, now.get(Calendar.HOUR_OF_DAY),
					now.get(Calendar.MINUTE), false);

			return timey;

		case DIALOG_ID_MANUAL_INPUT_DURATION:

			AlertDialog.Builder duration = new AlertDialog.Builder(parent);

			duration.setTitle(R.string.ui_manual_input_duration_title);

			final EditText durationInput = new EditText(parent);
			durationInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			duration.setView(durationInput);

			duration.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String theDuration = (durationInput.getText()
									.toString());
							((ManualInputActivity) parent)
									.onDurationSet(theDuration);
						}
					});

			duration.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});

			return duration.create();

		case DIALOG_ID_MANUAL_INPUT_DISTANCE:

			AlertDialog.Builder distance = new AlertDialog.Builder(parent);

			distance.setTitle(R.string.ui_manual_input_distance_in_miles_title);

			final EditText distanceInput = new EditText(parent);
			distanceInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			distance.setView(distanceInput);

			distance.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String theDistance = (distanceInput.getText()
									.toString());
							((ManualInputActivity) parent)
									.onDistanceSet(theDistance);
						}
					});

			distance.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});

			return distance.create();

		case DIALOG_ID_MANUAL_INPUT_CALORIES:

			AlertDialog.Builder calories = new AlertDialog.Builder(parent);

			calories.setTitle(R.string.ui_manual_input_calories_title);

			final EditText caloriesInput = new EditText(parent);
			caloriesInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			calories.setView(caloriesInput);

			calories.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String theCalories = (caloriesInput.getText()
									.toString());
							((ManualInputActivity) parent)
									.onCaloriesSet(theCalories);
						}
					});

			calories.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});

			return calories.create();

		case DIALOG_ID_MANUAL_INPUT_HEARTRATE:

			AlertDialog.Builder heart = new AlertDialog.Builder(parent);

			heart.setTitle(R.string.ui_manual_input_heartrate_title);

			final EditText heartInput = new EditText(parent);
			heartInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			heart.setView(heartInput);

			heart.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String theHeart = (heartInput.getText().toString());
							((ManualInputActivity) parent)
									.onHeartrateSet(theHeart);
						}
					});

			heart.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});

			return heart.create();

		case DIALOG_ID_MANUAL_INPUT_COMMENT:

			AlertDialog.Builder comment = new AlertDialog.Builder(parent);

			comment.setTitle(R.string.ui_manual_input_comment_title);

			final EditText commentInput = new EditText(parent);
			commentInput.setHint(R.string.ui_manual_input_comment_hint);
			commentInput.setInputType(InputType.TYPE_CLASS_TEXT);
			comment.setView(commentInput);

			comment.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							String theComment = (commentInput.getText()
									.toString());
							((ManualInputActivity) parent)
									.onCommentSet(theComment);
						}
					});

			comment.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});

			return comment.create();

		default:
			return null;
		}
	}

}
