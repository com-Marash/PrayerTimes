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
	public int getTimes(date, Coordination coords, timezone, dst, timeFormats format) {
		lat = coords.getLat();
		lng = coords.getLng(); 
		elv = (coords.getLng() == null) ? 0 : coords.getLng();
		timeFormat = (format == null)? timeFormat : format;
		if (date.constructor === Date)
			date = [date.getFullYear(), date.getMonth()+ 1, date.getDate()];
		if (typeof(timezone) == 'undefined' || timezone == 'auto')
			timezone = this.getTimeZone(date);
		if (typeof(dst) == 'undefined' || dst == 'auto') 
			dst = this.getDst(date);
		timeZone = 1* timezone+ (1* dst ? 1 : 0);
		jDate = this.julian(date[0], date[1], date[2])- lng/ (15* 24);
		
		return this.computeTimes();
	};
			
}
