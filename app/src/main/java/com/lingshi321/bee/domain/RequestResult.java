package com.lingshi321.bee.domain;

public class RequestResult {
	public static final int NORMAL = 1;                     //正常
	public static final int REQUESTERROR = -100;            //请求出现错误
	public static final int NODATA = -90;                   //在第一次加载时就没有数据
	public static final int NONETWORK = -80;                //网络异常，无法请求数据
	public static final int SESSIONOVERTIME = -70;          //session过期
	public static final int STOPTHREAD = 2;                 //正常关闭这次请求
}
