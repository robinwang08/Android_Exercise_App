package edu.dartmouth.cs.myruns.gae;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


public class HistoryEntry {
	public long id;
	public int inputType;
	public int activityType;
	public long dateTime;
	public int duration;
	public double distance;
	public double avgSpeed;
	public int calorie;
	public double climb;
	public int heartrate;
	public String comment;
	
	public com.google.appengine.labs.repackaged.org.json.JSONObject fromJSONObject(JSONObject obj) {		
		try {
			id = obj.getInt("id");
			inputType = obj.getInt("inputType");
			activityType = obj.getInt("activityType");
			dateTime = obj.getLong("dateTime" );
			duration = obj.getInt("duration");
			distance =  obj.getDouble("distance");
			avgSpeed = obj.getDouble("avgSpeed" );
			calorie = obj.getInt("calorie" );
			climb = obj.getDouble("climb");
			heartrate = obj.getInt("heartrate");
			comment = obj.getString("comment");
		} catch (JSONException e) {
			return null;
		}
		return obj;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		
		try {
			obj.put("id", id);
			obj.put("inputType",inputType );
			obj.put("activityType", activityType);
			obj.put("dateTime",dateTime );
			obj.put("duration", duration);
			obj.put("distance", distance);
			obj.put("avgSpeed",avgSpeed );
			obj.put("calorie",calorie );
			obj.put("climb", climb);
			obj.put("heartrate", heartrate);
			obj.put("comment", comment);
		} catch (JSONException e) {
			return null;
		}

		return obj;
	}
}
