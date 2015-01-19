/**
 * ActivityTabStart.java
 * 
 * Created by Xiaochao Yang on Dec 9, 2011 10:51:32 PM
 * 
 */

package edu.dartmouth.cs.myruns6;

import java.io.IOException;

import edu.dartmouth.cs.myruns6.gae.HistoryUploader;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class StartTabFragment extends Fragment {

	// Context stands for current running activity.
	private Context mContext;

	// View widgets on the screen needs to be programmatically configured
	private Spinner mSpinnerInputType;
	private Spinner mSpinnerActivityType;
	private Button mButtonStart;
	private Button mButtonSync;

	private HistoryUploader mHistoryUploader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view defined in start.xml. Return at the end
		View view = inflater.inflate(R.layout.start, container, false);

		// Initialize context
		mContext = getActivity();

		// Initialize uploader
		String serverUrl = Globals.SERVER_URL + "/post_data";
		mHistoryUploader = new HistoryUploader(mContext, serverUrl);
		// Initialize view widgets by IDs
		mSpinnerInputType = (Spinner) view.findViewById(R.id.spinnerInputType);
		mSpinnerActivityType = (Spinner) view
				.findViewById(R.id.spinnerActivityType);
		mButtonStart = (Button) view.findViewById(R.id.btnStart);
		mButtonSync = (Button) view.findViewById(R.id.btnSync);

		// Set up the onClick event of the "Start" button
		mButtonStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				int inputType = mSpinnerInputType.getSelectedItemPosition();

				Bundle extras = new Bundle();

				extras.putInt(Globals.KEY_TASK_TYPE, Globals.TASK_TYPE_NEW);
				// Input type as explained above.
				extras.putInt(Globals.KEY_INPUT_TYPE, inputType);

				Intent i;

				// Based on different selection, start different activity with

				switch (inputType) {

				case Globals.INPUT_TYPE_GPS:

					// GPS automatic tracking
					extras.putInt(Globals.KEY_ACTIVITY_TYPE,
							mSpinnerActivityType.getSelectedItemPosition());
					// Intent to launch MapDisplayActivity
					i = new Intent(mContext, MapDisplayActivity.class);
					break;

				case Globals.INPUT_TYPE_MANUAL:

					// Manual input
					extras.putInt(Globals.KEY_ACTIVITY_TYPE,
							mSpinnerActivityType.getSelectedItemPosition());
					// Intent to launch ManualInputActivity
					i = new Intent(mContext, ManualInputActivity.class);
					break;

				case Globals.INPUT_TYPE_AUTOMATIC:
					// Inference, the activity does not matter
					extras.putInt(Globals.KEY_ACTIVITY_TYPE, -1);
					// Intent to launch MapDisplayActivity
					i = new Intent(mContext, MapDisplayActivity.class);
					break;

				default:
					return;
				}
				// Put extras into the intent
				i.putExtras(extras);
				// Launch activity
				startActivity(i);
			}
		});

		// Setup the onClick event of the "Sync" button,
		// which will start syncing with the server
		mButtonSync.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				onSyncClicked(v);
			}
		});
		return view;
	}

	public void onSyncClicked(View view) {

		new AsyncTask<Void, Void, String>() {

			@Override
			// Get history and upload it to the server. 
			protected String doInBackground(Void... arg0) {
				// Query to access the database.

				Cursor c = getActivity().getContentResolver().query(HistoryProvider.CONTENT_URI,null,null, null, null);

				String uploadState = "";
				// Upload the history of all entries using upload(). 



				try {
					mHistoryUploader.upload(c);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					uploadState = "Sync failed: " + e1.getCause();
				}

				return uploadState;
			}

			@Override
			protected void onPostExecute(String errString) {
				String resultString;
				if(errString.equals("")) {
					resultString =  "All entries uploaded.";
				} else {
					resultString = errString;
				}

				Toast.makeText(mContext,"Uploaded!",Toast.LENGTH_SHORT).show();
			}
		}.execute();

	}

}
