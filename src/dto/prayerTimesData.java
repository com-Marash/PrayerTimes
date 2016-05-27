package dto;

public class prayerTimesData {

	
	public double imsak;
	public double fajr;
	public double sunrise;
	public double dhuhr;
	public double asr;
	public double sunset;
	public double maghrib;
	public double isha;
	
	public prayerTimesData(double imsak2, double fajr2, double sunrise2, double dhuhr2, double asr2, double sunset2,
			double maghrib2, double isha2) {
		imsak = imsak2;
		fajr= fajr2;
		sunrise = sunrise2;
		dhuhr = dhuhr2;
		asr = asr2;
		sunset = sunset2;
		maghrib = maghrib2;
		isha = isha2;
	}

	public prayerTimesData() {
		
	}

}
