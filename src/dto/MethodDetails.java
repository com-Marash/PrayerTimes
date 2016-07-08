package dto;
import methodDetailsEnums.asrJuristics;
import methodDetailsEnums.highLatMethods;
import methodDetailsEnums.midnightMethods;

public class MethodDetails {

	private String name;
	private double fajr;
	private double isha;
	private double maghrib;
	private midnightMethods midnight;
	private int ishaMin;
	private int maghribMin;
	private int imsakMin;
	private int dhuhrMin;
	private asrJuristics asr;
	private highLatMethods highLats;
	private String code;
	
	
	public MethodDetails(String name, double fajr, double isha, double maghrib,
			midnightMethods midnight, int ishaMin, int maghribMin,
			int imsakMin, int dhuhrMin, asrJuristics asr, highLatMethods highLats, String code){
		
		this.name = name;
		this.fajr = fajr;
		this.isha = isha;
		this.maghrib = maghrib;
		this.midnight = midnight;
		this.ishaMin = ishaMin;
		this.maghribMin = maghribMin;
		this.imsakMin = imsakMin;
		this.dhuhrMin = dhuhrMin;
		this.asr = asr;
		this.highLats = highLats;
		this.code = code;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public double getFajr() {
		return fajr;
	}


	public void setFajr(double fajr) {
		this.fajr = fajr;
	}


	public double getIsha() {
		return isha;
	}


	public void setIsha(double isha) {
		this.isha = isha;
	}


	public double getMaghrib() {
		return maghrib;
	}


	public void setMaghrib(double maghrib) {
		this.maghrib = maghrib;
	}


	public midnightMethods getMidnight() {
		return midnight;
	}


	public void setMidnight(midnightMethods midnight) {
		this.midnight = midnight;
	}


	public int getIshaMin() {
		return ishaMin;
	}


	public void setIshaMin(int ishaMin) {
		this.ishaMin = ishaMin;
	}


	public int getMaghribMin() {
		return maghribMin;
	}


	public void setMaghribMin(int maghribMin) {
		this.maghribMin = maghribMin;
	}


	public int getImsakMin() {
		return imsakMin;
	}


	public void setImsakMin(int imsakMin) {
		this.imsakMin = imsakMin;
	}


	public int getDhuhrMin() {
		return dhuhrMin;
	}


	public void setDhuhrMin(int dhuhrMin) {
		this.dhuhrMin = dhuhrMin;
	}


	public asrJuristics getAsr() {
		return asr;
	}


	public void setAsr(asrJuristics asr) {
		this.asr = asr;
	}


	public highLatMethods getHighLats() {
		return highLats;
	}


	public void setHighLats(highLatMethods highLats) {
		this.highLats = highLats;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}

	
}
