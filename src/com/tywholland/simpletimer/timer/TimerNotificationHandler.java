package com.tywholland.simpletimer.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.tywholland.simpletimer.R;
import com.tywholland.simpletimer.SimpleTimerApplication;

public class TimerNotificationHandler {
	public static final String MILLISECONDS_LEFT_KEY = "ms_left";
	public static final String CANCEL_ALARM = "cancelalarm";

	private static final int NOTIFICATION_ID = 4439;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotificationBuilder;
	private AlarmManager mAlarmManager;
	private SimpleTimerApplication mApplication;
	private PendingIntent mPendingIntent;

	public TimerNotificationHandler(SimpleTimerApplication application) {
		mApplication = application;
		mApplication.getApplicationContext();
		mAlarmManager = (AlarmManager) mApplication
				.getSystemService(Activity.ALARM_SERVICE);
		mNotificationManager = (NotificationManager) mApplication
				.getApplicationContext().getSystemService(
						Context.NOTIFICATION_SERVICE);
	}

	public void startTimer(String title, long milliseconds) {
		mNotificationBuilder = getBaseNotificationBuilder();
		mNotificationBuilder.setContentTitle(getTitleText(title, milliseconds))
				.setContentText(
						TimerUtil.getTimeStringFromMilliseconds(milliseconds));
		Intent intent = new Intent();
		intent.putExtra(MILLISECONDS_LEFT_KEY, milliseconds);
		setAlarm(0, milliseconds);
	}

	public void cancelTimer() {
		mAlarmManager.cancel(mPendingIntent);
		mNotificationManager.cancel(NOTIFICATION_ID);
		mApplication.stopNotifyingUser();
	}

	protected void setAlarm(long timeToAlarm, long millisecondsLeft) {
		mPendingIntent = getTimerPendingIntent(millisecondsLeft, false);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, (int) (timeToAlarm / 1000));
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				mPendingIntent);
	}

	private String getTitleText(String timerName, long milliseconds) {
		Calendar time = Calendar.getInstance();
		time.add(Calendar.SECOND, (int) (milliseconds / 1000));
		SimpleDateFormat sdf;
		if (PreferenceManager.getDefaultSharedPreferences(
				mApplication.getApplicationContext()).getBoolean(
				mApplication.getResources().getString(
						R.string.key_twenty_four_hour), false)) {
			sdf = new SimpleDateFormat("kk:mm:ss", Locale.getDefault());
		} else {
			sdf = new SimpleDateFormat("h:mm:ss aa", Locale.getDefault());
		}
		return timerName
				+ " - "
				+ sdf.format(time.getTime());
	}

	protected String getHoursMinutesText(long milliseconds) {
		Resources res = mApplication.getResources();
		String result = "";
		int hours = TimerUtil.getHoursFromMilliseconds(milliseconds);
		int minutes = TimerUtil.getMinutesFromMilliseconds(milliseconds);
		if (hours > 1) {
			result = result.concat(hours + " " + res.getString(R.string.hours)
					+ " ");
		} else if (hours == 1) {
			result = result.concat(hours + " " + res.getString(R.string.hour)
					+ " ");
		}
		if (minutes > 1) {
			result = result.concat(minutes + " "
					+ res.getString(R.string.minutes) + " ");
		} else if (minutes == 1) {
			result = result.concat(minutes + " "
					+ res.getString(R.string.minute) + " ");
		}
		result = result.concat(res.getString(R.string.remaining));
		return result;
	}

	protected String getSecondsText(long milliseconds) {
		Resources res = mApplication.getResources();
		String result = "";
		int seconds = TimerUtil.getSecondsFromMilliseconds(milliseconds);
		if (seconds > 1) {
			result = result.concat(seconds + " "
					+ res.getString(R.string.seconds) + " ");
		} else if (seconds == 1) {
			result = result.concat(seconds + " "
					+ res.getString(R.string.second) + " ");
		}
		result = result.concat(res.getString(R.string.remaining));
		return result;
	}

	protected void updateNotificationContentText(String timeText) {
		mNotificationBuilder.setContentText(timeText);
		mNotificationManager.notify(NOTIFICATION_ID,
				mNotificationBuilder.build());
	}

	protected void timerFinished() {
		mApplication.notifyUser();
		mNotificationBuilder
				.setContentText(
						mApplication.getResources().getString(
								R.string.notification_done_text))
				.setOngoing(false)
				.setDeleteIntent(getTimerPendingIntent(-1, true));
		mNotificationManager.notify(NOTIFICATION_ID,
				mNotificationBuilder.build());
	}

	private NotificationCompat.Builder getBaseNotificationBuilder() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				mApplication.getApplicationContext());
		Intent notificationIntent = new Intent(
				mApplication.getApplicationContext(), TimerActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(
				mApplication.getApplicationContext(), 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();

		builder.setSmallIcon(icon)
				.setWhen(when)
				.setOngoing(true)
				.setTicker(
						mApplication.getResources().getString(
								R.string.notification_popup_msg))
				.setContentIntent(contentIntent);

		return builder;
	}

	private PendingIntent getTimerPendingIntent(long millisecondsLeft,
			boolean cancel) {
		Intent intent = new Intent(mApplication.getApplicationContext(),
				TimerReceiver.class);
		intent.putExtra(MILLISECONDS_LEFT_KEY, millisecondsLeft);
		intent.putExtra(CANCEL_ALARM, cancel);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				mApplication.getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
}
