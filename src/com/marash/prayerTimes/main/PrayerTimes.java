/*
 * This code is using prayerTimes.org as main resource.
 * 
 * Development: Marash Company
 * License: MIT
 * Url: https://github.com/com-Marash/PrayerTimes
 * Version: 1.0.4-Beta 
 * 
 */

package com.marash.prayerTimes.main;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.marash.prayerTimes.dto.Coordination;
import com.marash.prayerTimes.dto.MethodDetails;
import com.marash.prayerTimes.dto.prayerTimesData;
import com.marash.prayerTimes.dto.sunPositionData;
import com.marash.prayerTimes.methodDetailsEnums.asrJuristics;
import com.marash.prayerTimes.methodDetailsEnums.highLatMethods;
import com.marash.prayerTimes.methodDetailsEnums.midnightMethods;



public class PrayerTimes {
	
	public enum methods{
		
		MWL(new MethodDetails("Muslim World League", 18, 17, -1, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle, "MWL")),
		ISNA(new MethodDetails("Islamic Society of North America (ISNA)", 15, 15, 0, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle, "ISNA")),
		Egypt(new MethodDetails("Egyptian General Authority of Survey", 19.5, 17.5, 0, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle, "Egypt")),
		Makkah(new MethodDetails("Umm Al-Qura University, Makkah", 18.5, -1, 0, midnightMethods.Standard, 90, 0 , 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle, "Makkah")),
		Karachi(new MethodDetails("University of Islamic Sciences, Karachi", 18, 18, 0, midnightMethods.Standard, -1, 0, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle, "Karachi")),
		Tehran(new MethodDetails("Institute of Geophysics, University of Tehran", 17.7, 14, 4.5, midnightMethods.Jafari, -1, -1, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle, "Tehran")),
		Jafari(new MethodDetails("Shia Ithna-Ashari, Leva Institute, Qum", 16, 14, 4, midnightMethods.Jafari, -1, -1, 10, 0, asrJuristics.Standard, highLatMethods.NightMiddle, "Jafari"));
		
		private MethodDetails details;
		
		private methods(MethodDetails details){
			this.details = details;
		}
		
		public MethodDetails getDetails() {
			return details;
		}
	}
	
	
	//---------------------- Default Settings --------------------
	//----------------------- Local Variables ---------------------
	
	private methods calcMethod;
	private MethodDetails setting;
	private int numIterations;
	
	// offset for Imsak, Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha, Midnight
	private int[] offset;
	
	private double lat, lng, elv;       // coordinates
	private double timeZone, jDate;     // time variables
	

	// constructor
	
	public PrayerTimes() {
		numIterations = 1;
		offset = new int[]{0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0};
		
		// default calcMethod
		calcMethod = methods.MWL;
		// set the setting based on default method
		setting = calcMethod.getDetails();
		
	}
			


	// set calculating parameters
	private void adjust (MethodDetails details) {
		setting = details;
	}
	
	// return prayer times for a given date
	// date is an array of three integers: [fullYear, month, day]
	// coordination
	// timezone : Double can be -12 to +12
	// dst refers to daylight saving time it can true (+1 hour) or false (+0 hour) 
	
	public prayerTimesData getTimes(int[] date, Coordination coords, Double timezone, Boolean dayLightSaving) {
		lat = coords.getLat();
		lng = coords.getLng();
		
		// what is coords[2]?
		//elv = (coords.getLng() == null) ? 0 : coords[2];
		elv = 0;
		
		// get the timeZone based on the input date
		if (timezone == null)
			timezone = getTimeZone(date);
		
		if (dayLightSaving == null) 
			dayLightSaving = getDst(date);
		
		// add daylight saving
		timeZone = timezone + (dayLightSaving ? 1 : 0);
		
		jDate = julian(date[0], date[1], date[2])- lng/ (15* 24);
		
		return computeTimes();
	}
	
	public prayerTimesData getTimes(int[] date, Coordination coords){
		return getTimes(date, coords, null, null);
	}
	
	/* ###### not supported at this time ###### */
	/*
	// convert float time to the given format (see timeFormats)
	private	String getFormattedTime(double time) {
		
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
	*/
	//---------------------- Calculation Functions -----------------------


	// compute mid-day time
	private double midDay(double time) {
		// sunPosition returns an object with equation and declination
		double eqt = sunPosition(jDate+ time).getEquation();
		double noon = DMath.fixHour(12- eqt);
		return noon;
	}


	// compute the time at which sun reaches a specific angle below horizon
	private double sunAngleTime(double angle, double time, boolean isDirectionCCW) {
		
		double decl = sunPosition(jDate+ time).getDeclination();
		
		double noon = midDay(time);
		
		double t = (1d/15d) * DMath.arccos((-DMath.sin(angle)- DMath.sin(decl)* DMath.sin(lat))/ 
				(DMath.cos(decl)* DMath.cos(lat)));
		return noon+ (isDirectionCCW ? -t : t);
	}


	// compute asr time 
	private double asrTime(double factor, double time) {
		
		double decl = sunPosition(jDate+ time).getDeclination();
		double angle = -DMath.arccot(factor+ DMath.tan(Math.abs(lat- decl)));
		return sunAngleTime(angle, time, false);
	}


	// compute declination angle of sun and equation of time
	// Ref: http://aa.usno.navy.mil/faq/docs/SunApprox.php
	private sunPositionData sunPosition (double jd) {
		double D = jd - 2451545.0;
		double g = DMath.fixAngle(357.529 + 0.98560028* D);
		double q = DMath.fixAngle(280.459 + 0.98564736* D);
		double L = DMath.fixAngle(q + 1.915* DMath.sin(g) + 0.020* DMath.sin(2*g));

		//double R = 1.00014 - 0.01671* DMath.cos(g) - 0.00014* DMath.cos(2*g);
		double e = 23.439 - 0.00000036* D;

		double RA = DMath.arctan2(DMath.cos(e)* DMath.sin(L), DMath.cos(L))/ 15;
		double eqt = q/15 - DMath.fixHour(RA);
		double decl = DMath.arcsin(DMath.sin(e)* DMath.sin(L));

		return new sunPositionData(decl, eqt);
	}


	// convert Gregorian date to Julian day
	// Ref: Astronomical Algorithms by Jean Meeus
	private double julian (int year, int month, int day) {
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
	private prayerTimesData computePrayerTimes (prayerTimesData times) {
		times = dayPortion(times);
		MethodDetails params  = setting;

		double imsak   = this.sunAngleTime(params.getImsakMin(), times.getImsak().getTime(), true);
		double fajr    = this.sunAngleTime(params.getFajr(), times.getFajr().getTime(), true);
		double sunrise = this.sunAngleTime(riseSetAngle(), times.getSunrise().getTime(), true);
		double dhuhr   = this.midDay(times.getDhuhr().getTime());
		double asr     = this.asrTime(asrFactor(params.getAsr()), times.getAsr().getTime());
		double sunset  = this.sunAngleTime(riseSetAngle(), times.getSunset().getTime() , false);;
		double maghrib = this.sunAngleTime(params.getMaghrib(), times.getMaghrib().getTime(), false);
		double isha    = this.sunAngleTime(params.getIsha(), times.getIsha().getTime(), false);

		return new prayerTimesData(imsak, fajr, sunrise, dhuhr, asr, sunset, maghrib, isha, null);
	}

	
	// compute prayer times
	private prayerTimesData computeTimes() {
		// default times
		prayerTimesData times = new prayerTimesData(5, 5, 6, 12, 13, 18, 18, 18, null);

		// main iterations
		for (int i=1 ; i<=numIterations ; i++)
			times = this.computePrayerTimes(times);

		times = this.adjustTimes(times);

		// add midnight time
		times.setMidnight( (setting.getMidnight().equals(midnightMethods.Jafari)) ?
				times.getSunset().getTime()+ timeDiff(times.getSunset().getTime(), times.getFajr().getTime())/ 2 :
				times.getSunset().getTime()+ timeDiff(times.getSunset().getTime(), times.getSunrise().getTime())/ 2);

		times = this.tuneTimes(times);
		return times;
		//return modifyFormats(times);
	}


	// adjust times
	private prayerTimesData adjustTimes( prayerTimesData times) {
		MethodDetails params = setting;

		times.setAsr(times.getAsr().getTime() + timeZone - lng/ 15d);
		times.setImsak(times.getImsak().getTime() + timeZone - lng/ 15d);
		times.setFajr(times.getFajr().getTime() + timeZone - lng/ 15d);
		times.setSunrise(times.getSunrise().getTime() + timeZone - lng/ 15d);
		times.setDhuhr(times.getDhuhr().getTime() + timeZone - lng/ 15d);
		times.setSunset(times.getSunset().getTime() + timeZone - lng/ 15d);
		times.setMaghrib(times.getMaghrib().getTime() + timeZone - lng/ 15d);
		times.setIsha(times.getIsha().getTime() + timeZone - lng/ 15d);
		
		
		if (!params.getHighLats().equals(highLatMethods.None))
			times = adjustHighLats(times);

		String methodCode = getMethod().getDetails().getCode();
		times.setImsak( times.getFajr().getTime() - params.getImsakMin()/ 60d);
		
		// does not apply to Jafari and Tehran
		if (!methodCode.equals("Jafari") && !methodCode.equals("Tehran")){
			times.setMaghrib(times.getSunset().getTime()+ params.getMaghrib()/ 60d);
		}
		
		// only applies to Makkah calculation:
		if (methodCode.equals("Makkah")){
			times.setIsha(times.getMaghrib().getTime()+ params.getIsha()/ 60d);
		}
		
		times.setDhuhr( times.getDhuhr().getTime() + params.getDhuhrMin()/ 60d);

		return times;
	}


	// get asr shadow factor
	private double asrFactor(asrJuristics asrParam){
		if (asrParam == asrJuristics.Hanafi){
			return 2;
		}else{
			// assume asrJuristics.Standard
			return 1;
		}
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

		times.setAsr(times.getAsr().getTime() + offset[0]/ 60);
		times.setImsak(times.getImsak().getTime() + offset[1]/ 60);
		times.setFajr(times.getFajr().getTime() + offset[2]/ 60);
		times.setSunrise(times.getSunrise().getTime() + offset[3]/ 60);
		times.setDhuhr(times.getDhuhr().getTime() + offset[4]/ 60);
		times.setSunset(times.getSunset().getTime() + offset[5]/ 60);
		times.setMaghrib(times.getMaghrib().getTime() + offset[6]/ 60);
		times.setIsha(times.getIsha().getTime() + offset[7]/ 60);
		times.setMidnight(times.getMidnight().getTime() + offset[8]/ 60);
		
		return times;
	}


	/* we do not support format
	// convert times to given time format
	public prayerTimesData modifyFormats(prayerTimesData times) {
		
		times.setImsak(getFormattedTime(times.getImsak().getTime(), timeFormat, null));
		times.setFajr (getFormattedTime(times.getFajr().getTime(), timeFormat, null));
		times.sunrise = getFormattedTime(times.sunrise, timeFormat);
		times.dhuhr= getFormattedTime(times.dhuhr, timeFormat);
		times.asr= getFormattedTime(times.asr, timeFormat);
		times.sunset= getFormattedTime(times.sunset, timeFormat);
		times.maghrib= getFormattedTime(times.maghrib, timeFormat);
		times.isha= getFormattedTime(times.isha, timeFormat);
	        
		return times;
	}
	*/

	// adjust times for locations in higher latitudes
	private prayerTimesData adjustHighLats(prayerTimesData times) {
		MethodDetails params = setting;
		double nightTime = this.timeDiff(times.getSunset().getTime(), times.getSunrise().getTime());

		times.setImsak ( this.adjustHLTime(times.getImsak().getTime(), times.getSunrise().getTime(), params.getImsakMin(), nightTime, "ccw") );
		times.setFajr ( this.adjustHLTime(times.getFajr().getTime(), times.getSunrise().getTime(), params.getFajr(), nightTime, "ccw") );
		times.setIsha  ( this.adjustHLTime(times.getIsha().getTime(), times.getSunset().getTime(), params.getIsha(), nightTime, null) );
		times.setMaghrib ( this.adjustHLTime(times.getMaghrib().getTime(), times.getSunset().getTime(), params.getMaghrib(), nightTime, null) );

		return times;
	}

	// adjust a time for higher latitudes
	private double adjustHLTime(double time, double base, double angle, double night, String direction) {
		double portion = this.nightPortion(angle, night);
		double timeDiff = (direction != null && direction.equals("ccw")) ?
			this.timeDiff(time, base):
			this.timeDiff(base, time);
		if ( timeDiff > portion)
			time = base + ((direction != null && direction.equals("ccw")) ? -portion : portion);
		return time;
	}


	// the night portion used for adjusting times in higher latitudes
	private double nightPortion(double angle, double night) {
		highLatMethods method = setting.getHighLats();
		double portion = 1d/2d ;// MidNight
		if (method.equals(highLatMethods.AngleBased))
			portion = 1d/60d * angle;
		if (method.equals(highLatMethods.OneSeventh))
			portion = 1d/7d;
		return portion* night;
	}


	// convert hours to day portions
	private prayerTimesData dayPortion( prayerTimesData times) {
		
		times.setAsr(times.getAsr().getTime() / 24);
		times.setImsak(times.getImsak().getTime() / 24);
		times.setFajr(times.getFajr().getTime() / 24);
		times.setSunrise(times.getSunrise().getTime() / 24);
		times.setDhuhr(times.getDhuhr().getTime() / 24);
		times.setSunset(times.getSunset().getTime() / 24);
		times.setMaghrib(times.getMaghrib().getTime() / 24);
		times.setIsha(times.getIsha().getTime() / 24);
		
		if (times.getMidnight().getTime() != null)
			times.setMidnight(times.getMidnight().getTime() / 24);
				
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

	// compute the difference between two times
	private double timeDiff(double time1, double time2) {
		return DMath.fixHour(time2- time1);
	}

	/*
	// add a leading 0 if necessary
	private double twoDigitsFormat(double num) {
		return (num <10) ? '0'+ num : num;
	}
	*/
	
	// #### getter / setter ####
	
	// set calculation method 
	public void setMethod(methods method) {		
		this.adjust(method.getDetails());
		calcMethod = method;
	}
	// get current calculation method
	public methods getMethod(){
		return calcMethod; 
	};

}
