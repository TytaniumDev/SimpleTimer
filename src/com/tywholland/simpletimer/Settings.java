package com.tywholland.simpletimer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, SimpleTimerActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
}
