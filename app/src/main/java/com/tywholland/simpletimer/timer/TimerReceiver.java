package com.tywholland.simpletimer.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimerReceiver extends BroadcastReceiver {
	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;

	@Override
	public void onReceive(Context context, Intent intent) {
		String title = intent.getStringExtra(TimerUtil.TITLE_KEY);
		long millisecondsLeft = intent.getLongExtra(
				TimerUtil.MILLISECONDS_LEFT_KEY, 0);
		if (intent.getBooleanExtra(TimerUtil.CANCEL_ALARM_KEY, false)) {
			TimerUtil.stopTimer(context.getApplicationContext());
		} else if (millisecondsLeft <= SECOND) {
			TimerUtil.alertTimerHasFinished(context.getApplicationContext(),
					title);
		} else {
			int hours = TimeConversionUtil
					.getHoursFromMilliseconds(millisecondsLeft);
			int minutes = TimeConversionUtil
					.getMinutesFromMilliseconds(millisecondsLeft);
			int seconds = TimeConversionUtil
					.getSecondsFromMilliseconds(millisecondsLeft);
			if (hours > 0 || minutes > 0) {
				// Set next update at minute breakpoint, or update at 59 seconds
				if (hours == 0 && minutes == 1 && seconds == 0) {
					millisecondsLeft -= SECOND;
					TimerUtil.setAlarm(context.getApplicationContext(), SECOND,
							millisecondsLeft, title);
				} else if (seconds > 0) {
					// Set update to next minute breakpoint
					millisecondsLeft -= seconds * SECOND;
					TimerUtil.setAlarm(context.getApplicationContext(),
							(seconds * SECOND), millisecondsLeft, title);
				} else {
					// Update in 1 minute
					millisecondsLeft -= MINUTE;
					TimerUtil.setAlarm(context.getApplicationContext(), MINUTE,
							millisecondsLeft, title);
				}
			} else {
				// Only seconds left, update every second
				millisecondsLeft -= SECOND;
				TimerUtil.setAlarm(context.getApplicationContext(), SECOND,
						millisecondsLeft, title);
			}
			TimerUtil.updateNotificationContentText(
					context.getApplicationContext(), title, millisecondsLeft);
		}
	}
}
