package com.lingshi321.bee.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public long orderId;
	public String orderQueryId;
	public String phone;
	public String school;
	public String building;
	public String dormitory;
	public String createTime;
	public String sendTime;
	public float costMoney;
	public float defaultMoney;
	public float selleMoney;
	public String note;
	public int status;
	
	public ArrayList<Food> defaultDetail;
	public ArrayList<Food> sellDetail;
	
	public static final int ALLORDERS = -1;       //所有订单
	public static final int NOTDELIVER = 10;      //未配送
	public static final int DELIVERING = 20;      //配送中
	public static final int DELIVERED = 30;       //完成配送
	public static final int PAYED = 40;           //已结算
//	public static final int BACKING = 50;         //未返回入库
	public static final int OVER = 50;            //完成
	
	public static final int REQUESTPAGESIZE = 5; //每页请求数量
}
