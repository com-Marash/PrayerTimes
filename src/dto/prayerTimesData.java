package dto;

public class prayerTimesData {

	
	private prayerTimesDouble imsak;
	private prayerTimesDouble fajr;
	private prayerTimesDouble sunrise;
	private prayerTimesDouble dhuhr;
	private prayerTimesDouble asr;
	private prayerTimesDouble sunset;
	private prayerTimesDouble maghrib;
	private prayerTimesDouble isha;
	private prayerTimesDouble midnight;
	
	public prayerTimesData(double imsak, double fajr, double sunrise, double dhuhr, double asr, double sunset,
			double maghrib, double isha, Double midnight) {
		this.imsak = new prayerTimesDouble(imsak);
		this.fajr= new prayerTimesDouble(fajr);
		this.sunrise = new prayerTimesDouble(sunrise);
		this.dhuhr = new prayerTimesDouble(dhuhr);
		this.asr = new prayerTimesDouble(asr);
		this.sunset = new prayerTimesDouble(sunset);
		this.maghrib = new prayerTimesDouble(maghrib);
		this.isha = new prayerTimesDouble(isha);
		this.midnight = new prayerTimesDouble(midnight);
	}

	public prayerTimesData() {
		
	}

	public prayerTimesDouble getImsak() {
		return imsak;
	}

	public void setImsak(double imsak) {
		this.imsak.setTime(imsak);
	}
	
	public prayerTimesDouble getFajr() {
		return fajr;
	}

	public void setFajr(double fajr) {
		this.fajr.setTime(fajr);
	}

	public prayerTimesDouble getSunrise() {
		return sunrise;
	}

	public void setSunrise(double sunrise) {
		this.sunrise.setTime(sunrise);
	}

	public prayerTimesDouble getDhuhr() {
		return dhuhr;
	}

	public void setDhuhr(double dhuhr) {
		this.dhuhr.setTime(dhuhr);
	}

	public prayerTimesDouble getAsr() {
		return asr;
	}

	public void setAsr(double asr) {
		this.asr.setTime(asr);
	}

	public prayerTimesDouble getSunset() {
		return sunset;
	}

	public void setSunset(double sunset) {
		this.sunset.setTime(sunset);
	}

	public prayerTimesDouble getMaghrib() {
		return maghrib;
	}

	public void setMaghrib(double maghrib) {
		this.maghrib.setTime(maghrib);
	}

	public prayerTimesDouble getIsha() {
		return isha;
	}

	public void setIsha(double isha) {
		this.isha.setTime(isha);
	}

	public prayerTimesDouble getMidnight() {
		return midnight;
	}

	public void setMidnight(Double midnight) {
		this.midnight.setTime(midnight);
	}
	
}
