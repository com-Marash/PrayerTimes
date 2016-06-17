package dto;

public class prayerTimesData {

	
	private double imsak;
	private double fajr;
	private double sunrise;
	private double dhuhr;
	private double asr;
	private double sunset;
	private double maghrib;
	private double isha;
	private Double midnight;
	
	public prayerTimesData(double imsak, double fajr, double sunrise, double dhuhr, double asr, double sunset,
			double maghrib, double isha, Double midnight) {
		this.imsak = imsak;
		this.fajr= fajr;
		this.sunrise = sunrise;
		this.dhuhr = dhuhr;
		this.asr = asr;
		this.sunset = sunset;
		this.maghrib = maghrib;
		this.isha = isha;
		this.midnight = midnight;
	}

	public prayerTimesData() {
		
	}

	public double getImsak() {
		return imsak;
	}

	public void setImsak(double imsak) {
		this.imsak = imsak;
	}

	public double getFajr() {
		return fajr;
	}

	public void setFajr(double fajr) {
		this.fajr = fajr;
	}

	public double getSunrise() {
		return sunrise;
	}

	public void setSunrise(double sunrise) {
		this.sunrise = sunrise;
	}

	public double getDhuhr() {
		return dhuhr;
	}

	public void setDhuhr(double dhuhr) {
		this.dhuhr = dhuhr;
	}

	public double getAsr() {
		return asr;
	}

	public void setAsr(double asr) {
		this.asr = asr;
	}

	public double getSunset() {
		return sunset;
	}

	public void setSunset(double sunset) {
		this.sunset = sunset;
	}

	public double getMaghrib() {
		return maghrib;
	}

	public void setMaghrib(double maghrib) {
		this.maghrib = maghrib;
	}

	public double getIsha() {
		return isha;
	}

	public void setIsha(double isha) {
		this.isha = isha;
	}

	public Double getMidnight() {
		return midnight;
	}

	public void setMidnight(Double midnight) {
		this.midnight = midnight;
	}
	
}
