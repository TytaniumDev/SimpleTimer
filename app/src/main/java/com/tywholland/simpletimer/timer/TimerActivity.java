package com.tywholland.simpletimer.timer;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tywholland.simpletimer.R;
import com.tywholland.simpletimer.Settings;
import com.tywholland.simpletimer.SimpleTimerApplication;

public class TimerActivity extends Activity {
	private static final String ALARM_TIME = "alarmkey";
	private static final String ALARM_NAME = "alarmnamekey";
	private static final int TIME_MAX_LENGTH = 6;

	private Button mStartButton;
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
	private EditText mAlarmNameView;
	private SimpleTimerApplication mAlarmApplication;
	private CountDownTimer mCountDownTimer;
	private boolean mCountingDown;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(getApplicationContext(), Settings.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAlarmApplication = (SimpleTimerApplication) getApplicationContext();
		if (PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).getBoolean(
				getString(R.string.key_button_placement), true)) {
			setContentView(R.layout.activity_main);
		} else {
			setContentView(R.layout.activity_main_bottom_start);
		}
		mCountingDown = false;

		restoreText();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).getBoolean(
				getString(R.string.key_button_placement), true)) {
			setContentView(R.layout.activity_main);
		} else {
			setContentView(R.layout.activity_main_bottom_start);
		}
		mStartButton = (Button) findViewById(R.id.startbutton);
		mStartButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mAlarmApplication.stopTimer();
				mAlarmApplication.setAlarmName(mAlarmNameView.getText()
						.toString());
				mAlarmApplication.startTimer(TimeConversionUtil
						.convertStringToMilliseconds(mAlarmApplication
								.getTimeString()));
				stopTextCountdown();
				startTextCountdown();
				mAlarmApplication.setTimeString("");
				mCountingDown = true;
				updateButtons();
			}
		});
		mStopButton = (Button) findViewById(R.id.stopbutton);
		mStopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mAlarmApplication.setTimeString("");
				mAlarmApplication.setAlarmName("");
				mAlarmNameView.setText("");
				mTimeView.setText(R.string.default_time);
				mAlarmApplication.stopTimer();
				stopTextCountdown();
				mCountingDown = false;
				updateButtons();
			}
		});
		mAlarmNameView = (EditText) findViewById(R.id.alarmNameEditText);
		mAlarmNameView.setText(mAlarmApplication.getAlarmName());
		mAlarmNameView.clearFocus();
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
		updateTimeView();
		updateButtons();
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
		startTextCountdown();
		if (!mCountingDown) {
			// Stop timer when app is launched if timer is not active
			mAlarmApplication.stopTimer();
		}
	}

	private void restoreText() {
		SharedPreferences settings = getPreferences(0);
		if (settings != null) {
			long milliseconds = settings.getLong(ALARM_TIME, 0);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(milliseconds);
			mAlarmApplication.setCurrentAlarmCalendar(c);
			String alarmName = settings.getString(ALARM_NAME, "");
			mAlarmApplication.setAlarmName(alarmName);
		}
	}

	private void startTextCountdown() {
		Calendar c = mAlarmApplication.getCurrentAlarmCalendar();
		if (c != null && c.getTimeInMillis() != 0) {
			long alarmTime = c.getTimeInMillis();
			long currentTime = Calendar.getInstance().getTimeInMillis();
			long timeDifference = alarmTime - currentTime;
			if (timeDifference > 0) {
				mCountingDown = true;
			} else {
				mCountingDown = false;
			}
			mCountDownTimer = new CountDownTimer(timeDifference, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					mTimeView
							.setText(TimeConversionUtil
									.getTimeStringFromMilliseconds(millisUntilFinished));
				}

				@Override
				public void onFinish() {
					mTimeView.setText(R.string.default_time);
					mCountingDown = false;
				}
			};
			mCountDownTimer.start();
		} else {
			mCountingDown = false;
		}
		updateButtons();
	}

	private void stopTextCountdown() {
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
	}

	private OnClickListener numpadButtonClickListener() {
		return new OnClickListener() {
			public void onClick(View v) {
				if (!mCountingDown) {
					if (mAlarmApplication.getTimeString().length() < TIME_MAX_LENGTH) {
						mAlarmApplication.appendToTimeString(((Button) v)
								.getText().toString());
						updateTimeView();
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.time_too_long_warning,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
	}

	private void updateButtons() {
		mStartButton.setEnabled(!mCountingDown);
		mNumpad0.setEnabled(!mCountingDown);
		mNumpad1.setEnabled(!mCountingDown);
		mNumpad2.setEnabled(!mCountingDown);
		mNumpad3.setEnabled(!mCountingDown);
		mNumpad4.setEnabled(!mCountingDown);
		mNumpad5.setEnabled(!mCountingDown);
		mNumpad6.setEnabled(!mCountingDown);
		mNumpad7.setEnabled(!mCountingDown);
		mNumpad8.setEnabled(!mCountingDown);
		mNumpad9.setEnabled(!mCountingDown);
	}

	private void updateTimeView() {
		Integer hours = TimeConversionUtil.getHoursFromTimeString(mAlarmApplication
				.getTimeString());
		Integer minutes = TimeConversionUtil.getMinutesFromTimeString(mAlarmApplication
				.getTimeString());
		Integer seconds = TimeConversionUtil.getSecondsFromTimeString(mAlarmApplication
				.getTimeString());
		mTimeView.setText(String.format("%02d", hours) + ":"
				+ String.format("%02d", minutes) + ":"
				+ String.format("%02d", seconds));
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ALARM_NAME, mAlarmNameView.getText().toString());
		editor.commit();
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
