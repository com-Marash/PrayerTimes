package com.marash.prayerTimes.dto;

import java.util.Calendar;

public class prayerTimesData {

	
	private PrayerTimesDate imsak;
	private PrayerTimesDate fajr;
	private PrayerTimesDate sunrise;
	private PrayerTimesDate dhuhr;
	private PrayerTimesDate asr;
	private PrayerTimesDate sunset;
	private PrayerTimesDate maghrib;
	private PrayerTimesDate isha;
	private PrayerTimesDate midnight;
	private Calendar calender; 
	
	public prayerTimesData(double imsak, double fajr, double sunrise, double dhuhr, double asr, double sunset,
			double maghrib, double isha, Double midnight) {
		this.imsak = new PrayerTimesDate(imsak);
		this.fajr= new PrayerTimesDate(fajr);
		this.sunrise = new PrayerTimesDate(sunrise);
		this.dhuhr = new PrayerTimesDate(dhuhr);
		this.asr = new PrayerTimesDate(asr);
		this.sunset = new PrayerTimesDate(sunset);
		this.maghrib = new PrayerTimesDate(maghrib);
		this.isha = new PrayerTimesDate(isha);
		this.midnight = new PrayerTimesDate(midnight);
		this.setCalender(null);
	}
	
	public prayerTimesData(double imsak, double fajr, double sunrise, double dhuhr, double asr, double sunset,
			double maghrib, double isha, Double midnight, Calendar calender ) {
		this.imsak = new PrayerTimesDate(imsak);
		this.fajr= new PrayerTimesDate(fajr);
		this.sunrise = new PrayerTimesDate(sunrise);
		this.dhuhr = new PrayerTimesDate(dhuhr);
		this.asr = new PrayerTimesDate(asr);
		this.sunset = new PrayerTimesDate(sunset);
		this.maghrib = new PrayerTimesDate(maghrib);
		this.isha = new PrayerTimesDate(isha);
		this.midnight = new PrayerTimesDate(midnight);
		this.setCalender(calender);
	}

	public prayerTimesData() {
		
	}

	public PrayerTimesDate getImsak() {
		return imsak;
	}

	public void setImsak(double imsak) {
		this.imsak.setTime(imsak);
	}
	
	public PrayerTimesDate getFajr() {
		return fajr;
	}

	public void setFajr(double fajr) {
		this.fajr.setTime(fajr);
	}

	public PrayerTimesDate getSunrise() {
		return sunrise;
	}

	public void setSunrise(double sunrise) {
		this.sunrise.setTime(sunrise);
	}

	public PrayerTimesDate getDhuhr() {
		return dhuhr;
	}

	public void setDhuhr(double dhuhr) {
		this.dhuhr.setTime(dhuhr);
	}

	public PrayerTimesDate getAsr() {
		return asr;
	}

	public void setAsr(double asr) {
		this.asr.setTime(asr);
	}

	public PrayerTimesDate getSunset() {
		return sunset;
	}

	public void setSunset(double sunset) {
		this.sunset.setTime(sunset);
	}

	public PrayerTimesDate getMaghrib() {
		return maghrib;
	}

	public void setMaghrib(double maghrib) {
		this.maghrib.setTime(maghrib);
	}

	public PrayerTimesDate getIsha() {
		return isha;
	}

	public void setIsha(double isha) {
		this.isha.setTime(isha);
	}

	public PrayerTimesDate getMidnight() {
		return midnight;
	}

	public void setMidnight(Double midnight) {
		this.midnight.setTime(midnight);
	}

	public Calendar getCalender() {
		return calender;
	}

	public void setCalender(Calendar calender) {
		this.calender = calender;
	}
	
}
