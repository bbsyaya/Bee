package com.lingshi321.bee.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lingshi321.bee.R;
import com.lingshi321.bee.domain.Food;
import com.lingshi321.bee.domain.Order;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.NetworkCheck;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyOrderDataRunnable implements Runnable {

    private Handler handler;
    private int orderStatus;
    private int pageNumber;
    private int pageSize;
    private final String TAG = "MyOrderDataRunnable";
    private Context context;

    public MyOrderDataRunnable(Handler handler,Context context, int orderStatus,
                               int pageNumber, int pageSize) {
        super();
        this.handler = handler;
        this.context = context;
        this.orderStatus = orderStatus;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    private void notifyUI(JSONObject responseContent) {
        Message msg = new Message();
        msg.arg1 = pageNumber;
        msg.what = orderStatus;
        ArrayList<Order> datas = new ArrayList<Order>();
        JSONObject dataObject;
        try {
            dataObject = responseContent.getJSONObject("data");
            msg.arg2 = dataObject.getInt("total");
            JSONArray orders = dataObject.getJSONArray("orders");
            for (int index = 0; index < orders.length(); index++) {
                JSONObject data = orders.getJSONObject(index);
                Order order = new Order();

                order.orderId = data.getLong("order_id");
                order.orderQueryId = data.getString("order_query_id");
                order.dormitory = data.getString("dormitory_name");
                order.building = data.getString("school_name");
                order.costMoney = (float) data.getDouble("order_cost_money");
                order.selleMoney = (float) data.getDouble("order_sell_money");
                order.defaultMoney = (float) data.getDouble("order_cost_money");
                order.createTime = data.getString("order_create_time");
                order.school = data.getString("school_name");
                order.status = data.getInt("order_status");
                order.note = data.getString("order_note");
                if (data.getJSONArray("phones").length() > 0)
                    order.phone = data.getJSONArray("phones").get(0).toString();
                else
                    order.phone = context.getString(R.string.phone_null);
                order.defaultDetail = new ArrayList<Food>();

                JSONArray defaultItems = data.optJSONArray("items");
                if (null != defaultItems) {
                    for (int index1 = 0; index1 < defaultItems.length(); index1++) {
                        JSONObject foodData = defaultItems.getJSONObject(index1);
                        Food food = new Food();
                        food.barCode = foodData.getString("snacks_bar_code");
                        food.costPrice = (float) foodData.getDouble("snacks_cost_price");
                        food.foodImgUrl = foodData.getString("snacks_pic");
                        food.foodId = foodData.getLong("snacks_id");
                        food.foodName = foodData.getString("snacks_name");
                        food.foodNum = foodData.getInt("snacks_number");
                        food.sellPrice = (float) foodData.getDouble("snacks_sell_price");
                        order.defaultDetail.add(food);
                    }
                }

                order.sellDetail = new ArrayList<Food>();
                JSONArray sellItems = data.optJSONArray("sell_items");
                if (null != sellItems) {
                    for (int index2 = 0; index2 < sellItems.length(); index2++) {
                        JSONObject foodData = sellItems.getJSONObject(index2);
                        Food food = new Food();
                        food.barCode = foodData.getString("snacks_bar_code");
                        food.costPrice = (float) foodData.getDouble("snacks_cost_price");
                        food.foodImgUrl = foodData.getString("snacks_pic");
                        food.foodId = foodData.getLong("snacks_id");
                        food.foodName = foodData.getString("snacks_name");
                        food.foodNum = foodData.getInt("snacks_number");
                        food.sellPrice = (float) foodData.getDouble("snacks_sell_price");
                        order.sellDetail.add(food);
                    }
                }
                datas.add(order);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.obj = datas;
        handler.sendMessage(msg);
    }

    public void stopThisThread() {
        Message msg = new Message();
        msg.arg1 = pageNumber;
        msg.what = RequestResult.STOPTHREAD;
        ArrayList<Order> datas = new ArrayList<Order>();
        msg.obj = datas;
        handler.sendMessage(msg);
    }

    private void notifyUINONETWORK() {
        Message msg = new Message();
        msg.what = RequestResult.NONETWORK;
        msg.obj = "无网络";
        handler.sendMessage(msg);
    }

    /**
     * session过期
     */
    private void sessionOverTime() {
        Message msg = new Message();
        msg.what = RequestResult.SESSIONOVERTIME;
        handler.sendMessage(msg);
    }

    /**
     * 当请求第一页数据时，返回界面消息包
     */
    private void notifyUINODATA() {
        Message msg = new Message();
        msg.what = RequestResult.NODATA;
        handler.sendMessage(msg);
    }

    private void notifyUIServerError() {
        Message msg = new Message();
        msg.what = RequestResult.REQUESTERROR;
        handler.sendMessage(msg);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
//
//		String requestURL = DataInterfaceConstants.HOST + DataInterfaceConstants.ORDERSURL + "?"
//				+ DataInterfaceConstants.USERID + "=" + SharedPreferencesUtil.getUserId() + "&"
//				+ DataInterfaceConstants.TOKEN + "=" + SharedPreferencesUtil.getToken();

        if (!NetworkCheck.isNetworkAvailable()) {
            notifyUINONETWORK();
            return;
        }

        try {
            HttpPost request = new HttpPost(DataInterfaceConstants.ORDERSURL);
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            if (orderStatus != Order.ALLORDERS) {
                args.add(new BasicNameValuePair(DataInterfaceConstants.STATUS, orderStatus + ""));
            }
            args.add(new BasicNameValuePair(DataInterfaceConstants.PAGENUMBER, pageNumber + ""));
            args.add(new BasicNameValuePair(DataInterfaceConstants.PAGESIZE, pageSize + ""));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(args, "UTF-8");
            request.setEntity(entity);
            // 浏览器去执行这次get请求，返回一个响应对象
            DefaultHttpClient httpClient = HttpClientSingleton.getHttpClient();
            HttpResponse response = httpClient.execute(request);

            int code = response.getStatusLine().getStatusCode();
            String content = "";
            Log.i(TAG, "code=" + code);
            switch (code) {
                case 200:
                    // 读取响应的内容
                    content = EntityUtils.toString(response.getEntity(), "UTF-8");
                    Log.i("test1", content);
                    JSONObject responseContent = analyResponseContent(content);
                    notifyUI(responseContent);
                    break;
                default:
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            sessionOverTime();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            notifyUIServerError();
        }
    }

    private JSONObject analyResponseContent(String content) throws JSONException {
        JSONObject responseContent = null;
        responseContent = new JSONObject(content);
        return responseContent;
    }

    private ArrayList<Order> analyOrders(String backData) {
        ArrayList<Order> datas = new ArrayList<Order>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.orderId = 100001;
            order.orderQueryId = "cqupt2706201512252314";
            order.phone = "136182603789";
            order.school = "重庆邮电大学" + i;
            order.building = "27栋";
            order.dormitory = "706";
            order.createTime = "12月23日 23:23 23";
            order.sendTime = "12月25日 23:23 23";
            order.costMoney = 23.5f;
            order.selleMoney = 24.5f;
            order.defaultMoney = 24.5f;
            order.note = "你妹的啊";
            if (orderStatus == Order.ALLORDERS) {
                Random random = new Random();
                int randomNumber = (int) ((Math.random()) * 6);
                Log.e("ee", randomNumber * 10 + "");
                order.status = Order.NOTDELIVER + randomNumber * 10;
            } else {
                order.status = orderStatus;
            }

            order.defaultDetail = new ArrayList<Food>();
            for (int index = 0; index < 10; index++) {
                Food food = new Food();
                food.foodId = index + 10000001l;
                food.barCode = "6900000000" + index;
                food.foodName = "百事可乐" + index;
                food.foodImgUrl = "http://adf.com/ab.jpg";
                food.costPrice = 12f + index;
                food.sellPrice = 10f + index;
                food.foodNum = index;
                order.defaultDetail.add(food);
            }
            Log.e("订单", order.status + "");
            if (order.status > Order.DELIVERED) {
                Log.e("订单", "创建订单详情");
                order.sellDetail = new ArrayList<Food>();
                for (int index = 0; index < 10; index++) {
                    Food food = new Food();
                    food.foodId = index + 10000001l;
                    food.barCode = "6900000000" + index;
                    food.foodName = "百事可乐" + index;
                    food.foodImgUrl = "http://adf.com/ab.jpg";
                    food.costPrice = 12f + index;
                    food.sellPrice = 10f + index;
                    food.foodNum = index;
                    order.sellDetail.add(food);
                }
            }
            datas.add(order);
        }
        return datas;
    }
}
