package com.lingshi321.bee.util;

public class DataInterfaceConstants {
	public static final int VERSION_ID = 1;
	public static final String HOST_OPENFIRE = "211.155.92.139";
	public static final int PORT_OPENFIRE = 5222;
	public static final String HOST = "http://www.lingshi321.com:8888/"; // 大服务器
//	 public static final String HOST = "http://113.251.171.173:8080/";
	// //王文祥服务器
//	public static final String HOST = "http://113.251.174.166:9000/"; // 范梦林服务器
//	 public static final String HOST = "http://113.251.165.147:9000/";
	// 请求的URL
//	 public static final String HOST = "http://192.168.191.1:9000/";
	public static final String LOGINURL = HOST + "userManage/staffLogin";
	public static final String ORDERSURL = HOST
			+ "orderManage/getAllOrderByPhone";

	public static final String LOGOUT = HOST + "logOut";public static final String USERINFO = HOST + "GETUSERINFO";
	public static final String UPDATEORDERSTATUS = HOST + "UPDATEORDERS";
	public static final String CHANGEDELIVERING = HOST
			+ "orderManage/updateOrderByPhone";
	public static final String CHANGEDELIVERINGPAY = HOST
			+ "orderManage/order_balance_update";
	public static final String CHECKVERSION = HOST
			+ "apk/check?software_version=";
	public static final String GETORGANIZATION = HOST
			+ "organizationManage/listdormitory";
	public static final String BOUNDORDER = HOST + "orderManage/boundorder";

	// 公共参数
	public static final String VERSION = "version";
	public static final String USERID = "user_id";
	public static final String SESSION = "session_id";

	// 登陆信息
	public static final String PHONE = "telephone";
	public static final String PASSWORD = "password";

	// 获取订单信息
	public static final String ORDERID = "order_id";
	public static final String STATUS = "order_status";
	public static final String PAGENUMBER = "page";
	public static final String PAGESIZE = "pageSize";

	// 修改订单信息
	public static final String ORDER_ID = "order_id";
	public static final String ORDER_STATUS = "order_status";
	public static final String SNACKS_ID = "snacks_id";
	public static final String SNACKS_NUM = "snacks_num";
	public static final String FOODS = "foods";
	public static final String SELL_ITEMS = "sell_items";

	// 用户保存信息
	public static final String USERNAME = "user_name";
	public static final String USERPHONE = "user_phone";
	public static final String USEREMAIL = "user_email";
	public static final String USERJOINTIME = "user_join_time";
	public static final String USERMANAGERPART = "user_manager_part";
	public static final String STAFF_ID = "staff_id";

	public static final String DORMITORY_ID = "dormitory_id";
	
	public static final int REQUEST_PAGE_NUMBER = 1;
}