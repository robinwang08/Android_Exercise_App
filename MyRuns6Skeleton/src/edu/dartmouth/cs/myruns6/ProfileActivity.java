package edu.dartmouth.cs.myruns6;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	// Codes to determine whether the user wants to take a picture or choose one
	public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
	public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;
	public static final int REQUEST_CODE_CROP_PHOTO = 2;

	private static final String IMAGE_UNSPECIFIED = "image/*";
	private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

	private Uri mImageCaptureUri;
	private ImageView mImageView;
	private boolean isTakenFromCamera;

	// Spinner for the class
	Spinner classSpinner;

	private int myYear, myMonth, myDay;
	static final int ID_DATEPICKER = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set layout to be the profile layout
		setContentView(R.layout.profile);

		Button datePickerButton = (Button) findViewById(R.id.bday);
		datePickerButton.setOnClickListener(datePickerButtonOnClickListener);

		// Refer to the AutoCompleteTextView in the profile layout
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.editMajor1);

		// Get the string array of all the majors
		String[] majors = getResources().getStringArray(R.array.majors_array);

		// Create the adapter and set it to the AutoCompleteTextView
		ArrayAdapter<String> majorAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, majors);

		// Set the adapter
		textView.setAdapter(majorAdapter);

		// Refer to the profile layout spinner for the class
		classSpinner = (Spinner) findViewById(R.id.editClass);

		// Create an ArrayAdapter using the string array and a default spinner
		ArrayAdapter<CharSequence> classAdapter = ArrayAdapter
				.createFromResource(this, R.array.class_array,
						android.R.layout.simple_spinner_item);

		// Specify the layout to use when the list of choices appears
		classAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		classSpinner.setAdapter(classAdapter);

		// Load the previously saved profile if there is one
		loadProfile();

		// Set the imageview
		mImageView = (ImageView) findViewById(R.id.imageProfile);

		if (savedInstanceState != null) {
			mImageCaptureUri = savedInstanceState
					.getParcelable(URI_INSTANCE_STATE_KEY);
		}
		// Load picture
		loadSnap();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
	}

	// ****************** button click callbacks ***************************//

	public void onSaveClicked(View v) {
		// Save profile
		saveProfile();

		// Save Picture
		saveSnap();

		// Making a "toast" informing the user the profile is saved.
		Toast.makeText(getApplicationContext(),
				getString(R.string.ui_profile_toast_save_text),
				Toast.LENGTH_SHORT).show();
		// Close the activity
		finish();
	}

	public void onCancelClicked(View v) {
		// Making a "toast" informing the user changes are canceled.
		Toast.makeText(getApplicationContext(),
				getString(R.string.ui_profile_toast_cancel_text),
				Toast.LENGTH_SHORT).show();
		// Close the activity
		finish();
	}

	public void onChangePhotoClicked(View v) {
		// Changing the profile image, show the dialog asking the user
		// to choose between taking a picture and picking from gallery
		// Go to MyRunsDialogFragment for details.
		displayDialog(MyRunsDialogFragment.DIALOG_ID_PHOTO_PICKER);

	}

	// Handle data after activity returns.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {

		case REQUEST_CODE_TAKE_FROM_CAMERA:
			// Send image taken from camera for cropping
			cropImage();
			break;

		case REQUEST_CODE_CROP_PHOTO:
			// Update image view after image crop
			Bundle extras = data.getExtras();
			// Set the picture image in UI
			if (extras != null) {
				mImageView
						.setImageBitmap((Bitmap) extras.getParcelable("data"));
			}

			// Delete temporary image taken by camera after crop.
			if (isTakenFromCamera) {
				File f = new File(mImageCaptureUri.getPath());
				if (f.exists())
					f.delete();
			}
			break;

		case REQUEST_CODE_SELECT_FROM_GALLERY:

			// Find and choose picture from gallery
			mImageCaptureUri = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(mImageCaptureUri,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			cursor.close();
			// Send image taken from camera for cropping
			cropImage();
			break;

		}

	}

	// ******* Photo picker dialog related functions ************//

	public void displayDialog(int id) {
		DialogFragment fragment = MyRunsDialogFragment.newInstance(id);
		fragment.show(getFragmentManager(),
				getString(R.string.dialog_fragment_tag_photo_picker));
	}

	public void onPhotoPickerItemSelected(int item) {
		Intent intent;

		switch (item) {

		case MyRunsDialogFragment.ID_PHOTO_PICKER_FROM_CAMERA:
			// Take photo from camera，
			// Construct an intent with action
			// MediaStore.ACTION_IMAGE_CAPTURE
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Construct temporary image path and name to save the taken
			// photo
			mImageCaptureUri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), "tmp_"
					+ String.valueOf(System.currentTimeMillis()) + ".jpg"));
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			try {
				// Start a camera capturing activity
				// REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
				// defined to identify the activity in onActivityResult()
				// when it returns
				startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			isTakenFromCamera = true;
			break;

		// User wants to choose photo from gallery; start new activity to do so
		// REQUEST_CODE_SELECT_FROM_GALLERY is an integer tag you
		// defined to identify the activity in onActivityResult()
		// when it returns
		case MyRunsDialogFragment.ID_PHOTO_PICKER_FROM_GALLERY:
			Intent intent2 = new Intent();
			intent2.setType("image/*");
			intent2.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent2, "Select Picture"),
					REQUEST_CODE_SELECT_FROM_GALLERY);
			break;

		default:
			return;
		}
	}

	// Crop and resize the image for profile
	private void cropImage() {
		// Use existing crop activity.
		// Use existing crop activity.
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(mImageCaptureUri, IMAGE_UNSPECIFIED);

		// Specify image size
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);

		// Specify aspect ratio, 1:1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		// REQUEST_CODE_CROP_PHOTO is an integer tag you defined to
		// identify the activity in onActivityResult() when it returns
		startActivityForResult(intent, REQUEST_CODE_CROP_PHOTO);
	}

	// ****************** private helper functions ***************************//

	private void loadProfile() {

		// Load and update all profile views

		// Get the shared preferences - create or retrieve the activity
		// preference object
		String mKey = getString(R.string.preference_name);
		SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);

		// Load the user's name
		mKey = getString(R.string.preference_key_profile_name);
		String mNameValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.editName)).setText(mNameValue);

		// Load the user's phone
		mKey = getString(R.string.preference_key_profile_phone);
		String mPhoneValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.editPhone)).setText(mPhoneValue);

		// Load the user's email
		mKey = getString(R.string.preference_key_profile_email);
		String mValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.editEmail)).setText(mValue);

		/*
		 * Code for loading user input class data
		 * 
		 * mKey = getString(R.string.preference_key_profile_class); String
		 * mClassValue = mPrefs.getString(mKey, " "); ((EditText)
		 * findViewById(R.id.editClass)).setText(mClassValue);
		 */

		// Load the user's major
		mKey = getString(R.string.preference_key_profile_major);
		String mMajorValue = mPrefs.getString(mKey, "");
		((EditText) findViewById(R.id.editMajor1)).setText(mMajorValue);

		// Load the user's birthday
		mKey = getString(R.string.preference_key_profile_birthday);
		String mBirthdayValue = mPrefs.getString(mKey, "");
		EditText textViewToChange = (EditText) findViewById(R.id.bdaytext);
		textViewToChange.setText(mBirthdayValue);

		// Load the user's class by getting the saved spinner's position
		mKey = getString(R.string.preference_key_profile_class);

		int mClassIntValue = mPrefs.getInt(mKey, -1);
		if (mClassIntValue >= 0) {
			classSpinner.setSelection(mClassIntValue);
		}

		// Load gender info and set radio box
		mKey = getString(R.string.preference_key_profile_gender);

		int mIntValue = mPrefs.getInt(mKey, -1);
		// In case there isn't one saved before:
		if (mIntValue >= 0) {
			// Find the radio button that should be checked.
			RadioButton radioBtn = (RadioButton) ((RadioGroup) findViewById(R.id.radioGender))
					.getChildAt(mIntValue);

			radioBtn.setChecked(true);

		}

	}

	private void saveProfile() {
		// Get the shared preferences editor
		String mKey = getString(R.string.preference_name);
		SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);

		SharedPreferences.Editor mEditor = mPrefs.edit();
		mEditor.clear();

		// Save the user'sname
		mKey = getString(R.string.preference_key_profile_name);

		String mNameValue = (String) ((EditText) findViewById(R.id.editName))
				.getText().toString();
		mEditor.putString(mKey, mNameValue);

		// Save the user's phone information
		mKey = getString(R.string.preference_key_profile_phone);

		String mPhoneValue = (String) ((EditText) findViewById(R.id.editPhone))
				.getText().toString();
		mEditor.putString(mKey, mPhoneValue);

		// Save the user's email information
		mKey = getString(R.string.preference_key_profile_email);

		String mValue = (String) ((EditText) findViewById(R.id.editEmail))
				.getText().toString();
		mEditor.putString(mKey, mValue);

		// Save the user's class information by saving the selection of the
		// spinner
		mKey = getString(R.string.preference_key_profile_class);
		int mClass = classSpinner.getSelectedItemPosition();
		mEditor.putInt(mKey, mClass);

		/*
		 * Code for the original class using user input
		 * 
		 * mKey = getString(R.string.preference_key_profile_class);
		 * 
		 * String mClassValue = (String) ((EditText)
		 * findViewById(R.id.editClass)) .getText().toString();
		 * mEditor.putString(mKey, mClassValue);
		 */

		// Save birthday Information
		mKey = getString(R.string.preference_key_profile_birthday);

		String mBirthdayValue = (String) ((EditText) findViewById(R.id.bdaytext))
				.getText().toString();
		mEditor.putString(mKey, mBirthdayValue);

		// Save major information
		mKey = getString(R.string.preference_key_profile_major);

		String mMajorValue = (String) ((EditText) findViewById(R.id.editMajor1))
				.getText().toString();
		mEditor.putString(mKey, mMajorValue);

		// Read which index the radio is checked.

		mKey = getString(R.string.preference_key_profile_gender);

		RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGender);
		int mIntValue = mRadioGroup.indexOfChild(findViewById(mRadioGroup
				.getCheckedRadioButtonId()));
		mEditor.putInt(mKey, mIntValue);

		// Commit all the changes into the shared preferences
		mEditor.commit();

	}

	private void loadSnap() {

		// Load profile photo from internal storage
		try {
			FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
			Bitmap bmap = BitmapFactory.decodeStream(fis);
			mImageView.setImageBitmap(bmap);
			fis.close();
		} catch (IOException e) {
			// Default profile photo if no photo saved before.
			mImageView.setImageResource(R.drawable.default_profile);
		}
	}

	private void saveSnap() {

		// Commit all the changes into preference file
		// Save profile image into internal storage.
		mImageView.buildDrawingCache();
		Bitmap bmap = mImageView.getDrawingCache();
		try {
			FileOutputStream fos = openFileOutput(
					getString(R.string.profile_photo_file_name), MODE_PRIVATE);
			bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// EXTRA CREDIT - BIRTHDAY Date Picker
	private Button.OnClickListener datePickerButtonOnClickListener = new Button.OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			final Calendar c = Calendar.getInstance();
			myYear = c.get(Calendar.YEAR);
			myMonth = c.get(Calendar.MONTH);
			myDay = c.get(Calendar.DAY_OF_MONTH);
			showDialog(ID_DATEPICKER);
		}
	};

	// Create the dialog to choose and set the birthday
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ID_DATEPICKER:

			return new DatePickerDialog(this, myDateSetListener, myYear,
					myMonth, myDay);
		default:
			return null;
		}
	}

	private DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			// Change to string month names
			String month = "invalid";
			DateFormatSymbols dfs = new DateFormatSymbols();
			String[] months = dfs.getMonths();
			if (monthOfYear >= 0 && monthOfYear <= 11) {
				month = months[monthOfYear];
			}

			String date = month + " " + String.valueOf(dayOfMonth) + ", "
					+ String.valueOf(year);

			// Show the birthday
			EditText textViewToChange = (EditText) findViewById(R.id.bdaytext);
			textViewToChange.setText("Birthday: " + date);

		}

	};

}