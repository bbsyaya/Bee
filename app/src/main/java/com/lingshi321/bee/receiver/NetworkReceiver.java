package com.lingshi321.bee.receiver;

import com.lingshi321.bee.service.PushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 监听网络改变
 * 
 * @author Administrator
 * 
 */

public class NetworkReceiver extends BroadcastReceiver {

	private final String TAG = "NetworkReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifiInfo.isConnected()) {

			Log.i(TAG, "WiFi is connected!");
			// 推送服务取消
//			context.startService(new Intent().setClass(context, PushService.class));
		}
		
		if(mobileInfo.isConnected()) {
			
			Log.i(TAG, "mobile is connected!");
			// 推送服务取消
//			context.startService(new Intent().setClass(context, PushService.class));
		}

	}

}
