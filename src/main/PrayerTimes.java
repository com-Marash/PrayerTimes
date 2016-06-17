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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import dto.Coordination;
import dto.MethodDetails;
import dto.sunPositionData;
import dto.prayerTimesData;
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
	
	public prayerTimesData getTimes(int[] date, Coordination coords, Double timezone, Boolean dst, timeFormats format) {
		lat = coords.getLat();
		lng = coords.getLng(); 
		elv = (coords.getLng() == null) ? 0 : coords.getLng();
		timeFormat = (format == null)? timeFormat : format;
		
		// get the timeZone based on the input date
		if (timezone == null){
			timezone = getTimeZone(date);
		}
		
		if (dst == null) 
			dst = getDst(date);
		
		// add daylight saving
		timeZone = timezone + (dst ? 1 : 0);
		
		jDate = julian(date[0], date[1], date[2])- lng/ (15* 24);
		
		return computeTimes();
	}
	
	// convert float time to the given format (see timeFormats)
	public	String getFormattedTime(double time, timeFormats format, String[] suffixes) {
		
		//if (time == null)
		//	return invalidTime;
		
		if (format == timeFormats.t_Float)
				return String.valueOf(time);
		
		if (suffixes == null){
			suffixes = timeSuffixes;
		}
		
		time = DMath.fixHour(time+ 0.5/ 60);  // add 0.5 minutes to round
		double hours = Math.floor(time); 
		double minutes = Math.floor((time- hours)* 60);
		String suffix = (format.equals(timeFormats.t_12h)) ? suffixes[hours < 12 ? 0 : 1] : "";
		double hour = (format.equals(timeFormats.t_24h)) ? twoDigitsFormat(hours) : ((hours+ 12 -1)% 12+ 1);
		return hour+ ":"+ this.twoDigitsFormat(minutes)+ (!suffix.isEmpty() ? " "+ suffix : "");
	}
	
	//---------------------- Calculation Functions -----------------------


	// compute mid-day time
	public double midDay(double time) {
		// sunPosition returns an object with equation and declination
		double eqt = sunPosition(jDate+ time).getEquation();
		double noon = DMath.fixHour(12- eqt);
		return noon;
	}


	// compute the time at which sun reaches a specific angle below horizon
	public double sunAngleTime(double angle, double time, boolean isDirectionCCW) {
		
		double decl = sunPosition(jDate+ time).getDeclination();
		
		double noon = midDay(time);
		
		double t = 1/15 * DMath.arccos((-DMath.sin(angle)- DMath.sin(decl)* DMath.sin(lat))/ 
				(DMath.cos(decl)* DMath.cos(lat)));
		return noon+ (isDirectionCCW ? -t : t);
	}


	// compute asr time 
	public double asrTime(double factor, double time) {
		
		double decl = sunPosition(jDate+ time).getDeclination();
		double angle = -DMath.arccot(factor+ DMath.tan(Math.abs(lat- decl)));
		return sunAngleTime(angle, time, false);
	}


	// compute declination angle of sun and equation of time
	// Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
	public sunPositionData sunPosition (double jd) {
		double D = jd - 2451545.0;
		double g = DMath.fixAngle(357.529 + 0.98560028* D);
		double q = DMath.fixAngle(280.459 + 0.98564736* D);
		double L = DMath.fixAngle(q + 1.915* DMath.sin(g) + 0.020* DMath.sin(2*g));

		double R = 1.00014 - 0.01671* DMath.cos(g) - 0.00014* DMath.cos(2*g);
		double e = 23.439 - 0.00000036* D;

		double RA = DMath.arctan2(DMath.cos(e)* DMath.sin(L), DMath.cos(L))/ 15;
		double eqt = q/15 - DMath.fixHour(RA);
		double decl = DMath.arcsin(DMath.sin(e)* DMath.sin(L));

		return new sunPositionData(decl, eqt);
	}


	// convert Gregorian date to Julian day
	// Ref: Astronomical Algorithms by Jean Meeus
	public double julian (int year, int month, int day) {
		if (month <= 2) {
			year -= 1;
			month += 12;
		};
		double A = Math.floor(year/ 100);
		double B = 2- A+ Math.floor(A/ 4);
		
		// JD
		return (Math.floor(365.25* (year+ 4716))+ Math.floor(30.6001* (month+ 1))+ day+ B- 1524.5);
	}


	//---------------------- Compute Prayer Times -----------------------

	
	// compute prayer times at given julian date
	public prayerTimesData computePrayerTimes (prayerTimesData times) {
		times = dayPortion(times);
		MethodDetails params  = setting;

		double imsak   = this.sunAngleTime(eval(params.getImsakMin()), times.getImsak(), true);
		double fajr    = this.sunAngleTime(eval(params.getFajr()), times.getFajr(), true);
		double sunrise = this.sunAngleTime(riseSetAngle(), times.getSunrise(), true);
		double dhuhr   = this.midDay(times.getDhuhr());
		double asr     = this.asrTime(asrFactor(params.getAsr()), times.getAsr());
		double sunset  = this.sunAngleTime(riseSetAngle(), times.getSunset() , false);;
		double maghrib = this.sunAngleTime(eval(params.getMaghrib()), times.getMaghrib(), false);
		double isha    = this.sunAngleTime(eval(params.getIsha()), times.getIsha(), false);

		return new prayerTimesData(imsak, fajr, sunrise, dhuhr, asr, sunset, maghrib, isha, null);
	}

	
	// compute prayer times
	public prayerTimesData computeTimes() {
		// default times
		prayerTimesData times = new prayerTimesData(5, 5, 6, 12, 13, 18, 18, 18, null);

		// main iterations
		for (int i=1 ; i<=numIterations ; i++)
			times = this.computePrayerTimes(times);

		times = this.adjustTimes(times);

		// add midnight time
		times.setMidnight( (setting.getMidnight().equals(midnightMethods.Jafari)) ?
				times.getSunset()+ timeDiff(times.getSunset(), times.getFajr())/ 2 :
				times.getSunset()+ timeDiff(times.getSunset(), times.getSunrise())/ 2);

		times = this.tuneTimes(times);
		return times;
		//return modifyFormats(times);
	}


	// adjust times
	private prayerTimesData adjustTimes( prayerTimesData times) {
		MethodDetails params = setting;

		times.setAsr(times.getAsr() + timeZone - lng/ 15);
		times.setImsak(times.getImsak() + timeZone - lng/ 15);
		times.setFajr(times.getFajr() + timeZone - lng/ 15);
		times.setSunrise(times.getSunrise() + timeZone - lng/ 15);
		times.setDhuhr(times.getDhuhr() + timeZone - lng/ 15);
		times.setSunset(times.getSunset() + timeZone - lng/ 15);
		times.setMaghrib(times.getMaghrib() + timeZone - lng/ 15);
		times.setIsha(times.getIsha() + timeZone - lng/ 15);
		
		
		if (!params.getHighLats().equals(highLatMethods.None))
			times = adjustHighLats(times);

		
		times.setImsak( times.getFajr() - eval(params.getImsakMin())/ 60);
		
		times.setMaghrib(times.getSunset()+ eval(params.getMaghrib())/ 60);
		
		times.setIsha(times.getMaghrib()+ eval(params.getIsha())/ 60);
		times.setDhuhr( times.getDhuhr() + eval(params.getDhuhrMin())/ 60);

		return times;
	}


	// get asr shadow factor
	public double asrFactor(asrJuristics asrParam) {
		
		if(asrParam == asrJuristics.Standard){
			return 1;
		}else if (asrParam == asrJuristics.Hanafi){
			return 2;
		}
		throw new Exception("asrParam is unset");
	}
	public double asrFactor(int asrParam) {
		return eval(asrParam);
	}

	// return sun angle for sunset/sunrise
	private double riseSetAngle() {
		//var earthRad = 6371009; // in meters
		//var angle = DMath.arccos(earthRad/(earthRad+ elv));
		double angle = 0.0347* Math.sqrt(elv); // an approximation
		return 0.833+ angle;
	}


	// apply offsets to the times
	private prayerTimesData tuneTimes(prayerTimesData times) {

		times.setAsr(times.getAsr() + offset[0]/ 60);
		times.setImsak(times.getImsak() + offset[1]/ 60);
		times.setFajr(times.getFajr() + offset[2]/ 60);
		times.setSunrise(times.getSunrise() + offset[3]/ 60);
		times.setDhuhr(times.getDhuhr() + offset[4]/ 60);
		times.setSunset(times.getSunset() + offset[5]/ 60);
		times.setMaghrib(times.getMaghrib() + offset[6]/ 60);
		times.setIsha(times.getIsha() + offset[7]/ 60);
		times.setMidnight(times.getMidnight() + offset[8]/ 60);
		
		return times;
	}


	// convert times to given time format
	public prayerTimesData modifyFormats(prayerTimesData times) {
		
		times.setImsak(getFormattedTime(times.getImsak(), timeFormat, null));
		times.setFajr (getFormattedTime(times.getFajr(), timeFormat, null));
		times.sunrise = getFormattedTime(times.sunrise, timeFormat);
		times.dhuhr= getFormattedTime(times.dhuhr, timeFormat);
		times.asr= getFormattedTime(times.asr, timeFormat);
		times.sunset= getFormattedTime(times.sunset, timeFormat);
		times.maghrib= getFormattedTime(times.maghrib, timeFormat);
		times.isha= getFormattedTime(times.isha, timeFormat);
	        
		return times;
	}


	// adjust times for locations in higher latitudes
	private prayerTimesData adjustHighLats(prayerTimesData times) {
		var params = setting;
		var nightTime = this.timeDiff(times.sunset, times.sunrise);

		times.imsak = this.adjustHLTime(times.imsak, times.sunrise, this.eval(params.imsak), nightTime, 'ccw');
		times.fajr  = this.adjustHLTime(times.fajr, times.sunrise, this.eval(params.fajr), nightTime, 'ccw');
		times.isha  = this.adjustHLTime(times.isha, times.sunset, this.eval(params.isha), nightTime);
		times.maghrib = this.adjustHLTime(times.maghrib, times.sunset, this.eval(params.maghrib), nightTime);

		return times;
	}

	// adjust a time for higher latitudes
	adjustHLTime: function(time, base, angle, night, direction) {
		var portion = this.nightPortion(angle, night);
		var timeDiff = (direction == 'ccw') ?
			this.timeDiff(time, base):
			this.timeDiff(base, time);
		if (isNaN(time) || timeDiff > portion)
			time = base+ (direction == 'ccw' ? -portion : portion);
		return time;
	},


	// the night portion used for adjusting times in higher latitudes
	nightPortion: function(angle, night) {
		var method = setting.highLats;
		var portion = 1/2 // MidNight
		if (method == 'AngleBased')
			portion = 1/60* angle;
		if (method == 'OneSeventh')
			portion = 1/7;
		return portion* night;
	},


	// convert hours to day portions
	public prayerTimesData dayPortion( prayerTimesData times) {
		for (var i in times)
			times[i] /= 24;
		return times;
	}


	//---------------------- Time Zone Functions -----------------------


	// get local time zone
	private double getTimeZone(int[] date) {
		int year = date[0];
		double t1 = gmtOffset(new int[]{year, 1, 1});
		double t2 = gmtOffset(new int[]{year, 6, 1});
		return Math.min(t1, t2);
	}


	// get daylight saving for a given date
	private boolean getDst(int[] date) {
		if (gmtOffset(date) != getTimeZone(date)){
			return true;
		}else{
			return false;
		}
	}

	// GMT offset for a given date
	private double gmtOffset(int[] date) {
		
		Calendar requestedDate = new GregorianCalendar(date[0], date[1]-1, date[2]);		
		TimeZone tz = Calendar.getInstance().getTimeZone();
		double offset = tz.getOffset(requestedDate.getTimeInMillis())  /  (1000 * 60 * 60);
		return offset;
	}


	//---------------------- Misc Functions -----------------------

	// convert given string into a number
	public double eval(String str) {
		return Double.parseDouble(str.split("[^0-9.+-]")[0]);
	}
	
	public double eval(double str) {
		return str;
	}
	
	public double eval(int str) {
		return str;
	}

	// compute the difference between two times
	private double timeDiff(double time1, double time2) {
		return DMath.fixHour(time2- time1);
	}


	// add a leading 0 if necessary
	private double twoDigitsFormat(double num) {
		return (num <10) ? '0'+ num : num;
	}

}
