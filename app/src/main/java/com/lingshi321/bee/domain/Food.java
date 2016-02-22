package com.lingshi321.bee.domain;

import java.io.Serializable;

public class Food implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public long foodId;                   //零食自增id
	public String barCode;                //零食条形码
	public String foodName;               //零食名称
	public int foodNum;                   //数量
	public String foodImgUrl;             //零食图片路径
	public float costPrice;               //成本价
	public float sellPrice;               //销售价格
}
