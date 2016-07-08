package main;

import dto.Coordination;
import dto.prayerTimesData;
import main.PrayerTimes.methods;

public class Tester {

	public static void main(String[] args) {
		PrayerTimes myPrayerTimes = new PrayerTimes();
		try {
			prayerTimesData calculatedTimes = myPrayerTimes.getTimes(new int[]{2016,7,8}, new Coordination(40, -80), null, null, null);
			
			myPrayerTimes.setMethod(methods.Tehran);
			
			System.out.println(calculatedTimes.getAsr());
			System.out.println(calculatedTimes.getDhuhr());
			System.out.println(calculatedTimes.getFajr());
			System.out.println(calculatedTimes.getImsak());
			System.out.println(calculatedTimes.getIsha());
			System.out.println(calculatedTimes.getMaghrib());
			System.out.println(calculatedTimes.getSunrise());
			System.out.println(calculatedTimes.getSunset());
			System.out.println(calculatedTimes.getMidnight());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
