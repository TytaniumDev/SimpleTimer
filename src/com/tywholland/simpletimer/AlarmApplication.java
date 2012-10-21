package com.tywholland.simpletimer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmApplication extends Application {
	public static final String ALARM_INTENT = "alarm";
	public static final String CANCEL_ALARM = "cancelalarm";
	public static final int NOTIFICATION_ID = 4439;

	private Vibrator mVibrator;
	private MediaPlayer mMediaPlayer;
	private AlarmManager mAlarmManager;
	private Calendar mCurrentAlarmCalendar;
	private NotificationManager mNotificationManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mAlarmManager = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
		mMediaPlayer = null;
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public Calendar getCurrentAlarmCalendar() {
		if (mCurrentAlarmCalendar == null) {
			return Calendar.getInstance();
		} else {
			return mCurrentAlarmCalendar;
		}
	}

	public void setCurrentAlarmCalendar(Calendar cal) {
		mCurrentAlarmCalendar = cal;
	}

	public void startTimer(long milliseconds) {
		mCurrentAlarmCalendar = Calendar.getInstance();
		mCurrentAlarmCalendar.add(Calendar.SECOND, (int) (milliseconds / 1000));

		// Create a new PendingIntent and add it to the AlarmManager
		mAlarmManager.set(AlarmManager.RTC_WAKEUP,
				mCurrentAlarmCalendar.getTimeInMillis(), getTimerPendingIntent(false));
		showTimerNotification();
	}

	public void stopTimer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer = null;
		}
		mVibrator.cancel();
		mAlarmManager.cancel(getTimerPendingIntent(false));
		getCurrentAlarmCalendar().setTimeInMillis(0);
		removeTimerNotification();
	}

	public void showTimerNotification() {
		Notification notification = getBaseNotificationBuilder().build();
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	public void updateTimerNotificationText(String text) {
		NotificationCompat.Builder builder = getBaseNotificationBuilder();
		builder.setContentText(text);
		mNotificationManager.notify(NOTIFICATION_ID, builder.build());
	}

	public void updateTimerNotificationAlarmDone() {
		NotificationCompat.Builder builder = getBaseNotificationBuilder();
		builder.setContentText(getResources().getString(
				R.string.notification_done_text))
				.setOngoing(false)
				.setDeleteIntent(getTimerPendingIntent(true));
		mNotificationManager.notify(NOTIFICATION_ID, builder.build());
	}

	public void removeTimerNotification() {
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

	private NotificationCompat.Builder getBaseNotificationBuilder() {
		Intent notificationIntent = new Intent(this, SimpleTimerActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		Resources res = getResources();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
		builder.setSmallIcon(icon)
				.setTicker(res.getString(R.string.notification_popup_msg))
				.setWhen(when)
				.setOngoing(true)
				.setContentIntent(contentIntent)
				.setContentTitle(res.getString(R.string.app_name))
				.setContentText(
						res.getString(R.string.notification_alarm_ending_on_text) + " "
								+ sdf.format(getCurrentAlarmCalendar().getTime()));
		return builder;
	}

	private PendingIntent getTimerPendingIntent(boolean cancelAlarm) {
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.putExtra(CANCEL_ALARM, cancelAlarm);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	public void notifyUser() {
		playSound();
		mVibrator.vibrate(new long[] { 0, 200, 500 }, 0);
	}

	private void playSound() {
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(this, getAlarmUri());
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			}
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			Log.e("AlarmUtil", "Error in MediaPlayer");
		}
	}

	private Uri getAlarmUri() {
		// Get an alarm sound. Try for an alarm. If none set, try notification,
		// Otherwise, ringtone.
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}
}
