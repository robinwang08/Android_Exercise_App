package edu.dartmouth.cs.myruns6.gae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import android.content.Context;
import android.database.Cursor;

import com.google.android.gcm.GCMRegistrar;

import edu.dartmouth.cs.myruns6.Globals;

public class HistoryUploader {

	private Context mContext;
	private String mServerUrl;

	public HistoryUploader(Context context, String serverUrl) {
		mContext = context;
		mServerUrl = serverUrl;
	}

	public boolean upload(Cursor cursor) throws IOException {
		if (!cursor.moveToFirst()) {
			return false;
		}

		// convert entrys to string
		ArrayList<HistoryEntry> entryList = new ArrayList<HistoryEntry>();
		do {
			HistoryEntry entry  = getEntryFromCursor(cursor);
			if(entry != null) {
				entryList.add(entry);
			}
		} while (cursor.moveToNext());

		String entryListString = convertToString(entryList);

		// upload
		final String regId = GCMRegistrar.getRegistrationId(mContext);

		Map<String, String> params = new HashMap<String, String>();
		params.put("regId", regId);
		params.put("data", entryListString);
		ServerUtilities.post(mServerUrl, params);

		return true;
	}

	private String convertToString(ArrayList<HistoryEntry> entryList) {
		
		JSONArray jsonArray = new JSONArray();
		for(HistoryEntry entry:entryList) {
			jsonArray.put(entry.toJSONObject());
		}
		String entryListStr = jsonArray.toString();

//		HistoryEntry entry0 = new HistoryEntry();
//		try {
//			JSONArray testArray = new JSONArray(entryListStr);
//			JSONObject object = testArray.getJSONObject(0);
//
//			entry0.fromJSONObject(object);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return entryListStr;
	}

	private  HistoryEntry getEntryFromCursor(Cursor cursor) {
		int idIndex = cursor.getColumnIndex(Globals.KEY_ROWID);
		int mInputTypeIndex = cursor
				.getColumnIndex(Globals.KEY_INPUT_TYPE);
		int activityTypeIndex = cursor
				.getColumnIndex(Globals.KEY_ACTIVITY_TYPE);
		int dateTimeIndex = cursor
				.getColumnIndex(Globals.KEY_DATE_TIME);
		int durationIndex = cursor.getColumnIndex(Globals.KEY_DURATION);
		int distanceIndex = cursor.getColumnIndex(Globals.KEY_DISTANCE);
		int avgSpeedIndex = cursor
				.getColumnIndex(Globals.KEY_AVG_SPEED);
		int caloriesIndex = cursor.getColumnIndex(Globals.KEY_CALORIES);
		int climbIndex = cursor.getColumnIndex(Globals.KEY_CLIMB);
		int heartrateIndex = cursor
				.getColumnIndex(Globals.KEY_HEARTRATE);
		int commentIndex = cursor.getColumnIndex(Globals.KEY_COMMENT);

		HistoryEntry entry = new HistoryEntry();

		if (mInputTypeIndex != -1)
			entry.id = cursor.getInt(idIndex);
		
		if (mInputTypeIndex != -1)
			entry.inputType = cursor.getInt(mInputTypeIndex);
		if (activityTypeIndex != -1)
			entry.activityType = cursor.getInt(activityTypeIndex);
		if (dateTimeIndex != -1)
			entry.dateTime = cursor.getLong(dateTimeIndex);
		if (durationIndex != -1)
			entry.duration =  cursor.getInt(durationIndex);
		if (distanceIndex != -1)
			entry.distance = cursor.getDouble(distanceIndex);
		if (avgSpeedIndex != -1)
			entry.avgSpeed = cursor.getDouble(avgSpeedIndex);
		if (caloriesIndex != -1)
			entry.calorie = cursor.getInt(caloriesIndex);
		if (climbIndex != -1)
			entry.climb =  cursor.getDouble(climbIndex);
		if (heartrateIndex != -1)
			entry.heartrate = cursor.getInt(heartrateIndex);
		if (commentIndex != -1)
			entry.comment = cursor.getString(commentIndex);

		
		return entry;
	}

}
