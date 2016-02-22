package com.lingshi321.bee.base;

import com.lingshi321.bee.util.NetworkCheck;
import com.lingshi321.bee.util.SharedPreferencesUtil;

import android.app.Application;

public class App extends Application{

	@Override
	public void onCreate() {
		
		super.onCreate();
		NetworkCheck.setContext(getApplicationContext());
		SharedPreferencesUtil.context = this.getApplicationContext();
	}

	
}
