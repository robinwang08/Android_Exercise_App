package edu.dartmouth.cs.myruns6;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MapDisplayActivity extends Activity {

	// Menu ID for deletion
	public static final int MENU_ID_DELETE = 0;

	PolylineOptions rectOptions;

	// Map elements:
	public GoogleMap mMap;
	public Marker start;
	public Marker end;
	public TextView typeStats;
	public TextView avespeedStats;
	public TextView curspeedStats;
	public TextView climbStats;
	public TextView caloriesStats;
	public TextView distanceStats;

	// For bookkeeping if the service bound already
	public boolean mIsBound;

	// For bookkeeping if the drawing is done in onPostExecute.
	public boolean mIsDoneDrawing;

	// GPS tracking service
	public TrackingService mSensorService;
	public Intent mServiceIntent;
	public Context mContext;
	public int mTaskType;
	private ArrayList<LatLng> mLatLngList;
	public ArrayList<Location> mLocationList;

	// Exercise entry
	public ExerciseEntryHelper mEntry;

	// Need some special handling if it's the first location update
	public boolean mIsFirstLocUpdate;

	// Use this to draw the start Marker
	public LatLng firstLatLng;

	// A broadcast receiver to receive location update and recenter the map if
	// necessary in another thread.
	private IntentFilter mLocationUpdateFilter;

	final static String ACTION = "NotifyServiceAction";

	private BroadcastReceiver mLocationUpdateReceiver = new BroadcastReceiver() {
		// you code
		@Override
		public void onReceive(Context context, Intent intent) {
			LatLng latlng;
			synchronized (mLocationList) {
				latlng = Utils.fromLocationToLatLng(mLocationList.get(0));
			}

			// Set the first GPS coordinate once get it.
			if (mIsFirstLocUpdate) {
				mIsFirstLocUpdate = false;
				firstLatLng = latlng;
			}

			if (firstLatLng != null) {
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
						Globals.DEFAULT_MAP_ZOOM_LEVEL));
			}

			mIsDoneDrawing = false;
			try {
				mEntry.updateStats();
			} catch (Exception e) {

				e.printStackTrace();
			}
			synchronized (mLocationList) {
				// ----------------------Skeleton--------------------------
				// Initialization
				if (mLocationList == null || mLocationList.isEmpty())
					return;

				// Convert the mLocationList to mLatLngList
				for (int i = 0; i < mLocationList.size() - 1; i++) {
					Location loc = mLocationList.get(i);
					mLatLngList.add(Utils.fromLocationToLatLng(loc));
				}

				// Draw Polyline using PolylineOptions
				// ----------------------Skeleton--------------------------
				PolylineOptions polylineOptions = new PolylineOptions();

				// Set Polyline's color
				polylineOptions.color(Color.RED);

				// Set Polyline's width
				polylineOptions.width(5);

				// Add all LatLng points into the list
				polylineOptions.addAll(mLatLngList);

				// Draw the list
				mMap.addPolyline(polylineOptions);
				// ----------------------Skeleton--------------------------
				// Draw marker
				// Initialization
				if (start == null)
					start = mMap
					.addMarker(new MarkerOptions()
					.position(firstLatLng)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

				// Update the latest Marker
				if (end != null)
					end.remove();

				end = mMap
						.addMarker(new MarkerOptions()
						.position(
								Utils.fromLocationToLatLng(mLocationList
										.get(mLocationList.size() - 1)))
										.icon(BitmapDescriptorFactory
												.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

				// Get real-time stats from the Exercise Entry
				String[] statDecriptions = mEntry.getStatsDescription(mContext);

				// Draw the stats on the map
				if (statDecriptions.length != 0) {
					typeStats.setText(statDecriptions[0]);
					avespeedStats.setText(statDecriptions[1]);
					curspeedStats.setText(statDecriptions[2]);
					climbStats.setText(statDecriptions[3]);
					caloriesStats.setText(statDecriptions[4]);
					distanceStats.setText(statDecriptions[5]);

				}
				// Clear the mLatLngList
				mLatLngList.removeAll(mLatLngList);
			}
			mIsDoneDrawing = true;

		}
	};

	// Set up the mMotionUpdateIntentFilter broadcast receiver to update
	// activity inference using onReceive(). Gets the classification results
	// from the TrackingService and updates mEntry.updateByInference
	// A broadcast receiver to update activity inference.
	private IntentFilter mMotionUpdateIntentFilter;
	private BroadcastReceiver mMotionUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//
			int val = intent.getIntExtra(Globals.KEY_CLASSIFICATION_RESULT, -1);
			mEntry.updateByInference(val);

		}
	};

	// ----------------------Skeleton--------------------------
	// Create new ServiceConnection
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {

			// Initialize mSensorService from TrackingService

			mSensorService = ((TrackingService.MyRunsBinder) service)
					.getService();

			// Get mLocationList from mSensorService
			// set Location list for mEntry.

			mLocationList = mSensorService.mLocationList;
			mEntry.setLocationList(mLocationList);

			// Start logging

			try {
				mEntry.startLogging();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			// Stop the service. This ONLY gets called when crashed.
			stopService(mServiceIntent);
			mSensorService = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Inflate the map_display
		setContentView(R.layout.map_display);


		// Initialize the mLatLngList.
		mLatLngList = new ArrayList<LatLng>();

		// ----------------------Skeleton--------------------------
		// Find all 6 TextView widgets using their resource id.
		typeStats = (TextView) findViewById(R.id.statsType);
		avespeedStats = (TextView) findViewById(R.id.statsSpeed);
		curspeedStats = (TextView) findViewById(R.id.statsCurSpeed);
		climbStats = (TextView) findViewById(R.id.statsClimb);
		caloriesStats = (TextView) findViewById(R.id.statsCalories);
		distanceStats = (TextView) findViewById(R.id.statsDistance);

		// ----------------------Skeleton--------------------------
		// Set context.
		mContext = this;

		// Initialize mEntry.
		mEntry = new ExerciseEntryHelper();

		// Initialize the Bound flag.
		mIsBound = false;

		// ----------------------Skeleton--------------------------
		// Get extras from intent and set the mTaskType, InputType, Row Id and
		// ActivityType

		Bundle extras = getIntent().getExtras();

		mTaskType = extras.getInt(Globals.KEY_TASK_TYPE, -1);
		mEntry.setInputType(extras.getInt(Globals.KEY_INPUT_TYPE, -1));

		// Is this right?
		mEntry.setID(extras.getLong(Globals.KEY_ROWID, -1));

		mEntry.setActivityType(extras.getInt(Globals.KEY_ACTIVITY_TYPE, -1));

		// ----------------------Skeleton--------------------------
		// Get google map from the MapFragment

		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment = (MapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		mMap = myMapFragment.getMap();
		mMap.setMyLocationEnabled(false);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// Different initialization based on different task type and input mode
		// Combinations can be (new or history) and (gps or automatic). Manual
		// mode is handled in ManualInputActivity
		// The difference between new and history is:
		// "new" task type needs a service to read sensor data. "gps"
		// mode only pulls the GPS locations, and "automatic" mode also pull
		// motion sensor data
		// for Weka classifier While the "history" task type reads from
		// database, and display the
		// map route only, does not need sensor service.
		switch (mTaskType) {

		case Globals.TASK_TYPE_NEW:

			// ----------------------Skeleton--------------------------

			// Register the GPS location sensor to receive location update

			extras = new Bundle();
			extras.putInt(Globals.KEY_TASK_TYPE, mTaskType);
			extras.putInt(Globals.KEY_INPUT_TYPE, mEntry.getInputType());

			mLocationUpdateFilter = new IntentFilter();
			mLocationUpdateFilter.addAction(ACTION);
			//registerReceiver(mLocationUpdateReceiver, mLocationUpdateFilter);

			// Set the mIsFirstLocUpdate flag to handle first location.
			mIsFirstLocUpdate = true;

			// If in the automatic mode, also register the
			// motion sensor intent filter
			if (mEntry.getInputType() == Globals.INPUT_TYPE_AUTOMATIC) {
				mMotionUpdateIntentFilter = new IntentFilter();
				mMotionUpdateIntentFilter
				.addAction(Globals.ACTION_MOTION_UPDATED);
			}

			// Start and bind the tracking service

			mServiceIntent = new Intent(this, TrackingService.class);

			// Bundle tsExtra = new Bundle();
			// tsExtra.putInt(Globals.KEY_TASK_TYPE, Globals.TASK_TYPE_HISTORY);
			mServiceIntent.putExtras(extras);

			this.startService(mServiceIntent);
			doBindService();

			break;

		case Globals.TASK_TYPE_HISTORY:
			// ----------------------Skeleton--------------------------

			// No longer need "Save" and "Cancel" button in history mode
			Button saveButton = (Button) findViewById(R.id.btnMapSave);
			Button cancelButton = (Button) findViewById(R.id.btnMapCancel);

			saveButton.setVisibility(View.GONE);
			cancelButton.setVisibility(View.GONE);

			// ----------------------Skeleton--------------------------

			// Read track from database
			try {
				if (mEntry == null)
					Log.e("Entry", "Entry is null");

				mEntry.readFromDB(mContext);
			} catch (Exception e) {
				e.printStackTrace();
			}

			mLocationList= mEntry.getLocationList();

			if(mLocationList == null){
				return;
			}

			// ----------------------Skeleton--------------------------

			// Convert the mLocationList to mLatLngList
			// so that you can draw polylines using LatLng objects

			for (int i = 0; i < mLocationList.size() - 1; i++) {
				Location loc = mLocationList.get(i);
				mLatLngList.add(Utils.fromLocationToLatLng(loc));
			}

			LatLng newLoc = mLatLngList.get(mLatLngList.size() - 1);

			// ----------------------Skeleton--------------------------
			// Draw marker for the start point

			mMap.addMarker(new MarkerOptions().position(mLatLngList.get(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


			// ----------------------Skeleton--------------------------
			// Draw marker for the end point

			mMap
			.addMarker(new MarkerOptions()
			.position(mLatLngList.get(mLatLngList.size() - 1))
			.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

			// ----------------------Skeleton--------------------------
			// Draw the GPS traces, set the width, color and use addAll to
			// write a Polyline that goes through all the LatLng points

			rectOptions = new PolylineOptions();
			rectOptions.addAll(mLatLngList);
			rectOptions.color(Color.RED);
			mMap.addPolyline(rectOptions);


			// ----------------------Skeleton--------------------------
			// Move map center to the 1st point in the route track.

			if (!mLatLngList.isEmpty()) {
				LatLng latlng = mLatLngList.get(0);
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
						Globals.DEFAULT_MAP_ZOOM_LEVEL));

			}

			// ----------------------Skeleton--------------------------
			// Clear the mLatLngList

			mLatLngList.removeAll(mLatLngList);

			// ----------------------Skeleton--------------------------
			// Get previous stats from the ExerciseEntry

			String[] statDecriptions = mEntry.getStatsDescription(mContext);

			// ----------------------Skeleton--------------------------
			// Draw the stats on the map

			if (statDecriptions.length != 0) {
				switch (mEntry.getActivityType()) {
				case Globals.ACTIVITY_TYPE_WALKING:
					typeStats.setText("Type: Walking");
					break;
				case Globals.ACTIVITY_TYPE_RUNNING:
					typeStats.setText("Type: Running");
					break;
				case Globals.ACTIVITY_TYPE_STANDING:
					typeStats.setText("Type: Standing");
					break;
				case Globals.ACTIVITY_TYPE_CYCLING:
					typeStats.setText("Type: Cycling");
					break;
				default:
					break;
				}
				avespeedStats.setText(statDecriptions[1]);
				curspeedStats.setText(statDecriptions[2]);
				climbStats.setText(statDecriptions[3]);
				caloriesStats.setText(statDecriptions[4]);
				distanceStats.setText(statDecriptions[5]);

			}
			break;

		default:
			finish(); // Should never happen.
			return;
		}

	}

	@Override
	protected void onResume() {
		// Register the receiver for receiving the location update broadcast
		// from the service. Logic is the same as in onCreate()

		// If "new" task, need to read sensor data.
		// ----------------------Skeleton--------------------------
		if (mTaskType == Globals.TASK_TYPE_NEW) {
			// Register gps location update receiver

			registerReceiver(mLocationUpdateReceiver, mLocationUpdateFilter);


			if (mEntry.getInputType() == Globals.INPUT_TYPE_AUTOMATIC) {
				registerReceiver(mMotionUpdateReceiver,
						mMotionUpdateIntentFilter);
			}



		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		// Unregister the receiver when the activity is about to go inactive
		// Reverse to what happened in onResume()
		// ----------------------Skeleton--------------------------
		if (mTaskType == Globals.TASK_TYPE_NEW) {

			this.unregisterReceiver(mLocationUpdateReceiver);
			if (mEntry.getInputType() == Globals.INPUT_TYPE_AUTOMATIC) {
				unregisterReceiver(mMotionUpdateReceiver);
			}
			//doUnbindService();
		}

		Log.d(Globals.TAG, "Activity paused");
		super.onPause();
	}

	public void onSaveClicked(View v) {

		// The Exercise will be only saved when the drawing is done.
		if (mIsDoneDrawing == true) {
			// We don't want to save duplicate exercise entries
			// so we need to disable the button after first click.
			v.setEnabled(false);

			// Insert the ExerciseEntry to database.
			long id = mEntry.insertToDB(mContext);

			// There are some cases when the entry is not saved. see insertToDB
			// for detail.
			if (id > 0) {
				Toast.makeText(getApplicationContext(),
						"Entry #" + id + " saved.", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Entry not saved.",
						Toast.LENGTH_SHORT).show();
			}

			// ----------------------Skeleton--------------------------
			// Stop the service in the foreground, unbind the service and stop
			// it.
			// cancel the fired Notification.
			mSensorService.stopForeground(true);
			doUnbindService();
			stopService(mServiceIntent);
			((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
					.cancelAll();
			finish();
		}
	}

	public void onCancelClicked(View v) {
		// Similar to what happened in onSaveClicked() but without the
		// insertToDB
		// operation.
		// ----------------------Skeleton--------------------------
		if (v.getId() == R.id.btnMapCancel) {
			Button btn = (Button) findViewById(R.id.btnMapCancel);
			btn.setEnabled(false);
		}

		// Stop the service and the notification.
		// ----------------------Skeleton--------------------------

		mSensorService.stopForeground(true);
		doUnbindService();
		stopService(mServiceIntent);
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
		.cancelAll();
		finish();
	}

	@Override
	public void onDestroy() {
		// Stop the service and the notification.
		// ----------------------Skeleton--------------------------
		// Need to check whether the mSensorService is null or not
		// before unbind and stop the service.
		if (mSensorService != null) {
			mSensorService.stopForeground(true);

			doUnbindService();
			stopService(mServiceIntent);
		}
		// Cancel the fired Notification.
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
		.cancelAll();
		// Finish the MapDisplayActivity itself.
		finish();
		super.onDestroy();
	}



	@Override
	public void onBackPressed() {
		// When back is pressed, similar to onCancelClicked, stop service and
		// the notification.
		if (mTaskType == Globals.TASK_TYPE_NEW) {
			// ----------------------Skeleton--------------------------
			mSensorService.stopForeground(true);
			doUnbindService();
			stopService(mServiceIntent);
			((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
			.cancelAll();
		}
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// If task type is displaying history, also give a menu button
		// To delete the entry
		MenuItem menuitem;
		if (mTaskType == Globals.TASK_TYPE_HISTORY) {
			menuitem = menu.add(Menu.NONE, MENU_ID_DELETE, MENU_ID_DELETE,
					"Delete");
			menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_DELETE:
			// Delete entry in database
			mEntry.deleteEntryInDB(mContext);
			finish();
			return true;
		default:
			finish();
			return false;
		}
	}

	private void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		// ----------------------Skeleton--------------------------
		if (!mIsBound) {
			bindService(mServiceIntent, connection, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}

	}

	private void doUnbindService() {
		if (mIsBound) {
			unbindService(connection);
			mIsBound = false;
		}
	}

	// Make sure current location falls into center area of the screen
	// Otherwise re center the map
	private boolean ifMapNeedRecenter(LatLng latlng) {
		// Gets a projection of the viewing frustum for
		// converting between screen coordinates and
		// geo-latitude/longitude coordinates.

		VisibleRegion vr = mMap.getProjection().getVisibleRegion();

		double left = vr.latLngBounds.southwest.longitude;
		double top = vr.latLngBounds.northeast.latitude;
		double right = vr.latLngBounds.northeast.longitude;
		double bottom = vr.latLngBounds.southwest.latitude;

		int rectWidth = (int) Math.abs(right - left);
		int rectHeight = (int) Math.abs(top - bottom);

		int rectCenterX = (int) mMap.getCameraPosition().target.longitude;
		int rectCenterY = (int) mMap.getCameraPosition().target.latitude;

		// Constructs the rectangle
		Rect validScreenRect = new Rect(rectCenterX - rectWidth / 2,
				rectCenterY - rectHeight / 2, rectCenterX + rectWidth / 2,
				rectCenterY + rectHeight / 2);

		return !validScreenRect.contains((int) latlng.longitude,
				(int) latlng.latitude);
	}
}
