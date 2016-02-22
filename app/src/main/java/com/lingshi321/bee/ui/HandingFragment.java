package com.lingshi321.bee.ui;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lingshi321.bee.LoginActivity;
import com.lingshi321.bee.MainActivity;
import com.lingshi321.bee.OrderDetailActivity;
import com.lingshi321.bee.R;
import com.lingshi321.bee.adapter.OrderAdapter;
import com.lingshi321.bee.domain.Food;
import com.lingshi321.bee.domain.Order;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.RequestDataURLUtil;
import com.lingshi321.bee.widget.RefreshableListView;
import com.lingshi321.bee.widget.RefreshableListView.IReflashLister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class HandingFragment extends Fragment implements IReflashLister, OnClickListener, OnItemClickListener {

    private MainActivity mainActivity;
    private RelativeLayout main;
    private RelativeLayout content;
    private RelativeLayout tip;                       //网络错误或者没有数据的提示

    private ArrayList<Order> deliveringOrder;
    private ArrayList<Order> deliveredOrder;
    private ArrayList<Order> payedOrder;

    private OrderAdapter deliveringAdapter;
    private OrderAdapter deliveredAdapter;
    private OrderAdapter payedAdapter;

    private RefreshableListView deliveringOrderRL;
    private RefreshableListView deliveredOrderRL;
    private RefreshableListView payedOrderRL;

    private RelativeLayout handing_delivering_title;
    private RelativeLayout handing_delivered_title;
    private RelativeLayout handing_payed_title;

    private TextView handing_delivering_title_TV;
    private TextView handing_delivered_title_TV;
    private TextView handing_payed_title_TV;

    private int deliveringPage = 1;
    private int deliveredPage = 1;
    private int payedPage = 1;

    private View deliveringFoot;
    private View deliveredFoot;
    private View payedFoot;

    private int orderStatus = -1;                  //表示界面显示的是那种类型的订单

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                case Order.DELIVERING:
                    ArrayList<Order> backDatas = (ArrayList<Order>) msg.obj;
                    if (msg.arg1 == 1 && backDatas.size() == 0) {
                        noDataFirstPage();
                        return;
                    } else if (msg.arg1 == 1 && backDatas.size() > 0) {                //arg1表示是第几页，1表示第一页
                        deliveringOrderRL.setVisibility(View.VISIBLE);
                        tip.setVisibility(View.GONE);
                        deliveringPage = 2;
                        deliveringOrder.clear();
                        deliveringOrder.addAll(0, backDatas);
                        deliveringAdapter.notifyDataSetChanged();
                        deliveringOrderRL.reflashComplete(IReflashLister.PULLDOWN);
                        deliveringFoot.setVisibility(View.VISIBLE);
                    } else if (backDatas.size() > 0) {                            //往数据后面追加
                        deliveringPage++;
                        deliveringOrder.addAll(deliveringOrder.size(), backDatas);
                        deliveringAdapter.notifyDataSetChanged();
                        deliveringOrderRL.reflashComplete(IReflashLister.PULLUP);
                    } else if (backDatas.size() == 0) {
                        deliveringFoot.setVisibility(View.GONE);
                        if(getActivity()!=null)
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    break;
                case Order.DELIVERED:
                    ArrayList<Order> backDatas1 = (ArrayList<Order>) msg.obj;
                    if (msg.arg1 == 1 && backDatas1.size() == 0) {
                        noDataFirstPage();
                        return;
                    } else if (msg.arg1 == 1 && backDatas1.size() > 0) {                //arg1表示是第几页，1表示第一页
                        deliveredOrderRL.setVisibility(View.VISIBLE);
                        tip.setVisibility(View.GONE);
                        deliveredPage = 2;
                        deliveredOrder.clear();
                        deliveredOrder.addAll(0, backDatas1);
                        deliveredAdapter.notifyDataSetChanged();
                        deliveredOrderRL.reflashComplete(IReflashLister.PULLDOWN);
                        deliveredFoot.setVisibility(View.VISIBLE);
                    } else if (backDatas1.size() > 0) {                            //往数据后面追加
                        deliveredPage++;
                        deliveredOrder.addAll(deliveredOrder.size(), backDatas1);
                        deliveredAdapter.notifyDataSetChanged();
                        deliveredOrderRL.reflashComplete(IReflashLister.PULLUP);
                    } else if (backDatas1.size() == 0) {
                        deliveredFoot.setVisibility(View.GONE);
                        Toast.makeText(getActivity().getApplicationContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    break;
                case Order.PAYED:
                    ArrayList<Order> backDatas2 = (ArrayList<Order>) msg.obj;
                    if (msg.arg1 == 1 && backDatas2.size() == 0) {
                        noDataFirstPage();
                        return;
                    } else if (msg.arg1 == 1 && backDatas2.size() > 0) {                //arg1表示是第几页，1表示第一页
                        payedOrderRL.setVisibility(View.VISIBLE);
                        tip.setVisibility(View.GONE);
                        payedPage = 2;
                        payedOrder.clear();
                        payedOrder.addAll(0, backDatas2);
                        payedAdapter.notifyDataSetChanged();
                        payedOrderRL.reflashComplete(IReflashLister.PULLDOWN);
                        payedFoot.setVisibility(View.VISIBLE);
                    } else if (backDatas2.size() > 0) {                            //往数据后面追加
                        payedPage++;
                        payedOrder.addAll(payedOrder.size(), backDatas2);
                        payedAdapter.notifyDataSetChanged();
                        deliveredOrderRL.reflashComplete(IReflashLister.PULLUP);
                    } else if (backDatas2.size() == 0) {
                        payedFoot.setVisibility(View.GONE);
                        Toast.makeText(getActivity().getApplicationContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public HandingFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        main = (RelativeLayout) inflater.inflate(R.layout.fragment_handing, container, false);
        initialView();

        orderStatus = -1;
        onClick(handing_delivering_title);

        return main;
    }


    private void initialView() {
        content = (RelativeLayout) main.findViewById(R.id.content);

        deliveringOrder = new ArrayList<Order>();
        deliveredOrder = new ArrayList<Order>();
        payedOrder = new ArrayList<Order>();

        deliveringAdapter = new OrderAdapter(deliveringOrder, getActivity());
        deliveringOrderRL = (RefreshableListView) content.findViewById(R.id.deliveringOrderRL);
        deliveringFoot = deliveringOrderRL.findViewById(R.id.foot_progress);
        deliveringFoot.setVisibility(View.INVISIBLE);
        deliveringOrderRL.setAdapter(deliveringAdapter);
        deliveringOrderRL.setInterface(this);

        deliveredAdapter = new OrderAdapter(deliveredOrder, getActivity());
        deliveredOrderRL = (RefreshableListView) content.findViewById(R.id.deliveredOrderRL);
        deliveredFoot = deliveredOrderRL.findViewById(R.id.foot_progress);
        deliveredFoot.setVisibility(View.INVISIBLE);
        deliveredOrderRL.setAdapter(deliveredAdapter);
        deliveredOrderRL.setInterface(this);

        payedAdapter = new OrderAdapter(payedOrder, getActivity());
        payedOrderRL = (RefreshableListView) content.findViewById(R.id.payedOrderRL);
        payedFoot = payedOrderRL.findViewById(R.id.foot_progress);
        payedFoot.setVisibility(View.INVISIBLE);
        payedOrderRL.setAdapter(payedAdapter);
        payedOrderRL.setInterface(this);

        handing_delivering_title = (RelativeLayout) main.findViewById(R.id.handing_delivering_title);
        handing_delivered_title = (RelativeLayout) main.findViewById(R.id.handing_delivered_title);
        handing_payed_title = (RelativeLayout) main.findViewById(R.id.handing_payed_title);

        handing_delivering_title_TV = (TextView) main.findViewById(R.id.handing_delivering_title_TV);
        handing_delivered_title_TV = (TextView) main.findViewById(R.id.handing_delivered_title_TV);
        handing_payed_title_TV = (TextView) main.findViewById(R.id.handing_payed_title_TV);

        handing_delivering_title.setOnClickListener(this);
        handing_delivered_title.setOnClickListener(this);
        handing_payed_title.setOnClickListener(this);

        deliveringOrderRL.setOnItemClickListener(this);
        deliveredOrderRL.setOnItemClickListener(this);
        payedOrderRL.setOnItemClickListener(this);

        tip = (RelativeLayout) main.findViewById(R.id.request_result_tip);
        tip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                switch (orderStatus) {
                    case Order.DELIVERING:
                        ((TextView) tip.findViewById(R.id.commText)).setText(R.string.loading_data);
                        RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.DELIVERING, 1, Order.REQUESTPAGESIZE);
                        break;
                    case Order.DELIVERED:
                        ((TextView) tip.findViewById(R.id.commText)).setText(R.string.loading_data);
                        RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.DELIVERED, 1, Order.REQUESTPAGESIZE);
                        break;
                    case Order.PAYED:
                        ((TextView) tip.findViewById(R.id.commText)).setText(R.string.loading_data);
                        RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.PAYED, 1, Order.REQUESTPAGESIZE);
                        break;
                    default:
                        break;
                }

            }
        });

    }

    @Override
    public void onReflash(final int reflashType) {
        // TODO Auto-generated method stub

        if (reflashType == IReflashLister.PULLDOWN) {
            reflashDownOrder(orderStatus);
        } else {
            reflashUpOrder(orderStatus);
        }
    }

    /**
     * 下拉刷新
     *
     * @param orderStatus
     */
    private void reflashDownOrder(int orderStatus) {
        switch (orderStatus) {
            case Order.DELIVERING:
                RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.DELIVERING, 1, Order.REQUESTPAGESIZE);
                break;
            case Order.DELIVERED:
                RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.DELIVERED, 1, Order.REQUESTPAGESIZE);
                break;
            case Order.PAYED:
                RequestDataURLUtil.getOrdersURL(handler, getActivity(), Order.PAYED, 1, Order.REQUESTPAGESIZE);
                break;
            default:
                break;
        }
    }

    /**
     * 加载到最后，加载更多
     *
     * @param orderStatus
     */
    private void reflashUpOrder(int orderStatus) {
        switch (orderStatus) {
            case Order.DELIVERING:
                RequestDataURLUtil.getOrdersURL(handler,getActivity(), Order.DELIVERING, deliveringPage, Order.REQUESTPAGESIZE);
                break;
            case Order.DELIVERED:
                RequestDataURLUtil.getOrdersURL(handler,getActivity(), Order.DELIVERED, deliveredPage, Order.REQUESTPAGESIZE);
                break;
            case Order.PAYED:
                RequestDataURLUtil.getOrdersURL(handler,getActivity(), Order.PAYED, payedPage, Order.REQUESTPAGESIZE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.handing_delivering_title:
                if (orderStatus == Order.DELIVERING) {        //表示当前显示的界面就是配送中的订单界面，就不做处理
                    return;
                } else {
                    recoveryTitleColorAndHideList(orderStatus);
                    orderStatus = Order.DELIVERING;
                    setTitleColorAndShowList(orderStatus);
                    deliveringOrderRL.setListViewPullDownReflash();
                }
                break;
            case R.id.handing_delivered_title:
                if (orderStatus == Order.DELIVERED) {        //表示当前显示的界面就是配送完成的订单界面，就不做处理
                    return;
                } else {
                    recoveryTitleColorAndHideList(orderStatus);
                    orderStatus = Order.DELIVERED;
                    setTitleColorAndShowList(orderStatus);
                    deliveredOrderRL.setListViewPullDownReflash();
                }
                break;
            case R.id.handing_payed_title:
                if (orderStatus == Order.PAYED) {        //表示当前显示的界面就是配送完成的订单界面，就不做处理
                    return;
                } else {
                    recoveryTitleColorAndHideList(orderStatus);
                    orderStatus = Order.PAYED;
                    setTitleColorAndShowList(orderStatus);
                    payedOrderRL.setListViewPullDownReflash();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 还原title的字体颜色
     *
     * @param orderStatus
     */
    private void recoveryTitleColorAndHideList(int orderStatus) {
        switch (orderStatus) {
            case Order.DELIVERING:
                handing_delivering_title_TV.setTextColor(this.getResources().getColor(R.color.font_color3));
                deliveringOrderRL.setVisibility(View.GONE);
                break;
            case Order.DELIVERED:
                handing_delivered_title_TV.setTextColor(this.getResources().getColor(R.color.font_color3));
                deliveredOrderRL.setVisibility(View.GONE);
                break;
            case Order.PAYED:
                handing_payed_title_TV.setTextColor(this.getResources().getColor(R.color.font_color3));
                payedOrderRL.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 设置title的字体颜色
     *
     * @param orderStatus
     */
    private void setTitleColorAndShowList(int orderStatus) {
        switch (orderStatus) {
            case Order.DELIVERING:
                handing_delivering_title_TV.setTextColor(this.getResources().getColor(R.color.logo_color));
                deliveringOrderRL.setVisibility(View.VISIBLE);
                break;
            case Order.DELIVERED:
                handing_delivered_title_TV.setTextColor(this.getResources().getColor(R.color.logo_color));
                deliveredOrderRL.setVisibility(View.VISIBLE);
                break;
            case Order.PAYED:
                handing_payed_title_TV.setTextColor(this.getResources().getColor(R.color.logo_color));
                payedOrderRL.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
        // TODO Auto-generated method stub
        index = index - 1;
        Order order = null;
        switch (orderStatus) {
            case Order.DELIVERING:
                order = deliveringOrder.get(index);
                break;
            case Order.DELIVERED:
                order = deliveredOrder.get(index);
                break;
            case Order.PAYED:
                order = payedOrder.get(index);
                break;
            default:
                break;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);          //打开订单详情界面的时候，可能需要对订单进行编辑，所以用的是forresult
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (orderStatus) {
            case Order.DELIVERING:
                deliveringOrderRL.setListViewPullDownReflash();
                break;
            case Order.DELIVERED:
                deliveredOrderRL.setListViewPullDownReflash();
                break;
            default:
                break;
        }
    }

    /**
     * 网络异常显示网络异常
     */
    private void netWorkError() {
        switch (orderStatus) {
            case Order.DELIVERING:
                deliveringOrderRL.setVisibility(View.GONE);
                deliveringPage = 1;
                break;
            case Order.DELIVERED:
                deliveredOrderRL.setVisibility(View.GONE);
                deliveredPage = 1;
                break;
            case Order.PAYED:
                payedOrderRL.setVisibility(View.GONE);
                payedPage = 1;
                break;
            default:
                break;
        }

        tip.setVisibility(View.VISIBLE);
        ((ImageView) tip.findViewById(R.id.commImg)).setImageResource(R.drawable.blank_state_no_network_icon);
        ((TextView) tip.findViewById(R.id.commText)).setText(R.string.no_net_work);
    }

    /**
     * 第一页没有数据
     */
    private void noDataFirstPage() {
        switch (orderStatus) {
            case Order.DELIVERING:
                deliveringOrderRL.setVisibility(View.GONE);
                deliveringPage = 1;
                break;
            case Order.DELIVERED:
                deliveredOrderRL.setVisibility(View.GONE);
                deliveredPage = 1;
                break;
            case Order.PAYED:
                payedOrderRL.setVisibility(View.GONE);
                payedPage = 1;
                break;
            default:
                break;
        }

        tip.setVisibility(View.VISIBLE);
        ((ImageView) tip.findViewById(R.id.commImg)).setImageResource(R.drawable.blank_state_no_data_icon);
        ((TextView) tip.findViewById(R.id.commText)).setText(R.string.no_data);
    }
}
