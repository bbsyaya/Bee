package com.lingshi321.bee;

import java.util.HashMap;
import java.util.Map;

import com.lingshi321.bee.common.LoginRunnable;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.service.PushService;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.SharedPreferencesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

public class LaunchActivity extends Activity {

	private LaunchHandler handler = null;

	private final long TIME_NORMAL = 1500;
	private final long TIME_TIMEOUT = 5000;
	private long time = -1;

	private final int TIMEOUT = -200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_launch);

		handler = new LaunchHandler();

		// 记录当前时间
		time = System.currentTimeMillis();

		init();
		
		// 发送一条消息，目的，登陆超时。
		handler.sendEmptyMessageDelayed(TIMEOUT, TIME_TIMEOUT);
	}

	private void init() {

		if (SharedPreferencesUtil.getPhone() != null
				&& !SharedPreferencesUtil.getPhone().equals("")) {

			login();

		} else {
			Message msg = handler.obtainMessage();
			msg.what = RequestResult.NODATA;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 登录操作
	 */
	private void login() {

		Map<String, String> params = new HashMap<String, String>();
		params.put(DataInterfaceConstants.PHONE,
				SharedPreferencesUtil.getUserPhone());
		params.put(DataInterfaceConstants.PASSWORD,
				SharedPreferencesUtil.getPassword());

		new Thread(new LoginRunnable(handler, params, null)).start();

	}

	private void startLogin() {
		startActivity(new Intent().setClass(LaunchActivity.this,
				LoginActivity.class));
		finish();
		overridePendingTransition(R.anim.alpha_, R.anim.alpha__);
	}

	public void handBackInfo(Message msg) {
		switch (msg.what) {
		case RequestResult.NORMAL:
			
			Intent intent = new Intent();
			intent.setClass(LaunchActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.alpha_, R.anim.alpha__);

			// 登录openfire服务器 推送服务取消
			// startService(new Intent().setClass(LaunchActivity.this,
			// PushService.class));

			break;
		case RequestResult.NONETWORK:
			Toast.makeText(getBaseContext(), msg.obj.toString(),
					Toast.LENGTH_LONG).show();
			startLogin();
			break;
		case RequestResult.REQUESTERROR:
			Toast.makeText(getBaseContext(), msg.obj.toString(),
					Toast.LENGTH_LONG).show();
			startLogin();
			break;
		case RequestResult.NODATA:
			startLogin();
			break;
		default:
			Toast.makeText(getBaseContext(), msg.obj.toString(),
					Toast.LENGTH_LONG).show();
			startLogin();
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	class LaunchHandler extends Handler {

		private boolean isExecute = false;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 这里是为了，启动页有个给用户展现的时间
			long time_ = System.currentTimeMillis() - time;
			if (!isExecute) {
				if (msg.what == TIMEOUT) {
					Toast.makeText(getBaseContext(),R.string.login_timeout,
							Toast.LENGTH_LONG).show();
					startLogin();
				} else {
					if (time_ < TIME_NORMAL)
						try {
							Thread.sleep(TIME_NORMAL - time_);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					handBackInfo(msg);
				}
			}
			isExecute = true;
		}

	}

}
