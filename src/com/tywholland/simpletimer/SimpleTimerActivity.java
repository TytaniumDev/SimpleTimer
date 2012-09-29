package com.tywholland.simpletimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tywholland.simpletimer.model.AlarmUtil;

public class SimpleTimerActivity extends Activity {
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
	private TextView mTimeView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTime = "";
        mButton = (Button) findViewById(R.id.button1);
        mButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlarmUtil.startTimer(getApplicationContext(), AlarmUtil.convertStringToMilliseconds(mTime));
			}
		});
        ((Button) findViewById(R.id.stopbutton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mTime = "";
				mTimeView.setText(R.string.default_time);
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
    }
    
    private OnClickListener numpadButtonClickListener() {
    	return new OnClickListener() {
			public void onClick(View v) {
				mTime = mTime.concat(((Button)v).getText().toString());
				updateTimeView();
			}
		};
    }
    
    private void updateTimeView() {
    	Integer hours = AlarmUtil.getHoursFromTimeString(mTime);
    	Integer minutes = AlarmUtil.getMinutesFromTimeString(mTime);
    	Integer seconds = AlarmUtil.getSecondsFromTimeString(mTime);
    	mTimeView.setText(String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
    }
}
