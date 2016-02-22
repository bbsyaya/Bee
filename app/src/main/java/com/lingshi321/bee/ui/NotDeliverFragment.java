package com.lingshi321.bee.ui;

import java.util.ArrayList;

import com.lingshi321.bee.LaunchActivity;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

@SuppressLint("ValidFragment")
public class NotDeliverFragment extends Fragment implements IReflashLister, OnItemClickListener {

    private MainActivity mainActivity;
    private RefreshableListView notDeliverListView;
    private OrderAdapter orderAdapter;

    private ArrayList<Order> orders;

    private int pageNumber = 1;
    private View foot;

    private RelativeLayout tip;                       //网络错误或者没有数据的提示

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Order.NOTDELIVER:
                    ArrayList<Order> backDatas = (ArrayList<Order>) msg.obj;
                    if (msg.arg1 == 1 && backDatas.size() == 0) {
                        noDataFirstPage();
                    } else if (msg.arg1 == 1 && backDatas.size() > 0) {                //arg1表示是第几页，1表示第一页
                        notDeliverListView.setVisibility(View.VISIBLE);
                        tip.setVisibility(View.GONE);
                        pageNumber = 2;
                        orders.clear();
                        orders.addAll(0, backDatas);
                        orderAdapter.notifyDataSetChanged();
                        notDeliverListView.reflashComplete(IReflashLister.PULLDOWN);
                        foot.setVisibility(View.VISIBLE);
                    } else if (backDatas.size() > 0) {                            //往数据后面追加
                        pageNumber++;
                        orders.addAll(orders.size(), backDatas);
                        orderAdapter.notifyDataSetChanged();
                        notDeliverListView.reflashComplete(IReflashLister.PULLUP);
                    } else if (backDatas.size() == 0) {
                        foot.setVisibility(View.GONE);
                        Toast.makeText(getActivity().getApplicationContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    break;
                case RequestResult.NONETWORK:
                    netWorkError();
                    break;
                case RequestResult.SESSIONOVERTIME:                    //session过期，返回登录界面从新登录
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LaunchActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("tip", "登陆信息过期，请重新登陆");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();
                    Toast.makeText(getActivity(), "登陆信息过期，正在重新登陆..", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public NotDeliverFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        orders = new ArrayList<Order>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.fragment_not_deliver, container, false);
        notDeliverListView = (RefreshableListView) layout
                .findViewById(R.id.not_delivery_rlv);
        notDeliverListView.setInterface(this);
        orderAdapter = new OrderAdapter(orders, getActivity());
        notDeliverListView.setAdapter(orderAdapter);
        notDeliverListView.setOnItemClickListener(this);

        foot = layout.findViewById(R.id.foot_progress);
        foot.setVisibility(View.INVISIBLE);

        tip = (RelativeLayout) layout.findViewById(R.id.request_result_tip);
        tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ((TextView) tip.findViewById(R.id.commText)).setText(R.string.loading_data);
                RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.NOTDELIVER, 1, Order.REQUESTPAGESIZE);
            }
        });
        notDeliverListView.setListViewPullDownReflash();
        return layout;
    }

    @Override
    public void onReflash(final int reflashType) {
        // TODO Auto-generated method stub
        if (reflashType == IReflashLister.PULLDOWN) {
            RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.NOTDELIVER, 1, Order.REQUESTPAGESIZE);
        } else {
            RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.NOTDELIVER, pageNumber, Order.REQUESTPAGESIZE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        index = index - 1;
        Order order = orders.get(index);
        Intent intent = new Intent();
        intent.setClass(getActivity(), OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 网络异常显示网络异常
     */
    private void netWorkError() {
        notDeliverListView.setVisibility(View.GONE);
        tip.setVisibility(View.VISIBLE);
        ((ImageView) tip.findViewById(R.id.commImg)).setImageResource(R.drawable.blank_state_no_network_icon);
        ((TextView) tip.findViewById(R.id.commText)).setText(R.string.no_net_work);
        pageNumber = 1;
    }

    /**
     * 第一页没有数据
     */
    private void noDataFirstPage() {
        notDeliverListView.setVisibility(View.GONE);
        tip.setVisibility(View.VISIBLE);
        ((ImageView) tip.findViewById(R.id.commImg)).setImageResource(R.drawable.blank_state_no_data_icon);
        ((TextView) tip.findViewById(R.id.commText)).setText(R.string.no_data);
        pageNumber = 1;
    }
}
