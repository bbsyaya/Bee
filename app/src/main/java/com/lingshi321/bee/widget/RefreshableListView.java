package com.lingshi321.bee.widget;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.lingshi321.bee.R;
import com.lingshi321.bee.domain.Order;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class RefreshableListView extends ListView implements OnScrollListener{
	private View header;             //顶部布局文件
	private View foot;               //底部布局文件
	private TextView tip;            //提示
	private TextView lastTime;       //最近刷新时间
//	private ProgressBar progressBar;
	
	private int headerHeight;        // 顶部布局文件的高度
	private int footHeight;          //底部布局文件的高度
	private int firstVisibleItem;    // 当前界面最顶端
	private boolean isRemark;        // 标记，当前是在ListView最顶端按下的
	private boolean isBottom;        //标记，当前是在ListView的最低端按下的
	private boolean pulldownReflashOver = true;     //下拉刷新线程是否结束
	private boolean pullupReflashOver = true;       //上拉刷新是否结束
	
	
	private int startY;              // 开始的位置
	private int scrollState;         // ListView当前滚动状态
	private int state;               // 当前的状态
	final int NONE = 0;              // 正常状态
	final int PULL = 1;              // 提示下拉状态
	final int RELESE = 2;            // 提示释放状态
	final int REFLASHING = 3;        // 刷新状态
	
	private IReflashLister reflashLister;         //刷新数据的接口
	
	public RefreshableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initeView(context);
	}
	
	public RefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initeView(context);
	}
	
	public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initeView(context);
	}
	
	private void initeView(Context context){
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		header = layoutInflater.inflate(R.layout.reflashlist_header, null);
		foot = layoutInflater.inflate(R.layout.reflashlist_foot, null);
		
		tip = (TextView)header.findViewById(R.id.tip);
		lastTime = (TextView)header.findViewById(R.id.lastupdatetime);
//		progressBar = (ProgressBar)header.findViewById(R.id.progress);
//		progressBar.setPadding(progressBar.getPaddingLeft(),
//				progressBar.getPaddingTop(),
//				progressBar.getPaddingRight() + progressBar.getWidth() + 10,
//				progressBar.getPaddingBottom());
		
		measureView(header);
		measureView(foot);
		headerHeight = header.getMeasuredHeight();
		footHeight = foot.getMeasuredHeight();
		
		Log.i("tag", headerHeight + "dddddddddd");
		topPadding(-headerHeight);

		this.addHeaderView(header, null, false);
		this.addFooterView(foot, null, false);
		this.setOnScrollListener(this);
	}
	
	/**
	 * 通知父布局，占用的宽和高
	 * @param view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}
	
	/**
	 * 设置header布局的上边距
	 * @param topPadding
	 */
	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}
	
	/**
	 * 判断移动过程中的操作
	 * 
	 * @param ev
	 */
	private void onMove(MotionEvent ev) {
		if (!isRemark) {
			return;
		}

		int tempY = (int) ev.getY();
		int space = (int) ev.getY() - startY;
		int topPadding = space - headerHeight;
		if(topPadding > headerHeight + 40){
			topPadding = headerHeight + 20;
		}
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				reflashViewByState();
			}
			break;

		case PULL:
			topPadding(topPadding);
			if (space > headerHeight + 20
					&& scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				state = RELESE;
				reflashViewByState();
			}
			break;

		case RELESE:
			topPadding(topPadding);
			if (space < headerHeight + 30) {
				state = PULL;
				reflashViewByState();
			} else if (space <= 0) {
				state = NONE;
				isRemark = false;
				reflashViewByState();
			}
			break;

		case REFLASHING:
			break;

		default:
			break;
		}
	}
	
	/**
	 * 更具当前状态，改变界面显示
	 */
	private void reflashViewByState() {
		
		switch (state) {
		case NONE:
			topPadding(-headerHeight);
			break;
		case PULL:
//			progressBar.setVisibility(View.GONE);
			tip.setText(getResources().getString(R.string.pull));
			break;
		case RELESE:
			tip.setText(getResources().getString(R.string.relese));
			break;
		case REFLASHING:
//			progressBar.setVisibility(View.VISIBLE);
			tip.setText(getResources().getString(R.string.loading));
			topPadding(5);
			pulldownReflashOver = false;
			pullupReflashOver = true;
			reflashLister.onReflash(IReflashLister.PULLDOWN);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(firstVisibleItem == 0){
				isRemark = true;
				startY = (int)ev.getY();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			onMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			if (state == RELESE) {
				state = REFLASHING;
			} else if (state == PULL) {
				state = NONE;
				isRemark = false;
			}
			reflashViewByState();
			break;
		default:
			break;
		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.firstVisibleItem = firstVisibleItem;
		//判断追加数据, 只有当下拉刷新完成，上一次的上拉刷新完成，并且是最后一个时，才从新请求数据
		if (pulldownReflashOver && pullupReflashOver && (firstVisibleItem + visibleItemCount == totalItemCount) && (totalItemCount > 0)) {
			isBottom = true;
			pullupReflashOver = false;
			reflashLister.onReflash(IReflashLister.PULLUP);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		// TODO Auto-generated method stub
		this.scrollState = scrollState;
	}
	
	/**
	 * 拉去完成数据
	 * @param reflashType 1表示下拉刷新完成，0表示底部加载更多完成
	 */
	public void reflashComplete(int reflashType) {
		if(IReflashLister.PULLDOWN == reflashType){
			state = NONE;
			isRemark = false;
			reflashViewByState();
			SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
			
			Date date = new Date(System.currentTimeMillis());
			String time = format.format(date);
			lastTime.setText(time);
			
			pulldownReflashOver = true;           //下拉刷新完成
		}else{
			pullupReflashOver = true;             //上拉刷新完成
		}
		
	}
	
	public void setListViewPullDownReflash(){
		state = REFLASHING;
		reflashViewByState();
	}
	public void setInterface(IReflashLister reflashLister){
		this.reflashLister = reflashLister;
	}
	
	public interface IReflashLister{
		public final int PULLDOWN = 1;    //下拉刷新
		public final int PULLUP = 2;      //上拉刷新
		/**
		 * 加载更多数据
		 * @param reflashType 1表示下拉刷新，0表示底部加载更多
		 */
		public void onReflash(int reflashType);
	}
	
}
