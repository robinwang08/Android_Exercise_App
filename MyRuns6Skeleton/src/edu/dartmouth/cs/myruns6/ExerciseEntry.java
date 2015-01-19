package edu.dartmouth.cs.myruns6;

import java.util.Date;

// Definition of a exercise entry
// Member variables are accessed through setters and getters
// And it's actually not directly exposed for use, see ExerciseEntryHelper.java
// We can give this to students

public class ExerciseEntry {
	
	private Long id;
	
	private long remoteId;
	private int inputType;
	private int activityType;
	private Date dateTime;
	private int duration;
	private double distance;
	private double avgPace; 
	private double avgSpeed;
	private int calorie;
	private double climb;
	private int heartrate;
	private String comment;
	
	public ExerciseEntry(){
		this.remoteId = -1L;
		this.inputType = -1;
		this.activityType = -1;
		this.dateTime = new Date(System.currentTimeMillis());
		this.duration = 0;
		this.distance = 0;
		this.avgPace = 0;
		this.avgSpeed = 0;
		this.calorie = 0;
		this.climb = 0;
		this.heartrate = 0;
		this.comment = "";
	}

	public long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(long remoteId) {
		this.remoteId = remoteId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getAvgPace() {
		return avgPace;
	}

	public void setAvgPace(double avgPace) {
		this.avgPace = avgPace;
	}

	public double getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(double avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public int getCalorie() {
		return calorie;
	}

	public void setCalorie(int calorie) {
		this.calorie = calorie;
	}

	public double getClimb() {
		return climb;
	}

	public void setClimb(double climb) {
		this.climb = climb;
	}

	public int getHeartrate() {
		return heartrate;
	}

	public void setHeartrate(int heartrate) {
		this.heartrate = heartrate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
}
