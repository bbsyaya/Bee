package com.lingshi321.bee.common;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Message;

import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.ui.InformationFragment;
import com.lingshi321.bee.ui.InformationFragment.InformationHandler;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.NetworkCheck;

public class LogoutRunnable implements Runnable {

	private InformationHandler handler = null;

	public LogoutRunnable(InformationHandler handler) {

		this.handler = handler;
	}

	/**
	 * 停止该线程
	 */
	public void stopThisThread() {
		Message msg = new Message();
		msg.what = RequestResult.REQUESTERROR;
		msg.arg1 = InformationFragment.LOGOUT;
		msg.obj = "服务器睡觉了";
		handler.sendMessage(msg);
	}

	public void noNetWork() {
		Message msg = new Message();
		msg.what = RequestResult.NONETWORK;
		msg.arg1 = InformationFragment.LOGOUT;
		msg.obj = "无网络";
		handler.sendMessage(msg);
	}

	@Override
	public void run() {

		if (!NetworkCheck.isNetworkAvailable()) {
			noNetWork();
			return;
		}
		try {
			DefaultHttpClient httpClient = HttpClientSingleton.getHttpClient();
			HttpGet httpGet = new HttpGet(DataInterfaceConstants.LOGOUT);
			HttpResponse response = httpClient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			switch (code) {
			case 200:
				Message message = new Message();
				message.what = RequestResult.NORMAL;
				message.arg1 = InformationFragment.LOGOUT;
				handler.sendMessage(message);
				break;

			default:
				break;
			}
			
		} catch (Exception e) {
			stopThisThread();
			e.printStackTrace();
		}

	}

}
