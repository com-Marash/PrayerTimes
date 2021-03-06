package com.marash.prayerTimes.main;

import com.marash.prayerTimes.dto.Coordination;
import com.marash.prayerTimes.dto.prayerTimesData;
import com.marash.prayerTimes.main.PrayerTimes.methods;

import java.util.Calendar;

public class Tester {

	public static void main(String[] args) {
		PrayerTimes myPrayerTimes = new PrayerTimes();
		myPrayerTimes.setMethod(methods.Makkah);
		
		try {
			
			Calendar calendar = Calendar.getInstance();
			
			prayerTimesData calculatedTimes = myPrayerTimes.getTimes(new int[]{calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) +1,calendar.get(Calendar.DAY_OF_MONTH)}, new Coordination(42.9878759, -81.29583202), (double) -5, null);
			
			System.out.println(calculatedTimes.getImsak().getFormatedTime());
			System.out.println(calculatedTimes.getFajr().getFormatedTime());
			System.out.println(calculatedTimes.getSunrise().getFormatedTime());
			System.out.println(calculatedTimes.getDhuhr().getFormatedTime());
			System.out.println(calculatedTimes.getAsr().getFormatedTime());
			System.out.println(calculatedTimes.getSunset().getFormatedTime());
			System.out.println(calculatedTimes.getMaghrib().getFormatedTime());
			System.out.println(calculatedTimes.getIsha().getFormatedTime());
			System.out.println(calculatedTimes.getMidnight().getFormatedTime());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
