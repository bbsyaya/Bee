package com.lingshi321.bee.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.lingshi321.bee.LoginActivity;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.NetworkCheck;
import com.lingshi321.bee.util.SharedPreferencesUtil;

import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.util.Log;

public class LoginRunnable implements Runnable {

	private Handler handler;
	private Map<String, String> params;
	private LoginActivity loginActivity;
	private final String TAG = "LoginRunnable";

	public LoginRunnable(Handler handler, Map<String, String> params,
			LoginActivity loginActivity) {
		this.handler = handler;
		this.params = params;
		this.loginActivity = loginActivity;
	}

	private void notifyUI(JSONObject responseContent) {
		Message msg = new Message();
		try {
			int code = responseContent.getInt("code");
			Log.i("code", code + "");
			switch (code) {
			case 0:
				msg.what = RequestResult.REQUESTERROR;
				msg.obj = responseContent.getString("msg");
				handler.sendMessage(msg);
				break;
			case 1:
				msg.what = RequestResult.NORMAL;
				msg.obj = responseContent.getJSONObject("data");
				handler.sendMessage(msg);
				break;
			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 停止该线程
	 */
	public void stopThisThread() {
		Message msg = new Message();
		msg.what = RequestResult.REQUESTERROR;
		msg.obj = "服务器睡觉了";
		handler.sendMessage(msg);
	}

	public void noNetWork() {
		Message msg = new Message();
		msg.what = RequestResult.NONETWORK;
		msg.obj = "无网络";
		handler.sendMessage(msg);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (!NetworkCheck.isNetworkAvailable()) {
			noNetWork();
			return;
		}

		String content = "";
		DefaultHttpClient httpClient = HttpClientSingleton.getHttpClient();
		try {
			HttpPost request = new HttpPost(DataInterfaceConstants.LOGINURL);
			List<NameValuePair> args = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				args.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(args,
					"UTF-8");
			request.setEntity(entity);
			// 浏览器去执行这次get请求，返回一个响应对象

			HttpResponse response = httpClient.execute(request);

			// JSESSIONID
			// String setCookie =
			// response.getFirstHeader("Set-Cookie").getValue();
			// String jsessionId = setCookie.substring("JSESSIONID=".length(),
			// setCookie.indexOf(";"));
			// SharedPreferencesUtil.setSessionId(jsessionId);

			// 获取响应码
			int code = response.getStatusLine().getStatusCode();
			// Header[] headers = response.getAllHeaders();
			// String c = "";
			// for (Header h : headers) {
			// c += h.getName() + ":" + h.getValue() + "\n";
			// }

			Log.i(TAG, "code=" + code);
			switch (code) {
			case 200:
				// 读取响应的内容
				content = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.i(TAG, content);
				JSONObject responseContent = analyResponseContent(content);
				notifyUI(responseContent);
				break;
			default:
				stopThisThread();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			stopThisThread();
		}
	}

	private JSONObject analyResponseContent(String content) {
		System.out.println(content);
		JSONObject responseContent = null;
		try {
			responseContent = new JSONObject(content);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseContent;
	}
}
