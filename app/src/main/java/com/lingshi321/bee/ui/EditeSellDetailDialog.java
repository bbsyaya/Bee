package com.lingshi321.bee.ui;

import java.util.ArrayList;

import com.lingshi321.bee.OrderDetailActivity;
import com.lingshi321.bee.R;
import com.lingshi321.bee.domain.Food;
import com.lingshi321.bee.domain.Order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 弹框让用户编辑销售详情
 * @author zengy
 *
 */
public class EditeSellDetailDialog extends Dialog implements android.view.View.OnClickListener{

	private Order order;      //订单信息
	private Context context;
	
	private Button confirm;
	private Button cancel;
	private LinearLayout edite_sell_items;
	
	private OrderDetailActivity orderDetailActivity;
	
	public EditeSellDetailDialog(OrderDetailActivity orderDetailActivity, Context context, Order order){
		super(context);
		this.context = context;
		this.order = order;
		this.orderDetailActivity = orderDetailActivity;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_edite_selldetail);
		initialView();
	}
	
	private void initialView(){
		edite_sell_items = (LinearLayout) findViewById(R.id.edite_sell_items);
		confirm = (Button) findViewById(R.id.confirm);
		confirm.setOnClickListener(this);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		
		LayoutInflater inflater = getLayoutInflater();
		if(order.sellDetail == null || order.sellDetail.size() <= 0){          //当无订单销售详情时，就把每种商品的销售数量设置为0
			for(int index = 0; index < order.defaultDetail.size(); index++){
				Food food = order.defaultDetail.get(index);
				//动态创建一行销售记录
				LinearLayout sell_item_content = (LinearLayout) inflater.inflate(R.layout.sell_item_content, null);
				TextView foodName = (TextView) sell_item_content.findViewById(R.id.edite_food_name);
				foodName.setText(food.foodName);
				
				EditText editText = (EditText) sell_item_content.findViewById(R.id.edite_food_number);
				editText.setId((int)food.foodId);     //设置该商品输入框为该商品的id      
				
				edite_sell_items.addView(sell_item_content);
			}
		}else{        //再次编辑销售详情，销售数量则已前一次销售数量显示
			for(int index = 0; index < order.sellDetail.size(); index++){
				Food food = order.sellDetail.get(index);
				LinearLayout sell_item_content = (LinearLayout) inflater.inflate(R.layout.sell_item_content, null);
				TextView foodName = (TextView) sell_item_content.findViewById(R.id.edite_food_name);
				foodName.setText(food.foodName);
				
				EditText editText = (EditText) sell_item_content.findViewById(R.id.edite_food_number);
				editText.setId((int)food.foodId);
				editText.setText((order.defaultDetail.get(index).foodNum-food.foodNum) + "");
				
				edite_sell_items.addView(sell_item_content);
			}
		}
	}

	/**
	 * 检测输入值是否正确，只能输入数字
	 * @return
	 */
	private boolean checkInputValue(){
		for(int index = 0; index < order.defaultDetail.size(); index++){
			Food food = order.defaultDetail.get(index);
			EditText content = (EditText) edite_sell_items.findViewById((int)food.foodId);    //通过每个商品的自增id去查找该商品的销售数量
			String contentNumber = content.getText().toString();
			int number = -1;
			try {
				number = Integer.parseInt(contentNumber);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Toast.makeText(context, contentNumber + " 输入错误!", Toast.LENGTH_LONG).show();
				//输入错误的输入框获得焦点
				content.setFocusable(true);   
				content.setFocusableInTouchMode(true);   
				content.requestFocus();  
				return false;
			}
			if(number > -1){
				if(number > order.defaultDetail.get(index).foodNum){
					Toast.makeText(context, contentNumber + " 输入错误!原因:剩余数量大于订单数量!", Toast.LENGTH_LONG).show();
					return false;
				}
				
			}else{
				Toast.makeText(context, contentNumber + " 输入错误!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.confirm:
			if(!checkInputValue()) return;          //检测用户输入的值是否合法，不合法就直接返回
			ArrayList<Food> sellDetail = new ArrayList<Food>(order.defaultDetail.size());
			for(int index = 0; index < order.defaultDetail.size(); index++){
				Food defaultFood = order.defaultDetail.get(index);
				Food sellFood = new Food();
				sellFood.barCode = defaultFood.barCode;
				sellFood.costPrice = defaultFood.costPrice;
				sellFood.foodId = defaultFood.foodId;
				sellFood.foodImgUrl = defaultFood.foodImgUrl;
				sellFood.foodName = defaultFood.foodName;
				sellFood.sellPrice = defaultFood.sellPrice;
				sellFood.foodNum = defaultFood.foodNum - Integer.parseInt(((EditText) edite_sell_items.findViewById((int)defaultFood.foodId)).getText().toString());
				
				sellDetail.add(sellFood);
			}
			order.sellDetail = sellDetail;
			float price = 0;
			for(int index = 0; index < sellDetail.size(); index++){
				Food food = sellDetail.get(index);
				price = price + food.sellPrice * food.foodNum;
			}
			order.selleMoney = price;
			orderDetailActivity.updateView();    //更新订单界面的销售详情记录
			orderDetailActivity.updatePrice();   //更新订单界面的销售价格
			dismiss();                           //关闭弹框
			break;
		case R.id.cancel:
			dismiss();                           //关闭弹框
			break;
		default:
			break;
		}
	}
	
}
