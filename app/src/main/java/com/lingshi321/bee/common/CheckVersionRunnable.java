package com.lingshi321.bee.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.ui.InformationFragment;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.NetworkCheck;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CheckVersionRunnable implements Runnable {

	private Handler handler = null;
	private String version = null;
	
	private final String TAG = "CheckVersionRunnable";

	public CheckVersionRunnable(Handler handler, String version) {
		this.handler = handler;
		this.version = version;
	}

	private void notifyUI(JSONObject responseContent) {
		Message msg = new Message();
		try {
			int code = responseContent.getInt("code");
			Log.i("code", code + "");
			switch (code) {
			case 0:
				msg.what = RequestResult.REQUESTERROR;
				msg.arg1 = InformationFragment.CHECKVERSION;
				msg.obj = responseContent.getString("msg");
				handler.sendMessage(msg);
				break;
			case 1:
				msg.what = RequestResult.NORMAL;
				msg.arg1 = InformationFragment.CHECKVERSION;
				msg.obj = responseContent.getJSONObject("data");
				handler.sendMessage(msg);
				break;
			default:
				break;
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}

	}

	/**
	 * 停止该线程
	 */
	public void stopThisThread() {
		Message msg = new Message();
		msg.what = RequestResult.REQUESTERROR;
		msg.arg1 = InformationFragment.CHECKVERSION;
		msg.obj = "服务器睡觉了";
		handler.sendMessage(msg);
	}

	public void noNetWork() {
		Message msg = new Message();
		msg.what = RequestResult.NONETWORK;
		msg.arg1 = InformationFragment.CHECKVERSION;
		msg.obj = "无网络";
		handler.sendMessage(msg);
	}

	@Override
	public void run() {
		if (!NetworkCheck.isNetworkAvailable()) {
			noNetWork();
			return;
		}
		String content = "";
		DefaultHttpClient httpClient = HttpClientSingleton.getHttpClient();
		try {
			HttpGet request = new HttpGet(DataInterfaceConstants.CHECKVERSION+version);
			HttpResponse response = httpClient.execute(request);
			int code = response.getStatusLine().getStatusCode();
			Log.i(TAG, "code="+code);
			switch (code) {
			case 200:
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.i(TAG, "content="+content);
				Message msg = handler.obtainMessage();
				msg.what = RequestResult.NORMAL;
				msg.arg1 = InformationFragment.CHECKVERSION;
				handler.sendMessage(msg);
//				JSONObject responseContent = analyResponseContent(content);
//				notifyUI(responseContent);
				break;

			default:
				stopThisThread();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONObject analyResponseContent(String content) {
		JSONObject responseContent = null;
		try {
			responseContent = new JSONObject(content);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return responseContent;
	}
}
