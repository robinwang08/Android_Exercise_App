package edu.dartmouth.cs.myruns.gae;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public class HistoryEntryEntityConverter {

	public static HistoryEntry fromEntity(Entity entity) {
		HistoryEntry entry = new HistoryEntry();
		
		entry.id = (Long)entity.getProperty("row_id");
		entry.inputType = ((Long)entity.getProperty("inputType")).intValue();
		entry.activityType =((Long)entity.getProperty("activityType")).intValue();
		entry.dateTime =  ((Long)entity.getProperty("dateTime" )).intValue();
		entry.duration =((Long)entity.getProperty("duration")).intValue();
		entry.distance = (Double) entity.getProperty("distance");
		entry.avgSpeed =(Double)entity.getProperty("avgSpeed" );
		entry.calorie = ((Long)entity.getProperty("calorie" )).intValue();
		entry.climb = (Double)entity.getProperty("climb");
		entry.heartrate = ((Long)entity.getProperty("heartrate")).intValue();
		entry.comment =(String)entity.getProperty("comment");
		
		return entry;
	}

	public static Entity toEntity(HistoryEntry entry, String kind, Key parentKey) {
		Entity entity = new Entity(kind, Long.toString(entry.id), parentKey);

		entity.setProperty("row_id", entry.id);
		entity.setProperty("inputType", entry.inputType);
		entity.setProperty("activityType", entry.activityType);
		entity.setProperty("dateTime", entry.dateTime);
		entity.setProperty("duration", entry.duration);
		entity.setProperty("distance", entry.distance);
		entity.setProperty("avgSpeed", entry.avgSpeed);
		entity.setProperty("calorie", entry.calorie);
		entity.setProperty("climb", entry.climb);
		entity.setProperty("heartrate", entry.heartrate);
		entity.setProperty("comment", entry.comment);

		return entity;
	}

}
