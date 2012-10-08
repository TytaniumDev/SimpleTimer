package com.tywholland.simpletimer;

import java.util.Calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SimpleTimerActivity extends Activity {
	private static final String ALARM_TIME = "alarmkey";

	private String mTime;
	private Button mButton;
	private Button mNumpad1;
	private Button mNumpad2;
	private Button mNumpad3;
	private Button mNumpad4;
	private Button mNumpad5;
	private Button mNumpad6;
	private Button mNumpad7;
	private Button mNumpad8;
	private Button mNumpad9;
	private Button mNumpad0;
	private Button mStopButton;
	private TextView mTimeView;
	private AlarmApplication mAlarmApplication;
	private CountDownTimer mCountDownTimer;
	private boolean mCountingDown;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mCountingDown = false;
		mAlarmApplication = (AlarmApplication) getApplicationContext();
		mTime = "";
		mButton = (Button) findViewById(R.id.startbutton);
		mButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mAlarmApplication.stopTimer();
				mAlarmApplication.startTimer(AlarmUtil
						.convertStringToMilliseconds(mTime));
				stopTextCountdown();
				startTextCountdown();
				mTime = "";
				mCountingDown = true;
			}
		});
		mStopButton = (Button) findViewById(R.id.stopbutton);
		mStopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mTime = "";
				mTimeView.setText(R.string.default_time);
				mAlarmApplication.stopTimer();
				stopTextCountdown();
				mCountingDown = false;
			}
		});
		mTimeView = (TextView) findViewById(R.id.timerTextView);
		mNumpad0 = (Button) findViewById(R.id.numpad0);
		mNumpad1 = (Button) findViewById(R.id.numpad1);
		mNumpad2 = (Button) findViewById(R.id.numpad2);
		mNumpad3 = (Button) findViewById(R.id.numpad3);
		mNumpad4 = (Button) findViewById(R.id.numpad4);
		mNumpad5 = (Button) findViewById(R.id.numpad5);
		mNumpad6 = (Button) findViewById(R.id.numpad6);
		mNumpad7 = (Button) findViewById(R.id.numpad7);
		mNumpad8 = (Button) findViewById(R.id.numpad8);
		mNumpad9 = (Button) findViewById(R.id.numpad9);
		mNumpad0.setOnClickListener(numpadButtonClickListener());
		mNumpad1.setOnClickListener(numpadButtonClickListener());
		mNumpad2.setOnClickListener(numpadButtonClickListener());
		mNumpad3.setOnClickListener(numpadButtonClickListener());
		mNumpad4.setOnClickListener(numpadButtonClickListener());
		mNumpad5.setOnClickListener(numpadButtonClickListener());
		mNumpad6.setOnClickListener(numpadButtonClickListener());
		mNumpad7.setOnClickListener(numpadButtonClickListener());
		mNumpad8.setOnClickListener(numpadButtonClickListener());
		mNumpad9.setOnClickListener(numpadButtonClickListener());
		restoreTime();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		startTextCountdown();
	}

	private void restoreTime() {
		SharedPreferences settings = getPreferences(0);
		if (settings != null) {
			long milliseconds = settings.getLong(ALARM_TIME, 0);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(milliseconds);
			mAlarmApplication.setCurrentAlarmCalendar(c);
		}
	}

	private void startTextCountdown() {
		Calendar c = mAlarmApplication.getCurrentAlarmCalendar();
		if (c != null && c.getTimeInMillis() != 0) {
			long alarmTime = c.getTimeInMillis();
			long currentTime = Calendar.getInstance().getTimeInMillis();
			long timeDifference = alarmTime - currentTime;
			if(timeDifference > 0) {
				mCountingDown = true;
			}
			else {
				mCountingDown = false;
			}
			mCountDownTimer = new CountDownTimer(timeDifference, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					mTimeView
							.setText(AlarmUtil
									.getTimeStringFromMilliseconds(millisUntilFinished));
				}

				@Override
				public void onFinish() {
					mTimeView.setText(R.string.default_time);
					mCountingDown = false;
				}
			};
			mCountDownTimer.start();
		}
		else {
			mCountingDown = false;
		}
	}
	
	private void stopTextCountdown() {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
	}

	private OnClickListener numpadButtonClickListener() {
		return new OnClickListener() {
			public void onClick(View v) {
				if(!mCountingDown) {
					mTime = mTime.concat(((Button) v).getText().toString());
					updateTimeView();
				}
			}
		};
	}

	private void updateTimeView() {
		Integer hours = AlarmUtil.getHoursFromTimeString(mTime);
    	Integer minutes = AlarmUtil.getMinutesFromTimeString(mTime);
    	Integer seconds = AlarmUtil.getSecondsFromTimeString(mTime);
    	mTimeView.setText(String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(ALARM_TIME, mAlarmApplication.getCurrentAlarmCalendar()
				.getTimeInMillis());
		editor.commit();
	}
}
