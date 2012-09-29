package com.tywholland.simpletimer.model;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tywholland.simpletimer.AlarmReceiverActivity;

@SuppressLint("NewApi")
public class AlarmUtil {
	public static void startTimer(Context context, long milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, (int) (milliseconds / 1000));

		// Create a new PendingIntent and add it to the AlarmManager
		Intent intent = new Intent(context, AlarmReceiverActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 12345,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
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
		if(time.length() > 4) {
			return Integer.parseInt(time.substring(0, time.length() - 4));
		}
		else {
			return 0;
		}
	}
	public static Integer getMinutesFromTimeString(String time) {
		if(time.length() > 2) {
			if (time.length() >= 4) {
				return Integer.parseInt(time.substring(time.length() - 4, time.length() - 2));
			}
			else {
				return Integer.parseInt(time.substring(0, time.length() - 2));
			}
		}
		else {
			return 0;
		}
	}
	public static Integer getSecondsFromTimeString(String time) {
		if(time.length() >= 2) {
			return Integer.parseInt(time.substring(time.length() - 2));
		}
		else if (time.length() > 0){
			return Integer.parseInt(time);
		}
		else {
			return 0;
		}
	}
}
