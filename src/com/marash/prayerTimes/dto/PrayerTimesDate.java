package com.marash.prayerTimes.dto;

import com.marash.prayerTimes.main.DMath;

public class PrayerTimesDate {
	private Double time;
	private int hour;
	private int min;
	
	public PrayerTimesDate(Double time){
		this.setTime(time);
	}
	
	public void setTime(Double time){
		this.time = time;
		calculateTimes();
	}
	public int getHour(){
		return hour;
	}
	
	public int getMin(){
		return min;
	}
	
	public Double getTime(){
		return time;
	}
	
	public String getFormatedTime(){
		String hourString = String.valueOf(hour);
		String minString = String.valueOf(min);
		if (hour < 10){
			hourString = "0" + hourString;
		}
		if (min <10){
			minString = "0" + minString;
		}
		return hourString + ":" + minString;
	}
	
	private void calculateTimes(){
		if (time != null){
			Double tempTime = DMath.fixHour(time+ 0.5/ 60);  // add 0.5 minutes to round
			hour = (int) Math.floor(tempTime); 
			min = (int) Math.floor((tempTime- hour)* 60);
		}
	}
}
