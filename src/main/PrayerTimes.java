/*
 * This code is using prayerTimes.org as main resource.
 * 
 * Development: Marash Company
 * License: MIT
 * Url: https://github.com/com-Marash/PrayerTimes
 * Version: 0.0.1-preAlpha 
 * 
 */

package main;
import java.lang.reflect.Method;
import java.util.ArrayList;

import dto.Coordination;
import dto.MethodDetails;
import methodDetailsEnums.asrJuristics;
import methodDetailsEnums.highLatMethods;
import methodDetailsEnums.midnightMethods;



public class PrayerTimes {
	
	public enum timeNames{
		Imsak, Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha, Midnight
	}
	
	
	public enum methods{
		
		MWL(new MethodDetails("Muslim World League", 18, 17, -1, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle)),
		ISNA(new MethodDetails("Islamic Society of North America (ISNA)", 15, 15, -1, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle)),
		Egypt(new MethodDetails("Egyptian General Authority of Survey", 19.5, 17.5, -1, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle)),
		Makkah(new MethodDetails("Umm Al-Qura University, Makkah", 18.5, -1, -1, midnightMethods.Standard, 90, 0 , 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle)),
		Karachi(new MethodDetails("University of Islamic Sciences, Karachi", 18, 18, -1, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle)),
		Tehran(new MethodDetails("Institute of Geophysics, University of Tehran", 17.7, 14, 4.5, midnightMethods.Jafari, -1, -1, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle)),
		Jafari(new MethodDetails("Shia Ithna-Ashari, Leva Institute, Qum", 16, 14, 4, midnightMethods.Jafari, -1, -1, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle));
		
		private MethodDetails details;
		
		methods(MethodDetails details){
			this.details = details;
		}
		
		public MethodDetails getDetails() {
			return details;
		}
	}
	
	
	public enum timeFormats{
		t_24h,         // 24-hour format
		t_12h,         // 12-hour format
		t_12hNS,       // 12-hour format with no suffix
		t_Float        // floating point number
	}
	
	//---------------------- Default Settings --------------------
	//----------------------- Local Variables ---------------------
	
	private methods calcMethod;

	private MethodDetails setting;
	
	private timeFormats timeFormat;
	private String[] timeSuffixes;
	private String invalidTime;
	private int numIterations;
	
	// offset for Imsak, Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha, Midnight
	private int[] offset;
	
	private double lat, lng, elv,       // coordinates
	timeZone, jDate;     // time variables
	

	// constructor
	
	public PrayerTimes() {
		
		timeFormat = timeFormats.t_24h;
		timeSuffixes = new String[]{"am","pm"};
		invalidTime = "-----";
		numIterations = 1;
		offset = new int[]{0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0};
		
		// default calcMethod
		calcMethod = methods.MWL;
		// set the setting based on default method
		setting = calcMethod.getDetails();
		
	}		
			
	//----------------------- Public Functions ------------------------
			
	// set calculation method 
	public void setMethod(methods method) {		
		this.adjust(method.getDetails());
		calcMethod = method;
	}

	// set calculating parameters
	public void adjust (MethodDetails details) {
		setting = details;
	}

	// offset for Imsak, Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha, Midnight
	// set time offsets
	public void tune (int[] timeOffsets) {
		offset = timeOffsets;
	};

	// get current calculation method
	public methods getMethod(){
		return calcMethod; 
	};

	// get current setting
	public MethodDetails getSetting(){
		return setting;
	};

	// get current time offsets
	public int[] getOffsets() {
		return offset;
	};
	
	// return prayer times for a given date
	// date is an array of three integers: [fullYear, month, day]
	// coordination
	// timezone : Double can be -12 to +12
	// dst refers to daylight saving time it can true (+1 hour) or false (+0 hour) 
	
	public int getTimes(int[] date, Coordination coords, Double timezone, Boolean dst, timeFormats format) {
		lat = coords.getLat();
		lng = coords.getLng(); 
		elv = (coords.getLng() == null) ? 0 : coords.getLng();
		timeFormat = (format == null)? timeFormat : format;
		
		// get the timeZone based on the input date
		if (timezone == null){
			timezone = this.getTimeZone(date);
		}
		
		if (dst == null) 
			dst = this.getDst(date);
		
		// add daylight saving
		timeZone = timezone + (dst ? 1 : 0);
		
		jDate = this.julian(date[0], date[1], date[2])- lng/ (15* 24);
		
		return this.computeTimes();
	}
	
	// convert float time to the given format (see timeFormats)
	public	getFormattedTime(time, format, suffixes) {
		if (isNaN(time))
			return invalidTime;
		if (format == 'Float') return time;
		suffixes = suffixes || timeSuffixes;
		
		time = DMath.fixHour(time+ 0.5/ 60);  // add 0.5 minutes to round
		var hours = Math.floor(time); 
		var minutes = Math.floor((time- hours)* 60);
		var suffix = (format == '12h') ? suffixes[hours < 12 ? 0 : 1] : '';
		var hour = (format == '24h') ? this.twoDigitsFormat(hours) : ((hours+ 12 -1)% 12+ 1);
		return hour+ ':'+ this.twoDigitsFormat(minutes)+ (suffix ? ' '+ suffix : '');
	}
	
	//---------------------- Calculation Functions -----------------------


	// compute mid-day time
	midDay: function(time) {
		var eqt = this.sunPosition(jDate+ time).equation;
		var noon = DMath.fixHour(12- eqt);
		return noon;
	},


	// compute the time at which sun reaches a specific angle below horizon
	sunAngleTime: function(angle, time, direction) {
		var decl = this.sunPosition(jDate+ time).declination;
		var noon = this.midDay(time);
		var t = 1/15* DMath.arccos((-DMath.sin(angle)- DMath.sin(decl)* DMath.sin(lat))/ 
				(DMath.cos(decl)* DMath.cos(lat)));
		return noon+ (direction == 'ccw' ? -t : t);
	},


	// compute asr time 
	asrTime: function(factor, time) { 
		var decl = this.sunPosition(jDate+ time).declination;
		var angle = -DMath.arccot(factor+ DMath.tan(Math.abs(lat- decl)));
		return this.sunAngleTime(angle, time);
	},


	// compute declination angle of sun and equation of time
	// Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
	sunPosition: function(jd) {
		var D = jd - 2451545.0;
		var g = DMath.fixAngle(357.529 + 0.98560028* D);
		var q = DMath.fixAngle(280.459 + 0.98564736* D);
		var L = DMath.fixAngle(q + 1.915* DMath.sin(g) + 0.020* DMath.sin(2*g));

		var R = 1.00014 - 0.01671* DMath.cos(g) - 0.00014* DMath.cos(2*g);
		var e = 23.439 - 0.00000036* D;

		var RA = DMath.arctan2(DMath.cos(e)* DMath.sin(L), DMath.cos(L))/ 15;
		var eqt = q/15 - DMath.fixHour(RA);
		var decl = DMath.arcsin(DMath.sin(e)* DMath.sin(L));

		return {declination: decl, equation: eqt};
	},


	// convert Gregorian date to Julian day
	// Ref: Astronomical Algorithms by Jean Meeus
	julian: function(year, month, day) {
		if (month <= 2) {
			year -= 1;
			month += 12;
		};
		var A = Math.floor(year/ 100);
		var B = 2- A+ Math.floor(A/ 4);

		var JD = Math.floor(365.25* (year+ 4716))+ Math.floor(30.6001* (month+ 1))+ day+ B- 1524.5;
		return JD;
	},

	
			
}
