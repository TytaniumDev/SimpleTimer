package com.tywholland.simpletimer;

import android.util.Log;

public class AlarmUtil {
	public static String getTimeStringFromMilliseconds(long milliseconds) {
		long time = milliseconds / 1000;
		String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}
		return hours + ":" + minutes + ":" + seconds;
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
