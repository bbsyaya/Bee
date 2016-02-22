package com.lingshi321.bee.ui;

import java.util.ArrayList;

import com.lingshi321.bee.LoginActivity;
import com.lingshi321.bee.MainActivity;
import com.lingshi321.bee.OrderDetailActivity;
import com.lingshi321.bee.R;
import com.lingshi321.bee.adapter.OrderAdapter;
import com.lingshi321.bee.domain.Order;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.util.RequestDataURLUtil;
import com.lingshi321.bee.widget.RefreshableListView;
import com.lingshi321.bee.widget.RefreshableListView.IReflashLister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class OrdersFragment extends Fragment implements IReflashLister, OnItemClickListener{

	private MainActivity mainActivity;
	private RelativeLayout tip;                       //网络错误或者没有数据的提示
	private RefreshableListView allOrdersListView;
	private OrderAdapter orderAdapter;
	private ArrayList<Order> orders;
	
	private int pageNumber = 1;
	private View foot;
	
	private Handler handler = new Handler() {
        @Override  
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        	case Order.ALLORDERS:
        		ArrayList<Order> backDatas = (ArrayList<Order>) msg.obj;
				if(msg.arg1 == 1 && backDatas.size() == 0){
					noDataFirstPage();
				}else if(msg.arg1 == 1 && backDatas.size() > 0){                //arg1表示是第几页，1表示第一页
					allOrdersListView.setVisibility(View.VISIBLE);
					tip.setVisibility(View.GONE);
					pageNumber = 2;
					orders.clear();
					orders.addAll(0, backDatas);
					orderAdapter.notifyDataSetChanged();
					allOrdersListView.reflashComplete(IReflashLister.PULLDOWN);
					foot.setVisibility(View.VISIBLE);
				}else if(backDatas.size() > 0){                            //往数据后面追加
					pageNumber++;
					orders.addAll(orders.size(), backDatas);
					orderAdapter.notifyDataSetChanged();
					allOrdersListView.reflashComplete(IReflashLister.PULLUP);
				}else if(backDatas.size() == 0){
					foot.setVisibility(View.GONE);
					if(getActivity()!=null)
					Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
				}
				break;
        	case RequestResult.NONETWORK:
				netWorkError();
				break;
			case RequestResult.SESSIONOVERTIME:                    //session过期，返回登录界面从新登录
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("tip", "登陆信息过期，请重新登陆");
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			default:
				break;
			}	
        }
    };
    
	public OrdersFragment(MainActivity mainActivity){
		this.mainActivity = mainActivity;
		orders = new ArrayList<Order>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_all, container, false);
		allOrdersListView = (RefreshableListView)layout.findViewById(R.id.all_orders_rlv);
		allOrdersListView.setInterface(this);
		orderAdapter = new OrderAdapter(orders, getActivity());
		allOrdersListView.setAdapter(orderAdapter);
		allOrdersListView.setOnItemClickListener(this);
		
		foot = layout.findViewById(R.id.foot_progress);
		foot.setVisibility(View.INVISIBLE);
		
		tip = (RelativeLayout) layout.findViewById(R.id.request_result_tip);
		tip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((TextView)tip.findViewById(R.id.commText)).setText(R.string.loading_data);
				RequestDataURLUtil.getOrdersURL(handler, getActivity(),Order.ALLORDERS, 1, Order.REQUESTPAGESIZE);
			}
		});
		
		allOrdersListView.setListViewPullDownReflash();
		
		return layout;
	}

	@Override
	public void onReflash(final int reflashType) {
		// TODO Auto-generated method stub
		if(reflashType == IReflashLister.PULLDOWN){       //表示下拉刷新
			RequestDataURLUtil.getOrdersURL(handler, getActivity(),Order.ALLORDERS, 1, Order.REQUESTPAGESIZE);
		}else{
			RequestDataURLUtil.getOrdersURL(handler,getActivity(), Order.ALLORDERS, pageNumber, Order.REQUESTPAGESIZE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		// TODO Auto-generated method stub
		index = index - 1;
		Order order = orders.get(index);
		Toast.makeText(mainActivity, order.school, Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.setClass(getActivity(), OrderDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", order);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == 1){   //表示上一个界面对订单信息进行了编辑，需要更新数据
			allOrdersListView.setListViewPullDownReflash();
		}
	}
	
	/**
	 * 网络异常显示网络异常
	 */
	private void netWorkError(){
		allOrdersListView.setVisibility(View.GONE);
		tip.setVisibility(View.VISIBLE);
		((ImageView)tip.findViewById(R.id.commImg)).setImageResource(R.drawable.blank_state_no_network_icon);
		((TextView)tip.findViewById(R.id.commText)).setText(R.string.no_net_work);
		pageNumber = 1;
	}
	
	/**
	 * 第一页没有数据
	 */
	private void noDataFirstPage(){
		allOrdersListView.setVisibility(View.GONE);
		tip.setVisibility(View.VISIBLE);
		((ImageView)tip.findViewById(R.id.commImg)).setImageResource(R.drawable.blank_state_no_data_icon);
		((TextView)tip.findViewById(R.id.commText)).setText(R.string.no_data);
		pageNumber = 1;
	}
}
