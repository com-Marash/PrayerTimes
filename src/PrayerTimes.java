import java.lang.reflect.Method;

import enums.asrJuristics;
import enums.highLatMethods;
import enums.midnightMethods;

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
		
		public MethodDetails details;
		
		methods(MethodDetails details){
			this.details = details;
		}
	}
	
	
	public enum timeFormats{
		t_24h,         // 24-hour format
		t_12h,         // 12-hour format
		t_12hNS,       // 12-hour format with no suffix
		t_Float        // floating point number
	}
	
	//---------------------- Default Settings --------------------

	private methods calcMethod;

	private static class setting{
		
	}
	
	private timeFormats timeFormat;
	private String[] timeSuffixes;
	private String invalidTime;
	private int numIterations;
	private Object offset;
	
	

	
	public PrayerTimes() {
		
		timeFormat = timeFormats.t_24h;
		timeSuffixes = new String[]{"am","pm"};
		invalidTime = "-----";
		numIterations = 1;
		offset = new Object();
		calcMethod = methods.MWL;
	}


	// init time offsets
	for (var i in timeNames)
		offset[i] = 0;
			
	//----------------------- Local Variables ---------------------
	private double lat, lng, elv,       // coordinates
		timeZone, jDate;     // time variables
}
