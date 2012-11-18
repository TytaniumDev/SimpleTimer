package com.tywholland.simpletimer;

import java.io.IOException;
import java.util.Calendar;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tywholland.simpletimer.timer.TimerNotificationHandler;

public class SimpleTimerApplication extends Application {
	private Vibrator mVibrator;
	private MediaPlayer mMediaPlayer;
	private Calendar mCurrentAlarmCalendar;
	private SharedPreferences mSettings;
	private String mTimeString;
	private String mAlarmName;
	private TimerNotificationHandler mTimerNotificationHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		mSettings = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mTimerNotificationHandler = new TimerNotificationHandler(this);
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
		mTimerNotificationHandler.startTimer(getNotificationAlarmTitle(), milliseconds);
	}
	
	public void stopTimer() {
		mTimerNotificationHandler.cancelTimer();
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
		return mAlarmName.length() > 0 ? mAlarmName
				: getString(R.string.timer);
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
	
	public TimerNotificationHandler getTimerNotificationHandler() {
		return mTimerNotificationHandler;
	}
}
