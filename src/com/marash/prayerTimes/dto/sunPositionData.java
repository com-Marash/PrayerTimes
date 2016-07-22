package com.marash.prayerTimes.dto;

public class sunPositionData {
	private double declination;
	private double equation;
	
	public sunPositionData(double declination, double equation){
		this.declination = declination;
		this.equation = equation;
	}
	
	public double getDeclination() {
		return declination;
	}
	public void setDeclination(double declination) {
		this.declination = declination;
	}
	public double getEquation() {
		return equation;
	}
	public void setEquation(double equation) {
		this.equation = equation;
	}
}
