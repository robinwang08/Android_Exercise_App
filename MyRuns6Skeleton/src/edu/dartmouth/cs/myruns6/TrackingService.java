/**
 * LocationService.java
 * 
 * Created by Xiaochao Yang on Sep 11, 2011 4:50:19 PM
 * 
 */

package edu.dartmouth.cs.myruns6;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import com.meapsoft.FFT;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

// This service will: read and process GPS data.
public class TrackingService extends Service implements LocationListener,
SensorEventListener {
	
	// A buffer list to store all GPS track points
		// It's accessed at different places
		public ArrayList<Location> mLocationList;
		public int mInferenceResult;

		// Sensor manager for accelerometer
		private SensorManager mSensorManager;
		private Sensor mAccelerometer;

		// Location manager and Notification manager
		private LocationManager mlocationManager;
		private NotificationManager mNotificationManager;

		// Context for "this"
		private Context mContext;

		// Intents for broadcasting location/motion updates
		private Intent mLocationUpdateBroadcast;
		private Intent mActivityClassificationBroadcast;

		// A blocking queue for buffering motion sensor data
		private static ArrayBlockingQueue<Double> mAccBuffer;

		// The AsyncTask running in a different thread all the time to
		// process the motion sensor data and do classification
		private ActivityClassificationTask mActivityClassificationTask;

		// Based on input type, GPS or automatic, do different things
		private int mInputType;

		// Standard service stuff.
		private final IBinder binder = new MyRunsBinder();

		public class MyRunsBinder extends Binder {
			TrackingService getService() {
				return TrackingService.this;
			}
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			return binder;
		}


	@Override
	public void onCreate() {

		// Initialize mContext, mLocationList, mLocationUpdateBroadcast
		// ----------------------Skeleton--------------------------

		// mContext=getApplicationContext();
		mContext = this;

		// add intent
		mLocationUpdateBroadcast = new Intent();

		// Add the arraylist of locations
		mLocationList = new ArrayList<Location>();
		
		mAccBuffer = new ArrayBlockingQueue<Double>(
				Globals.ACCELEROMETER_BUFFER_CAPACITY);

		mActivityClassificationBroadcast = new Intent();
		mActivityClassificationBroadcast
		.setAction(Globals.ACTION_MOTION_UPDATED);

		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Read inputType, can be GPS or Automatic.
		// ----------------------Skeleton-------------------------

		mInputType = intent.getExtras().getInt(Globals.KEY_INPUT_TYPE);
		// Bundle extras = intent.getExtras();
		// mInputType=extras.getInt(Globals.KEY_INPUT_TYPE,-1);

		// ----------------------Skeleton--------------------------
		// Get LocationManager and set related provider.

		// Get LocationManager and set related provider.
		// GPS_PROVIDER recommended.
		mlocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, this);

		// If it's automatic mode, registering motion sensor for activity
		// recognition.
		if (mInputType == Globals.INPUT_TYPE_AUTOMATIC) {

			//			You code here
			//			Set up the SensorManager
			//			Instantiate the mActivityClassificationTask AsynTask and start it 
			//				


			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		    mSensorManager.registerListener(this, mAccelerometer,SensorManager.SENSOR_DELAY_FASTEST);
		    mActivityClassificationTask = new ActivityClassificationTask();
			mActivityClassificationTask.execute();
			
		}
			
		// For indoor debugging, can use network cellular location

		// Location lastLocation = mlocationManager
		// .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// mLocationList.add(lastLocation);

		// ----------------------Skeleton--------------------------
		// Fire the MapDisplayAcitivty

		// ----------------------Skeleton--------------------------
		// Set flags to avoid re-invent activity.
		// http://developer.android.com/guide/topics/manifest/activity-element.html#lmode
		// IMPORTANT!. no re-create activity

		Intent myIntent = new Intent(mContext, MapDisplayActivity.class);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// Send Notification

		String notificationTitle = getString(R.string.ui_maps_display_notification_title);
		String notificationText = getString(R.string.ui_maps_display_notification_content);

		// ----------------------Skeleton--------------------------
		// Using pending intent to bring back the MapActivity from notification
		// center.

		PendingIntent pendingIntent = PendingIntent.getActivity(
				getBaseContext(), 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		// ----------------------Skeleton--------------------------
		// Use NotificationManager to build notification(icon, content, title,
		// flag and pIntent)

		Notification notification = new Notification.Builder(this)
				.setContentTitle(notificationTitle)
				.setContentText(notificationText)
				.setSmallIcon(R.drawable.greend)
				.setContentIntent(pendingIntent).build();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification.flags = notification.flags
				| Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		mNotificationManager.notify(0, notification);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// ----------------------Skeleton--------------------------

		// Unregistering listeners
		mlocationManager.removeUpdates(this);
		
		mNotificationManager.cancelAll();
		
		Log.d(Globals.TAG, "Service Destoryed");
		
		if (mInputType == Globals.INPUT_TYPE_AUTOMATIC) {

			//Your code here

			mSensorManager.unregisterListener(this);
			mActivityClassificationTask.cancel(false);


		}
		
		super.onDestroy();

	}

	// Gets called when new GPS location updates
	public void onLocationChanged(Location location) {

		// ----------------------Skeleton--------------------------
		// Check whether location is valid, drop if invalid

		if (location == null) {
			return;
		}

		// ----------------------Skeleton--------------------------
		// Buffer the new location. mLocation is connected by reference by
		// several other classes. Accessed with "synchronized" lock

		else {

			synchronized (mLocationList) {
				mLocationList.add(location);
			}

			// ----------------------Skeleton--------------------------
			// Send broadcast saying new location is updated

			mLocationUpdateBroadcast = new Intent();
			mLocationUpdateBroadcast.setAction(MapDisplayActivity.ACTION);
			sendBroadcast(mLocationUpdateBroadcast);
		}
	}

	// You don't need to implement the other three abstract methods of the
	// LocationListener Interface class.:
	// onProviderDisabled, onProviderEnabled, and onStatusChanged.
	// You can leave them as blank.
	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}



	// ----------------------Skeleton--------------------------
	// An AsyncTask running in a separate thread processing the sensor data.
	// It's an infinite loop, waiting on new sensor event, and uses weka
	// classifier to do activity recognition
	private class ActivityClassificationTask extends
	AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {

			//			Your code  here. Take a look at collector but do not copy 
			//			the code directly. You must understand the collector code
			//			and write it in your own way


			ArrayList<Double> featVect = new ArrayList<Double>();

			int blockSize = 0;
			FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
			double [] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] re = accBlock;
			double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];

			double max = Double.MIN_VALUE;

			while (true) {

				try {
					// need to check if the AsyncTask is cancelled or not in the while loop
					if (isCancelled () == true)
					{
						return null;
					}


					// Dumping buffer
					accBlock[blockSize++] = mAccBuffer.take().doubleValue();

					if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
						blockSize = 0;

						// time = System.currentTimeMillis();
						max = .0;
						for (double val : accBlock) {
							if (max < val) {
								max = val;
							}
						}

						fft.fft(re, im);
						
						Log.e("val","0:" + re[0] + ";" + im[0]);
						for (int i = 0; i < re.length; i++) {
							double mag = Math.sqrt(re[i] * re[i] + im[i]
									* im[i]);
							featVect.add(mag);
							if(i==0) {
								Log.e("00000","000000:   " + mag + "      "+featVect.get(0));
							}
							im[i] = .0; // Clear the field
						}

						featVect.add(max);

						int classifiedValue = (int) WekaClassifier.classify(featVect.toArray());
						Log.e("ACCCCC", "classify: " + classifiedValue + " 0:"+featVect.toArray()[0]);
						//Toast.makeText(mContext, String.valueOf(classifiedValue), Toast.LENGTH_SHORT).show();
						mActivityClassificationBroadcast.putExtra(Globals.KEY_CLASSIFICATION_RESULT, classifiedValue);
						mContext.sendBroadcast(mActivityClassificationBroadcast);
						featVect.clear();
					}
				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

		// ----------------------Skeleton--------------------------
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

				double m = Math.sqrt(event.values[0] * event.values[0]
						+ event.values[1] * event.values[1] + event.values[2]
								* event.values[2]);

				// Inserts the specified element into this queue if it is possible
				// to do so immediately without violating capacity restrictions,
				// returning true upon success and throwing an IllegalStateException
				// if no space is currently available. When using a
				// capacity-restricted queue, it is generally preferable to use
				// offer.

				try {
					mAccBuffer.add(new Double(m));
				} catch (IllegalStateException e) {

					// Exception happens when reach the capacity.
					// Doubling the buffer. ListBlockingQueue has no such issue,
					// But generally has worse performance
					ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
							mAccBuffer.size() * 2);

					mAccBuffer.drainTo(newBuf);
					mAccBuffer = newBuf;
					mAccBuffer.add(new Double(m));
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}


}
