import enums.asrJuristics;
import enums.highLatMethods;
import enums.midnightMethods;

public class MethodDetails {

	public String name;
	public double fajr, isha, maghrib;
	public midnightMethods midnight;
	public int ishaMin, maghribMin;
	public int imsakMin;
	public int dhuhrMin;
	public asrJuristics asr;
	public highLatMethods highLats;
	
	
	public MethodDetails(String name, double fajr, double isha, double maghrib,
			midnightMethods midnight, int ishaMin, int maghribMin,
			int imsakMin, int dhuhrMin, asrJuristics asr, highLatMethods highLats ){
		
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
	}

}
