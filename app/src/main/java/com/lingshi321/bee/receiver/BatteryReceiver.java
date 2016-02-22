package com.lingshi321.bee.receiver;

import com.lingshi321.bee.service.PushService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * 监听电池变化
 * 
 * @author Administrator
 */
public class BatteryReceiver extends BroadcastReceiver {

	private final String TAG = "BatteryReceiver";
	private int batteryStatus = -1;

	public BatteryReceiver() {

	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

			batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS,
					BatteryManager.BATTERY_STATUS_UNKNOWN)) {
			case BatteryManager.BATTERY_STATUS_UNKNOWN: // 未知

				break;
			case BatteryManager.BATTERY_STATUS_CHARGING: // 充电

				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING: // 未充电

				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING: // 放电

				break;
			case BatteryManager.BATTERY_STATUS_FULL: // 充满电

				break;
			default:
				break;
			}
			switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
			case BatteryManager.BATTERY_PLUGGED_USB:

				break;
			case BatteryManager.BATTERY_PLUGGED_AC:

				break;
			case BatteryManager.BATTERY_PLUGGED_WIRELESS:

				break;
			default:
				break;
			}

		}

		Log.i(TAG, "batteryStatus=" + batteryStatus);
		// 推送服务取消
//		context.startService(new Intent().setClass(context, PushService.class));

	}

}
