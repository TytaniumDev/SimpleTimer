package com.tywholland.simpletimer;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tywholland.simpletimer.timer.TimerActivity;
import com.tywholland.simpletimer.timer.TimerNotificationUtil;
import com.tywholland.simpletimer.timer.TimerReceiver;

public class SimpleTimerApplication extends Application {
	private static final int PENDING_INTENT_ID = 94549;

	private Vibrator mVibrator;
	private MediaPlayer mMediaPlayer;
	private Calendar mCurrentAlarmCalendar;
	private SharedPreferences mSettings;
	private String mTimeString;
	private String mAlarmName;

	@Override
	public void onCreate() {
		super.onCreate();
		mSettings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mMediaPlayer = null;
		mTimeString = "";
		mAlarmName = "";
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
		Intent intent = new Intent();
		intent.putExtra(TimerNotificationUtil.MILLISECONDS_LEFT_KEY,
				milliseconds);
		intent.putExtra(TimerNotificationUtil.TIMER_NAME_KEY, getNotificationAlarmTitle());
		PendingIntent pendingIntent = getTimerPendingIntent(milliseconds, false);
		((AlarmManager) getSystemService(Activity.ALARM_SERVICE)).set(
				AlarmManager.RTC_WAKEUP, Calendar.getInstance()
						.getTimeInMillis(), pendingIntent);
	}

	public void stopTimer() {
		((AlarmManager) getSystemService(Activity.ALARM_SERVICE))
				.cancel(PendingIntent.getBroadcast(getApplicationContext(),
						PENDING_INTENT_ID, new Intent(getApplicationContext(),
								TimerActivity.class),
						PendingIntent.FLAG_CANCEL_CURRENT));
		((NotificationManager) getApplicationContext().getSystemService(
				Context.NOTIFICATION_SERVICE))
				.cancel(TimerNotificationUtil.NOTIFICATION_ID);
		stopNotifyingUser();
	}

	public void stopNotifyingUser() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer = null;
		}
		mVibrator.cancel();
		getCurrentAlarmCalendar().setTimeInMillis(0);
	}

	private String getNotificationAlarmTitle() {
		return mAlarmName.length() > 0 ? mAlarmName : getString(R.string.timer);
	}

	public void notifyUser() {
		playSound();
		if (mSettings.getBoolean(getString(R.string.key_vibrate), true)) {
			mVibrator.vibrate(new long[] { 0, 200, 500 }, 0);
		}
	}

	private void playSound() {
		try {
			String alarmSound = mSettings.getString(
					getString(R.string.key_alarm_sound), null);
			Uri alarmUri = alarmSound != null ? Uri.parse(alarmSound)
					: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(this, alarmUri);
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

	public PendingIntent getTimerPendingIntent(long millisecondsLeft,
			boolean cancel) {
		Intent intent = new Intent(getApplicationContext(), TimerReceiver.class);
		intent.putExtra(TimerNotificationUtil.MILLISECONDS_LEFT_KEY,
				millisecondsLeft);
		intent.putExtra(TimerNotificationUtil.CANCEL_ALARM, cancel);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), PENDING_INTENT_ID, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	public String getTimeString() {
		return mTimeString;
	}

	public void setTimeString(String mTimeString) {
		this.mTimeString = mTimeString;
	}

	public void appendToTimeString(String append) {
		this.mTimeString = this.mTimeString.concat(append);
	}

	public String getAlarmName() {
		return mAlarmName;
	}

	public void setAlarmName(String alarmName) {
		this.mAlarmName = alarmName;
	}
}
