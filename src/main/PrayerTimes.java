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
import dto.sunPositionData;
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
		String suffix = format == timeFormats.t_12h ? suffixes[hours < 12 ? 0 : 1] : "";
		double hour = format == timeFormats.t_24h ? twoDigitsFormat(hours) : ((hours+ 12 -1)% 12+ 1);
		return hour+ ":"+ this.twoDigitsFormat(minutes)+ (suffix ? " "+ suffix : "");
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
	public double computePrayerTimes (double times) {
		times = this.dayPortion(times);
		var params  = setting;

		double imsak   = this.sunAngleTime(this.eval(params.imsak), times.imsak, true);
		double fajr    = this.sunAngleTime(this.eval(params.fajr), times.fajr, true);
		double sunrise = this.sunAngleTime(this.riseSetAngle(), times.sunrise, true);
		double dhuhr   = this.midDay(times.dhuhr);
		double asr     = this.asrTime(this.asrFactor(params.asr), times.asr);
		double sunset  = this.sunAngleTime(this.riseSetAngle(), times.sunset);;
		double maghrib = this.sunAngleTime(this.eval(params.maghrib), times.maghrib);
		double isha    = this.sunAngleTime(this.eval(params.isha), times.isha);

		return {
			imsak: imsak, fajr: fajr, sunrise: sunrise, dhuhr: dhuhr,
			asr: asr, sunset: sunset, maghrib: maghrib, isha: isha
		};
	},


	// compute prayer times
	computeTimes: function() {
		// default times
		var times = {
			imsak: 5, fajr: 5, sunrise: 6, dhuhr: 12,
			asr: 13, sunset: 18, maghrib: 18, isha: 18
		};

		// main iterations
		for (var i=1 ; i<=numIterations ; i++)
			times = this.computePrayerTimes(times);

		times = this.adjustTimes(times);

		// add midnight time
		times.midnight = (setting.midnight == 'Jafari') ?
				times.sunset+ this.timeDiff(times.sunset, times.fajr)/ 2 :
				times.sunset+ this.timeDiff(times.sunset, times.sunrise)/ 2;

		times = this.tuneTimes(times);
		return this.modifyFormats(times);
	},


	// adjust times
	adjustTimes: function(times) {
		var params = setting;
		for (var i in times)
			times[i] += timeZone- lng/ 15;

		if (params.highLats != 'None')
			times = this.adjustHighLats(times);

		if (this.isMin(params.imsak))
			times.imsak = times.fajr- this.eval(params.imsak)/ 60;
		if (this.isMin(params.maghrib))
			times.maghrib = times.sunset+ this.eval(params.maghrib)/ 60;
		if (this.isMin(params.isha))
			times.isha = times.maghrib+ this.eval(params.isha)/ 60;
		times.dhuhr += this.eval(params.dhuhr)/ 60;

		return times;
	},


	// get asr shadow factor
	asrFactor: function(asrParam) {
		var factor = {Standard: 1, Hanafi: 2}[asrParam];
		return factor || this.eval(asrParam);
	},


	// return sun angle for sunset/sunrise
	riseSetAngle: function() {
		//var earthRad = 6371009; // in meters
		//var angle = DMath.arccos(earthRad/(earthRad+ elv));
		var angle = 0.0347* Math.sqrt(elv); // an approximation
		return 0.833+ angle;
	},


	// apply offsets to the times
	tuneTimes: function(times) {
		for (var i in times)
			times[i] += offset[i]/ 60;
		return times;
	},


	// convert times to given time format
	modifyFormats: function(times) {
		for (var i in times)
			times[i] = this.getFormattedTime(times[i], timeFormat);
		return times;
	},


	// adjust times for locations in higher latitudes
	adjustHighLats: function(times) {
		var params = setting;
		var nightTime = this.timeDiff(times.sunset, times.sunrise);

		times.imsak = this.adjustHLTime(times.imsak, times.sunrise, this.eval(params.imsak), nightTime, 'ccw');
		times.fajr  = this.adjustHLTime(times.fajr, times.sunrise, this.eval(params.fajr), nightTime, 'ccw');
		times.isha  = this.adjustHLTime(times.isha, times.sunset, this.eval(params.isha), nightTime);
		times.maghrib = this.adjustHLTime(times.maghrib, times.sunset, this.eval(params.maghrib), nightTime);

		return times;
	},


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
	dayPortion: function(times) {
		for (var i in times)
			times[i] /= 24;
		return times;
	},


	//---------------------- Time Zone Functions -----------------------


	// get local time zone
	getTimeZone: function(date) {
		var year = date[0];
		var t1 = this.gmtOffset([year, 0, 1]);
		var t2 = this.gmtOffset([year, 6, 1]);
		return Math.min(t1, t2);
	},


	// get daylight saving for a given date
	getDst: function(date) {
		return 1* (this.gmtOffset(date) != this.getTimeZone(date));
	},


	// GMT offset for a given date
	gmtOffset: function(date) {
		var localDate = new Date(date[0], date[1]- 1, date[2], 12, 0, 0, 0);
		var GMTString = localDate.toGMTString();
		var GMTDate = new Date(GMTString.substring(0, GMTString.lastIndexOf(' ')- 1));
		var hoursDiff = (localDate- GMTDate) / (1000* 60* 60);
		return hoursDiff;
	},


	//---------------------- Misc Functions -----------------------

	// convert given string into a number
	public double eval(String str) {
		return Double.parseDouble(str.split("[^0-9.+-]")[0]);
	}


	// detect if input contains 'min'
	isMin: function(arg) {
		return (arg+ '').indexOf('min') != -1;
	},


	// compute the difference between two times
	timeDiff: function(time1, time2) {
		return DMath.fixHour(time2- time1);
	},


	// add a leading 0 if necessary
	twoDigitsFormat: function(num) {
		return (num <10) ? '0'+ num : num;
	}

}}

			
}
