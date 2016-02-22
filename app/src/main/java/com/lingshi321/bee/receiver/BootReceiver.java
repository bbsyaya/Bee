package com.lingshi321.bee.receiver;


import com.lingshi321.bee.service.PushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 接收系统广播，开机自启服务
 * 
 * @author Administrator
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	private final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			
			Log.i(TAG, "ACTION_BOOT_COMPLETED");
			// 推送服务取消
//			context.startService(new Intent().setClass(context, PushService.class));
				
			
		}

	}

}
