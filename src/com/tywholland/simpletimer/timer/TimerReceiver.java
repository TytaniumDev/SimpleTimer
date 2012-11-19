package com.tywholland.simpletimer.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimerReceiver extends BroadcastReceiver {
	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND * 60;

	@Override
	public void onReceive(Context context, Intent intent) {
		TimerNotificationUtil handler = new TimerNotificationUtil(context);
		if (intent.getBooleanExtra(TimerNotificationUtil.CANCEL_ALARM, false)) {
			handler.cancelTimer();
		} else {
			long millisecondsLeft = intent.getLongExtra(
					TimerNotificationUtil.MILLISECONDS_LEFT_KEY, 0);
			if (millisecondsLeft <= SECOND) {
				handler.timerFinished();
			} else {
				int hours = TimerUtil
						.getHoursFromMilliseconds(millisecondsLeft);
				int minutes = TimerUtil
						.getMinutesFromMilliseconds(millisecondsLeft);
				int seconds = TimerUtil
						.getSecondsFromMilliseconds(millisecondsLeft);
				if (hours > 0 || minutes > 0) {
					// Set next update at minute breakpoint, or update at 59
					// seconds
					if (hours == 0 && minutes == 1 && seconds == 0) {
						millisecondsLeft -= SECOND;
						handler.setAlarm(SECOND, millisecondsLeft);
						handler.updateNotificationContentText(handler
								.getSecondsText(millisecondsLeft));
					} else if (seconds > 0) {
						// Set update to next minute breakpoint
						millisecondsLeft -= seconds * SECOND;
						handler.setAlarm((seconds * SECOND), millisecondsLeft);
						handler.updateNotificationContentText(handler
								.getHoursMinutesText(millisecondsLeft));
					} else {
						// Update in 1 minute
						millisecondsLeft -= MINUTE;
						handler.setAlarm(MINUTE, millisecondsLeft);
						handler.updateNotificationContentText(handler
								.getHoursMinutesText(millisecondsLeft));
					}
				} else {
					// Only seconds left, update every second
					millisecondsLeft -= SECOND;
					handler.updateNotificationContentText(handler
							.getSecondsText(millisecondsLeft));
					handler.setAlarm(SECOND, millisecondsLeft);
				}
			}
		}
	}

}
