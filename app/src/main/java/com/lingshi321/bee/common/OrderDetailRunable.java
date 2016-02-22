package com.lingshi321.bee.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.lingshi321.bee.domain.Food;
import com.lingshi321.bee.domain.Order;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.NetworkCheck;

public class OrderDetailRunable implements Runnable {

	private Handler handler;
	private Map<String, String> params;
	private int orderStatus;
	private ArrayList<Food> sellDetail = null;
	public OrderDetailRunable(Handler handler, Map<String, String> params,
			ArrayList<Food> sellDetail,
			int orderStatus) {
		this.handler = handler;
		this.params = params;
		this.sellDetail = sellDetail;
		this.orderStatus = orderStatus;
	}

	private void notifyUI(int code) {

		Message msg = handler.obtainMessage();
		msg.what = code;
		handler.sendMessage(msg);

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

		DefaultHttpClient httpClient = HttpClientSingleton.getHttpClient();
		int code = -1;
		try {
			if (orderStatus == Order.DELIVERING) { // 这是完成配送状态改变
				HttpPost request = new HttpPost(
						DataInterfaceConstants.CHANGEDELIVERING);
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

				// 获取响应码
				code = response.getStatusLine().getStatusCode();

			} else if(orderStatus == Order.DELIVERED){
				Log.i("json", disposeData() + "");
				HttpPost request = new HttpPost(
						DataInterfaceConstants.CHANGEDELIVERINGPAY);
				List<NameValuePair> args = new ArrayList<NameValuePair>();
				args.add(new BasicNameValuePair(
						DataInterfaceConstants.ORDER_ID, params
								.get(DataInterfaceConstants.ORDER_ID)));
				args.add(new BasicNameValuePair(DataInterfaceConstants.SELL_ITEMS, disposeData() + ""));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(args,
						"UTF-8");
				request.setEntity(entity);
				HttpResponse response = httpClient.execute(request);

				code = response.getStatusLine().getStatusCode();

				System.out.println("#########################################");
			}

			Log.i("orderChange", code + "");

			notifyUI(code);
			
		} catch (Exception e) {
			e.printStackTrace();
			stopThisThread();
		}
	}

	/**
	 * 将sellDetail封装成JSON
	 * 
	 * @return
	 */
	private JSONObject disposeData() {

		if (sellDetail != null) {

			JSONObject jsonObject = new JSONObject();
			JSONArray array = new JSONArray();
			for (int i = 0; i < sellDetail.size(); i++) {
				JSONObject object = new JSONObject();
				try {
					object.put(DataInterfaceConstants.SNACKS_NUM,
							sellDetail.get(i).foodNum);
					object.put(DataInterfaceConstants.SNACKS_ID,
							sellDetail.get(i).foodId);
				} catch (JSONException e) {

					e.printStackTrace();
				}
				array.put(object);
			}
			if (array != null) {
				try {
					jsonObject.put(DataInterfaceConstants.FOODS, array);
					return jsonObject;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

}
