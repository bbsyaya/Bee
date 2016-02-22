package com.lingshi321.bee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lingshi321.bee.common.OrderDetailRunable;
import com.lingshi321.bee.domain.Food;
import com.lingshi321.bee.domain.Order;
import com.lingshi321.bee.ui.EditeSellDetailDialog;
import com.lingshi321.bee.util.DataInterfaceConstants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 订单详情界面
 * 
 * @author zengy
 * 
 */
public class OrderDetailActivity extends Activity implements OnClickListener {
	private Order order;

	private Button changeStatus;
	private Button editeSellButton;
	private OrderDetailHandler handler = null;
	private ProgressDialog progressDialog = null;
	private boolean isOnClick = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		order = (Order) intent.getSerializableExtra("order"); // 获取订单信息

		handler = new OrderDetailHandler();

		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置当前应用无title
		setContentView(R.layout.activity_order_detail);

		initialView();
		setButton();
	}

	/**
	 * 初始化界面
	 */
	private void initialView() {

		LinearLayout default_items = (LinearLayout) findViewById(R.id.default_items);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 动态构造套餐详情
		for (int index = 0; index < order.defaultDetail.size(); index++) {
			Food food = order.defaultDetail.get(index);
			LinearLayout orderItem = (LinearLayout) inflater.inflate(
					R.layout.sell_item, null);

			TextView food_name = (TextView) orderItem
					.findViewById(R.id.food_name);
			food_name.setText(food.foodName);
			TextView food_sell_price = (TextView) orderItem
					.findViewById(R.id.food_sell_price);
			food_sell_price.setText(food.sellPrice + "");
			TextView food_number = (TextView) orderItem
					.findViewById(R.id.food_number);
			food_number.setText(food.foodNum + "");
			TextView food_price = (TextView) orderItem
					.findViewById(R.id.food_price);
			food_price.setText(food.foodNum * food.sellPrice + "");
			default_items.addView(orderItem);
		}
		
		// 套餐总价信息
		TextView total = (TextView) findViewById(R.id.total_price);
		total.setText(countTotal(order.defaultDetail)+" RMB");

		if (order.status < Order.PAYED) { // 当订单状态值小于已结账时，表示订单为配送完成，未配送。显示还为结算，无销售详情
			LinearLayout food_sell_item_mark = (LinearLayout) findViewById(R.id.food_sell_item_mark);
			food_sell_item_mark.setVisibility(View.GONE);
		} else { // 显示销售详情
			showSellItem();
		}

		// 订单信息
		TextView order_queryid = (TextView) findViewById(R.id.order_queryid);
		TextView order_create_time = (TextView) findViewById(R.id.order_create_time);
		TextView order_send_time = (TextView) findViewById(R.id.order_send_time);
		TextView order_sell_money = (TextView) findViewById(R.id.order_sell_money);
		TextView order_school = (TextView) findViewById(R.id.order_school);
		TextView order_address = (TextView) findViewById(R.id.order_address);
		TextView order_note = (TextView) findViewById(R.id.order_note);

		order_queryid.setText(order.orderQueryId);
		order_create_time.setText(order.createTime);
		order_send_time.setText(order.sendTime);
		order_sell_money.setText(order.selleMoney + "");
		order_school.setText(order.school);
		order_address.setText(order.building + " " + order.dormitory);
		order_note.setText(order.note);
	}

	/**
	 * 显示销售详情
	 */
	private void showSellItem() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TextView not_sell_item = (TextView) findViewById(R.id.not_sell_item);
		RelativeLayout sell_total = (RelativeLayout) findViewById(R.id.sell_total);
		if (not_sell_item != null) { // 当订单为已结算，则把还未结算，无销售详情隐藏，并显示总价信息
			not_sell_item.setVisibility(View.GONE);
			sell_total.setVisibility(View.VISIBLE);
		}

		LinearLayout food_sell_item_mark = (LinearLayout) findViewById(R.id.food_sell_item_mark);
		food_sell_item_mark.setVisibility(View.VISIBLE);

		LinearLayout sell_items = (LinearLayout) findViewById(R.id.sell_items);
		sell_items.removeAllViews();

		for (int index = 0; order.sellDetail != null
				&& index < order.sellDetail.size(); index++) {
			Food food = order.sellDetail.get(index);
			LinearLayout orderItem = (LinearLayout) inflater.inflate(
					R.layout.sell_item, null);

			TextView food_name = (TextView) orderItem
					.findViewById(R.id.food_name);
			food_name.setText(food.foodName);
			TextView food_sell_price = (TextView) orderItem
					.findViewById(R.id.food_sell_price);
			food_sell_price.setText(food.sellPrice + "");
			TextView food_number = (TextView) orderItem
					.findViewById(R.id.food_number);
			food_number.setText(food.foodNum + "");
			TextView food_price = (TextView) orderItem
					.findViewById(R.id.food_price);
			food_price.setText(food.foodNum * food.sellPrice + "");
			sell_items.addView(orderItem);
		}
		
		// 总价信息
		TextView total_price = (TextView) findViewById(R.id.total_sell_price);
		total_price.setText(countTotal(order.sellDetail)+" RMB");
	}

	/**
	 * 设置按钮的内容
	 */
	private void setButton() {
		editeSellButton = (Button) findViewById(R.id.edite_sell_item_button);
		editeSellButton.setOnClickListener(this);
		if (order.status != Order.DELIVERED) { // 当订单的状态为已配送时，即可编辑销售详情
			editeSellButton.setVisibility(View.GONE);
		}

		changeStatus = (Button) findViewById(R.id.change_status);
		changeStatus.setOnClickListener(this);
		switch (order.status) {
		case Order.NOTDELIVER:
			changeStatus.setVisibility(View.GONE);
			break;
		case Order.DELIVERING:
			changeStatus.setText("完成配送");
			break;
		case Order.DELIVERED:
			changeStatus.setText("确定结算");
			break;
		case Order.PAYED:
			changeStatus.setVisibility(View.GONE);
			break;
		case Order.OVER:
			changeStatus.setVisibility(View.GONE);
			break;
		default:
			break;
		}

		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);

	}

	/**
	 * 更新销售详情，弹框回调
	 */
	public void updateView() {
		showSellItem();
	}

	/**
	 * 更新该订单的价格，弹框回调
	 */
	public void updatePrice() {
		TextView order_sell_money = (TextView) findViewById(R.id.order_sell_money);
		order_sell_money.setText(order.selleMoney + "");
	}

	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {
		case R.id.change_status:
			if(!isOnClick){
				isOnClick = true;
			progressDialog = ProgressDialog.show(OrderDetailActivity.this,
					null, "正在努力加载中..", true);
			
			switch (order.status) {
			case Order.DELIVERING:
				Map<String, String> params = new HashMap<String, String>();
				params.put(DataInterfaceConstants.ORDER_ID, order.orderId + "");
				params.put(DataInterfaceConstants.ORDER_STATUS, Order.DELIVERED
						+ "");

				Log.i("order", "id=" + order.orderId + "status="
						+ Order.DELIVERED);

				new Thread(new OrderDetailRunable(handler, params, null,
						Order.DELIVERING)).start();
				break;
			case Order.DELIVERED:
				if (order.sellDetail != null && order.sellDetail.size() > 0) {
					Map<String, String> params1 = new HashMap<String, String>();
					params1.put(DataInterfaceConstants.ORDER_ID, order.orderId
							+ "");
					params1.put(DataInterfaceConstants.ORDER_STATUS,
							Order.PAYED + "");

					Log.i("order", "id=" + order.orderId + "status="
							+ Order.PAYED);

					new Thread(new OrderDetailRunable(handler, params1,
							order.sellDetail, Order.DELIVERED)).start();
				} else {

					Toast.makeText(getApplication(), "请先结算!", Toast.LENGTH_LONG)
							.show();
					if (progressDialog != null) {
						progressDialog.dismiss();
						isOnClick = false;
					}
				}
				break;
			default:
				break;
			}
			break;
			}
		case R.id.edite_sell_item_button:
			popDialog();
			break;
		case R.id.back:
			if (order.status == Order.DELIVERING
					|| order.status == Order.DELIVERED) {
				setResult(1);
			}
			finish();
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (order.status == Order.DELIVERING
					|| order.status == Order.DELIVERED) {
				setResult(1);
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 弹出输入框
	 */
	private void popDialog() {
		EditeSellDetailDialog sellDetailDialog = new EditeSellDetailDialog(
				this, OrderDetailActivity.this, order);
		sellDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		sellDetailDialog.show();
	}

	/**
	 * 计算总价
	 * @param orders
	 * @return
	 */
	private float countTotal(ArrayList<Food> orders){
		int i = 0;
		for (Food food : orders) {
			i += (food.foodNum*food.sellPrice);
		}
		return i;
	}
	
	@SuppressLint("HandlerLeak")
	class OrderDetailHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 200) {

				if (order.status == Order.DELIVERING
						|| order.status == Order.DELIVERED) {
					setResult(1);
				}
				finish();
				Toast.makeText(getApplication(), "改变状态成功!", Toast.LENGTH_LONG)
						.show();
			}
			if (progressDialog != null) {
				progressDialog.dismiss();
				isOnClick = false;
			}

		}

	}
}
