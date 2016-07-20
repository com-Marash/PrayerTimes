package dto;

import main.DMath;

public class prayerTimesDouble {
	private Double time;
	private int hour;
	private int min;
	
	public prayerTimesDouble(Double time){
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
		return hour + ":" + min;
	}
	
	private void calculateTimes(){
		if (time != null){
			Double tempTime = DMath.fixHour(time+ 0.5/ 60);  // add 0.5 minutes to round
			hour = (int) Math.floor(tempTime); 
			min = (int) Math.floor((tempTime- hour)* 60);
		}
	}
}
