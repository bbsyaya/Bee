package com.lingshi321.bee;

import com.lingshi321.bee.ui.HandingFragment;
import com.lingshi321.bee.ui.InformationFragment;
import com.lingshi321.bee.ui.NotDeliverFragment;
import com.lingshi321.bee.ui.OrdersFragment;
import com.lingshi321.bee.util.NetworkCheck;

import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener {
	private int nowIndex = -1;
	private FragmentManager fragmentManager;

	private NotDeliverFragment notDeliverFragment;
	private HandingFragment handingFragment;
	private OrdersFragment ordersFragment;
	private InformationFragment informationFragment;

	private RelativeLayout not_deliver_rl;
	private RelativeLayout handing_rl;
	private RelativeLayout orders_rl;
	private RelativeLayout information_rl;

	private ImageView notDeliverIV;
	private ImageView handingIV;
	private ImageView ordersIV;
	private ImageView informationIV;
	
	private TextView notDeliverTV;
	private TextView handingTV;
	private TextView ordersTV;
	private TextView informationTV;
	
	public final int REQUESTCODE = 101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		NetworkCheck.setContext(getApplicationContext());
		
		fragmentManager = getSupportFragmentManager();
		not_deliver_rl = (RelativeLayout) findViewById(R.id.not_deliver_rl);
		handing_rl = (RelativeLayout) findViewById(R.id.handing_rl);
		orders_rl = (RelativeLayout) findViewById(R.id.orders_rl);
		information_rl = (RelativeLayout) findViewById(R.id.information_rl);

		not_deliver_rl.setOnClickListener(this);
		handing_rl.setOnClickListener(this);
		orders_rl.setOnClickListener(this);
		information_rl.setOnClickListener(this);

		notDeliverIV = (ImageView) findViewById(R.id.not_deliver_iv);
		handingIV = (ImageView) findViewById(R.id.handing_iv);
		ordersIV = (ImageView) findViewById(R.id.orders_iv);
		informationIV = (ImageView) findViewById(R.id.information_iv);
		
		notDeliverTV = (TextView) findViewById(R.id.not_deliver_tv);
		handingTV = (TextView) findViewById(R.id.handing_tv);
		ordersTV = (TextView) findViewById(R.id.orders_tv);
		informationTV = (TextView) findViewById(R.id.information_tv);
		
		onClick(not_deliver_rl);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.not_deliver_rl:
			if(nowIndex == NowFragementIndex.NOTDELIVERFRAGMENT){
				return;
			}
			recoveryBottomShow(nowIndex);
			setTabSelection(NowFragementIndex.NOTDELIVERFRAGMENT);
			changeBottomSelectShow(NowFragementIndex.NOTDELIVERFRAGMENT);
			
			nowIndex = NowFragementIndex.NOTDELIVERFRAGMENT;
			break;
		case R.id.handing_rl:
			if(nowIndex == NowFragementIndex.HANDINGFRAGMENT){
				return;
			}
			recoveryBottomShow(nowIndex);
			setTabSelection(NowFragementIndex.HANDINGFRAGMENT);
			changeBottomSelectShow(NowFragementIndex.HANDINGFRAGMENT);
			
			nowIndex = NowFragementIndex.HANDINGFRAGMENT;
			break;
		case R.id.orders_rl:
			if(nowIndex == NowFragementIndex.ORDERSFRAGMENT){
				return;
			}
			recoveryBottomShow(nowIndex);
			setTabSelection(NowFragementIndex.ORDERSFRAGMENT);
			changeBottomSelectShow(NowFragementIndex.ORDERSFRAGMENT);
			
			nowIndex = NowFragementIndex.ORDERSFRAGMENT;
			break;
		case R.id.information_rl:
			if(nowIndex == NowFragementIndex.INFORMATIONFRAGMENT){
				return;
			}
			recoveryBottomShow(nowIndex);
			setTabSelection(NowFragementIndex.INFORMATIONFRAGMENT);
			changeBottomSelectShow(NowFragementIndex.INFORMATIONFRAGMENT);
			
			nowIndex = NowFragementIndex.INFORMATIONFRAGMENT;
			
			break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数设置选中的Tab页
	 * 
	 * @param index
	 *            每个Tab页对应的下标，0表示未配送，1表示进行中订单，2表示所有订单，3表示个人信息
	 */
	public void setTabSelection(int index) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		switch (index) {
		case NowFragementIndex.NOTDELIVERFRAGMENT:
			if (notDeliverFragment == null) {
				notDeliverFragment = new NotDeliverFragment(this);
			}
			fragmentTransaction.replace(R.id.fragment_content,
					notDeliverFragment);
			break;
		case NowFragementIndex.HANDINGFRAGMENT:
			if (handingFragment == null) {
				handingFragment = new HandingFragment(this);
			}
			fragmentTransaction.replace(R.id.fragment_content, handingFragment);
			break;
		case NowFragementIndex.ORDERSFRAGMENT:
			if (ordersFragment == null) {
				ordersFragment = new OrdersFragment(this);
			}
			fragmentTransaction.replace(R.id.fragment_content, ordersFragment);
			break;
		case NowFragementIndex.INFORMATIONFRAGMENT:
			if (informationFragment == null) {
				informationFragment = new InformationFragment(this);
			}
			fragmentTransaction.replace(R.id.fragment_content,
					informationFragment);
			break;
		default:
			break;
		}

		fragmentTransaction.commit();
	}
	
	/**
	 * 根据传入的index参数还原底部样式
	 * @param index 每个Tab页对应的下标，0表示未配送，1表示进行中订单，2表示所有订单，3表示个人信息
	 */
	private void recoveryBottomShow(int index){
		switch (index) {
		case NowFragementIndex.NOTDELIVERFRAGMENT:
			notDeliverIV.setImageResource(R.drawable.not_deliver_unpressed);
			notDeliverTV.setTextColor(this.getResources().getColor(R.color.font_color3));
			break;
		case NowFragementIndex.HANDINGFRAGMENT:
			handingIV.setImageResource(R.drawable.handing_unpressed);
			handingTV.setTextColor(this.getResources().getColor(R.color.font_color3));
			break;
		case NowFragementIndex.ORDERSFRAGMENT:
			ordersIV.setImageResource(R.drawable.orders_unpressed);
			ordersTV.setTextColor(this.getResources().getColor(R.color.font_color3));
			break;
		case NowFragementIndex.INFORMATIONFRAGMENT:
			informationIV.setImageResource(R.drawable.information_unpressed);
			informationTV.setTextColor(this.getResources().getColor(R.color.font_color3));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 根据传入的index参数显示底部的显示内容
	 * @param index 每个Tab页对应的下标，0表示未配送，1表示进行中订单，2表示所有订单，3表示个人信息
	 */
	private void changeBottomSelectShow(int index) {
		switch (index) {
		case NowFragementIndex.NOTDELIVERFRAGMENT:
			notDeliverIV.setImageResource(R.drawable.not_deliver_pressed);
			notDeliverTV.setTextColor(this.getResources().getColor(R.color.logo_color));
			break;
		case NowFragementIndex.HANDINGFRAGMENT:
			handingIV.setImageResource(R.drawable.handing_pressed);
			handingTV.setTextColor(this.getResources().getColor(R.color.logo_color));
			break;
		case 2:
			ordersIV.setImageResource(R.drawable.orders_pressed);
			ordersTV.setTextColor(this.getResources().getColor(R.color.logo_color));
			break;
		case 3:
			informationIV.setImageResource(R.drawable.information_pressed);
			informationTV.setTextColor(this.getResources().getColor(R.color.logo_color));
			break;
		default:
			break;
		}
	}
	
	
	
	private class NowFragementIndex{
		public static final int NOTDELIVERFRAGMENT = 0;
		public static final int HANDINGFRAGMENT = 1;
		public static final int ORDERSFRAGMENT = 2;
		public static final int INFORMATIONFRAGMENT = 3;
	}
}
