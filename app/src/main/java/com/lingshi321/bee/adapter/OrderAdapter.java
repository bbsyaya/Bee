package com.lingshi321.bee.adapter;

import java.util.ArrayList;

import com.lingshi321.bee.R;
import com.lingshi321.bee.domain.Order;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Order> data;
	private Context context;

	public OrderAdapter(ArrayList<Order> data, Activity activity) {
		this.data = data;
		this.activity = activity;
		this.context = activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Order order = data.get(position);
		View view = View.inflate(context, R.layout.order_item, null);

		TextView queryId = (TextView) view.findViewById(R.id.order_query_id);
		queryId.setText(order.orderQueryId);

		TextView address = (TextView) view
				.findViewById(R.id.order_item_address);
		address.setText(order.building + " " + order.dormitory);

		final TextView phone = (TextView) view
				.findViewById(R.id.order_item_phone);
		phone.setText(order.phone);

		TextView allPrice = (TextView) view.findViewById(R.id.order_item_price);
		allPrice.setText(order.defaultMoney + "");

		TextView sendTime = (TextView) view
				.findViewById(R.id.order_item_send_time);
		sendTime.setText(order.createTime);
		TextView status = (TextView) view.findViewById(R.id.order_status);

		RelativeLayout orderPhone = (RelativeLayout) view
				.findViewById(R.id.order_phone);

		orderPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("确认拨打电话?")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String phoneNumber = phone.getText().toString();
								if (phoneNumber != null && !phoneNumber.equals("")) {
									Intent intent = new Intent(Intent.ACTION_CALL, Uri
											.parse("tel:" + phoneNumber));
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intent);
								}
							}
						})
						.setNegativeButton("取消", null)
						.show();

			}
		});

		switch (order.status) {
		case Order.NOTDELIVER:
			status.setText(context.getResources().getText(R.string.not_deliver));
			break;
		case Order.DELIVERING:
			status.setText(context.getResources().getText(R.string.delivering));
			break;
		case Order.DELIVERED:
			status.setText(context.getResources().getText(R.string.delivered));
			break;
		case Order.PAYED:
			status.setText(context.getResources().getText(R.string.payed));
			break;
		default:
			break;
		}
		return view;
	}

}
