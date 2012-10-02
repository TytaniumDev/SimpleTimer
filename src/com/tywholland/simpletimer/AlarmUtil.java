package com.tywholland.simpletimer;

import java.util.concurrent.TimeUnit;

import android.util.Log;

public class AlarmUtil {
	public static String getTimeStringFromMilliseconds(long milliseconds) {
		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	public static long convertStringToMilliseconds(String time) {
		int seconds = getSecondsFromTimeString(time);
		int minutes = getMinutesFromTimeString(time);
		int hours = getHoursFromTimeString(time);
		Log.i("AlarmUtil", "Converted " + time + " into " + hours + " hours, "
				+ minutes + " minutes, " + seconds + " seconds");
		return ((seconds * 1000) + (minutes * 60 * 1000) + (hours * 60 * 60 * 1000));
	}

	public static Integer getHoursFromTimeString(String time) {
		if (time.length() > 4) {
			return Integer.parseInt(time.substring(0, time.length() - 4));
		} else {
			return 0;
		}
	}

	public static Integer getMinutesFromTimeString(String time) {
		if (time.length() > 2) {
			if (time.length() >= 4) {
				return Integer.parseInt(time.substring(time.length() - 4,
						time.length() - 2));
			} else {
				return Integer.parseInt(time.substring(0, time.length() - 2));
			}
		} else {
			return 0;
		}
	}

	public static Integer getSecondsFromTimeString(String time) {
		if (time.length() >= 2) {
			return Integer.parseInt(time.substring(time.length() - 2));
		} else if (time.length() > 0) {
			return Integer.parseInt(time);
		} else {
			return 0;
		}
	}
}
