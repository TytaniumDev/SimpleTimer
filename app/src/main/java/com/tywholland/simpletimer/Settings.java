package com.tywholland.simpletimer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.tywholland.simpletimer.R;

public class Settings extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
}
