/**
 * Globals.java
 * 
 * Created by Xiaochao Yang on Dec 9, 2011 1:43:35 PM
 * 
 */

package edu.dartmouth.cs.myruns6;

import android.content.Context;
import android.telephony.TelephonyManager;

// All the global constants are put here
public abstract class Globals {

	// Debugging tag for the whole project
	public static final String TAG = "MyRuns";
	
	// Const for distance/time conversions
	public static final double KM2MILE_RATIO = 1.609344;
	public static final double KILO = 1000;
	public static final int SECONDS_PER_HOUR = 3600;
	
	// Some map display related consts 
	public static final double MINIMUM_LOCATION_ACCURACY = 15.0;
	public static final int GPS_LOCATION_CACHE_SIZE = 100000;
	public static final int DEFAULT_MAP_ZOOM_LEVEL = 17;
	
	// Table schema, column names
	public static final String KEY_ROWID = "_id";
	public static final String KEY_INPUT_TYPE = "input_type";
	public static final String KEY_ACTIVITY_TYPE = "activity_type";
	public static final String KEY_DATE_TIME = "date_time";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_AVG_PACE = "avg_pace";
	public static final String KEY_AVG_SPEED = "avg_speed";
	public static final String KEY_CALORIES = "calories";
	public static final String KEY_CLIMB = "climb";
	public static final String KEY_HEARTRATE = "heartrate";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_PRIVACY = "privacy";
	public static final String KEY_GPS_DATA = "gps_data";
	
	// All activity types in a string array.
	// "Standing" is not an exercise type... it's there for activity recognition result
	// There was a debate about using "Standing" or "Still". The authority decided using
	// "Standing"...
	public static final String[] ACTIVITY_TYPES = {"Running", "Cycling", "Walking", "Hiking", 
		"Downhill Skiing", "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming", 
		"Mountain Biking", "Wheelchair", "Elliptical", "Other",  "Standing"};
	
	// Int encoded activity types
	public static final int ACTIVITY_TYPE_ERROR = -1;
	public static final int ACTIVITY_TYPE_RUNNING = 0;
	public static final int ACTIVITY_TYPE_CYCLING = 1;
	public static final int ACTIVITY_TYPE_WALKING = 2;
	public static final int ACTIVITY_TYPE_HIKING = 3;
	public static final int ACTIVITY_TYPE_DOWNHILL_SKIING = 4;
	public static final int ACTIVITY_TYPE_CROSS_COUNTRY_SKIING = 5;
	public static final int ACTIVITY_TYPE_SNOWBOARDING = 6;
	public static final int ACTIVITY_TYPE_SKATING = 7;
	public static final int ACTIVITY_TYPE_SWIMMING = 8;
	public static final int ACTIVITY_TYPE_MOUNTAIN_BIKING = 9;
	public static final int ACTIVITY_TYPE_WHEELCHAIR = 10;
	public static final int ACTIVITY_TYPE_ELLIPTICAL = 11;
	public static final int ACTIVITY_TYPE_OTHER = 12;
	public static final int ACTIVITY_TYPE_STANDING = 13; 
	public static final int ACTIVITY_TYPE_UNKNOWN = 14;
	
	// Consts for input types. 
	public static final int INPUT_TYPE_ERROR = -1;
	public static final int INPUT_TYPE_MANUAL = 0;
	public static final int INPUT_TYPE_GPS = 1;
	public static final int INPUT_TYPE_AUTOMATIC = 2;
	
	// The notification bar ID 
	public static final int NOTIFICATION_ID = 1;
	
	// Consts for task types
	public static final String KEY_TASK_TYPE = "TASK_TYPE";
	public static final int TASK_TYPE_ERROR = -1;
	public static final int TASK_TYPE_NEW = 1;
	public static final int TASK_TYPE_HISTORY = 2;

	// Motion sensor buffering related consts 
	public static final int ACCELEROMETER_BUFFER_CAPACITY = 2048;
	public static final int ACCELEROMETER_BLOCK_CAPACITY = 64;
	
	// Activity recognition results
	public static final int INFERENCE_ID_STANDING = 0;
	public static final int INFERENCE_ID_WALKING = 1;
	public static final int INFERENCE_ID_RUNNING = 2;
	public static final int INFERENCE_ID_OTHER = 3;
	
	// Consts for broadcast in the BackgroundService.java
	public static final String ACTION_LOCATION_UPDATED = "MYRUNS_LOCATION_UPDATED";
	public static final String ACTION_MOTION_UPDATED = "MYRUNS_MOTION_UPDATED";
	public static final String KEY_CLASSIFICATION_RESULT = "classificiation_result";
	
	// The names of the class labels and keys for activity recognition 
	public static final String CLASS_LABEL_KEY = "label";	
	public static final String CLASS_LABEL_STANDING = "standing";
	public static final String CLASS_LABEL_WALKING = "walking";
	public static final String CLASS_LABEL_RUNNING = "running";
	
	// Some consts for the feature names in the feature vector
	public static final String FEAT_FFT_COEF_LABEL = "fft_coef_";
	public static final String FEAT_MAX_LABEL = "max";
	public static final String FEAT_SET_NAME = "accelerometer_features";
	
	// Consts for GCM
	// Google API project id registered to use GCM.
	// Different student should have different ones.
	public static final String SENDER_ID = "60149575056";
	// URL of the AppEngine Server 
	// Different student should have different ones. http://129.170.212.209/
	public static final String SERVER_URL = "http://10.31.193.234:8888";
    // Intent used to display a message in the screen.
	public static final String DISPLAY_MESSAGE_ACTION = "DISPLAY_MESSAGE";
    //Intent's extra that contains the message to be displayed.
	public static final String EXTRA_MESSAGE = "message";
	public static String UPDATE = "Update";
	public static String DELETE = "Delete";
	public static String SEPARATOR = ":"; 
	
	//GAE
	public static String getSenderID(Context context) {
		TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		
		return telMgr.getDeviceId();
	}
}
