package edu.dartmouth.cs.myruns;



/**
 * Helper class for converting a String to an Exercise entry.
 */
public class ExerciseEntry {

	public String mId;
	public int mDuration;
	public String mActivityType;
	public static final String[] ACTIVITY_TYPES = {"Running", "Cycling", "Walking", "Hiking", 
		"Downhill Skiing", "Cross-Country Skiing", "Snowboarding", "Skating", "Swimming", 
		"Mountain Biking", "Wheelchair", "Elliptical", "Other",  "Standing"};

	/**
	 * Convert from a String to an ExerciseEntry
	 *
	 * @param The data posted by the device
	 */
	public static ExerciseEntry string2Entry(String data) {
		ExerciseEntry entry = new ExerciseEntry();
		// In the client, we use ";" as the splitter
		String [] items = data.split(";");
		int index;
		for (String str : items ) {
			// Find id value
			index = str.indexOf("id");
			if (index != -1) {
				entry.mId = str.substring(index + 3); 
			}
			// Find Duration value
			index = str.indexOf("Duration");
			if (index != -1) {
				entry.mDuration = Integer.parseInt(str.substring(index + 9)) / 60; 
			}
			// Find ActivityType value
			index = str.indexOf("ActivityType");
			if (index != -1) {
				entry.mActivityType = ExerciseEntry.ACTIVITY_TYPES[Integer.parseInt(str.substring(index + 13))]; 
			}

		}
		return entry;
	}

	/**
	 * Get the entry ID from the String data.
	 * If no ID, returns an empty String.
	 *
	 * @param The data posted by the device
	 */
	public static String string2Id(String data) {
		String id = new String();
		// In the client, we use ";" as the splitter
		String [] items = data.split(";");
		int index;
		for (String str : items ) {
			// Find id value
			index = str.indexOf("id");
			if (index != -1) {
				id = str.substring(index + 3); 
			}
		
		}
		return id;
	}

	/**
	 * @return the String representation of a linked list element.
	 */
	public String toString() {
		String result = new String();

		result += "id=" + mId + "; ";
		result += " Totoal duration is " + mDuration + " Minutes; ";
		result += " Activity is " + mActivityType +".";

		return result;
	}
}
