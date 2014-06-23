package com.wutong.taxiapp.activity;

import java.io.Serializable;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.iss.exception.NetRequestException;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.domain.MyLocation;
import com.wutong.taxiapp.domain.RequestCallTaxi;
import com.wutong.taxiapp.domain.ResponseTaxiNum;
import com.wutong.taxiapp.net.TaxiLib;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.ErrorInfo;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_passenger.R;

public class TaxiMainActivity extends SlidingFragmentActivity implements
		OnClickListener, ImBaseSocketNet {

	private Activity mContext;
	private TextView taxi_main_car_num_tv;
	private RadioGroup sexy_rd;
	private TextView taxi_main_go_tv;
	private TextView taxi_main_to_tv;
	private TextView taxi_main_car_num_tv2;
	private EditText taxi_price_et;
	private Button go_speak_view;
	private Button submit;
	private TextView above_content;
	private ImageView above_margin;
	private TaxiLib lib;

	// 起点和重点位置
	private MyLocation mStartLocation;
	private MyLocation mEndLocation;

	// 地图定位
	private double longitude;// 经度
	private double latitude;// 纬度
	private MyLocationListenner myListener;// 位置监听
	private LocationClient mLocClient; // 定位客户端
	private BDLocation startLocation;
	private ResponseTaxiNum parserTaxiNum;
	private boolean isPublicInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		IApplication.getInstance().addActivity(this);
		initSlidingMenu();
		setContentView(R.layout.activity_main);
		initLocation();
		initControl();
		initView();
		setListener();
		initData();
		lib = new TaxiLib(mContext, this);
	}

	// 定位初始化
	private void initLocation() {
		myListener = new MyLocationListenner();
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		// mLocClient.requestLocation();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			startLocation = location;

			longitude = location.getLongitude();// 获取经度
			latitude = location.getLatitude(); // 获取纬度

			String addrStr = location.getAddrStr();// 详细地址信息
			mStartLocation = new MyLocation(MyLocation.STARTADDRESS, addrStr,
					addrStr, longitude, latitude, location.getCity());

			taxi_main_go_tv.setText(addrStr);
			taxi_main_go_tv.setClickable(true);

			try {
				lib.requestTaxiNum(latitude, longitude, addrStr);
			} catch (RemoteException e) {
				ToastUtils.toast(mContext, ErrorInfo.ERRORNET);
				e.printStackTrace();
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	// 初始化侧边栏
	private void initSlidingMenu() {

		setBehindContentView(R.layout.behind_slidingmenu);// 设置侧边栏布局
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		sm.setShadowWidthRes(R.dimen.shadow_width);// 侧边栏阴影宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 屏幕宽度-侧边栏滑出宽度
		// sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// sm.setShadowDrawable(R.drawable.slidingmenu_shadow);
		// sm.setShadowWidth(20);
		sm.setBehindScrollScale(0);
		// 设置滑动时菜单的是否淡入淡出
		sm.setFadeEnabled(true);
		// 设置淡入淡出的比例
		sm.setFadeDegree(0.4f);
		// 设置滑动时拖拽效果
		sm.setBehindScrollScale(0.4f);
	}

	// 得到侧边栏控制
	private void initControl() {
		// TODO Auto-generated method stub

	}

	protected void initView() {

		above_content = (TextView) findViewById(R.id.above_content);
		above_margin = (ImageView) findViewById(R.id.above_margin);
		taxi_main_car_num_tv = (TextView) findViewById(R.id.taxi_main_car_num_tv);
		sexy_rd = (RadioGroup) findViewById(R.id.sexy_rd);
		RadioButton sexy_all = (RadioButton) findViewById(R.id.sexy_all);
		taxi_main_go_tv = (TextView) findViewById(R.id.taxi_main_go_tv);
		taxi_main_to_tv = (TextView) findViewById(R.id.taxi_main_to_tv);
		taxi_main_car_num_tv2 = (TextView) findViewById(R.id.taxi_main_car_num_tv);
		taxi_price_et = (EditText) findViewById(R.id.taxi_price_et);
		go_speak_view = (Button) findViewById(R.id.go_speak_view);
		submit = (Button) findViewById(R.id.submit);

		taxi_main_go_tv.setClickable(false);
		taxi_main_go_tv.setText("正在获取您的位置......");
		above_content.setText("现在用车");
		sexy_all.setChecked(true);

	}

	protected void initData() {

	}

	protected void setListener() {

		above_margin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TaxiMainActivity.this.showMenu();
			}
		});

		taxi_main_go_tv.setOnClickListener(this);
		taxi_main_to_tv.setOnClickListener(this);
		go_speak_view.setOnClickListener(this);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.taxi_main_go_tv) {
			ActivityUtils.startActivityForResult(
					mContext,
					TaxiMapActivity.class,
					"LOCATION",
					new MyLocation(MyLocation.STARTADDRESS, startLocation
							.getStreet(), startLocation.getAddrStr(),
							startLocation.getLongitude(), startLocation
									.getLatitude(), startLocation.getCity(),
							parserTaxiNum.getTaxis()), MyLocation.STARTADDRESS,
					TaxiMainActivity.this);

		} else if (id == R.id.taxi_main_to_tv) {

			ActivityUtils.startActivityForResult(
					mContext,
					TaxiMapActivity.class,
					"LOCATION",
					new MyLocation(MyLocation.EndADDRESS, startLocation
							.getStreet(), startLocation.getAddrStr(),
							startLocation.getLongitude(), startLocation
									.getLatitude(), startLocation.getCity(),
							parserTaxiNum.getTaxis()), MyLocation.EndADDRESS,
					TaxiMainActivity.this);

		} else if (id == R.id.submit) {// 直接叫车 根据 位置

			if (mStartLocation == null) {

				ToastUtils.toast(mContext, "起始位置为空！！请点击选择");

			} else if (mEndLocation == null) {
				ToastUtils.toast(mContext, "终点位置为空！！请点击选择");

			} else {
				// 发布打车信息

				String price = taxi_price_et.getText().toString();

				int sexId = sexy_rd.getCheckedRadioButtonId();

				String sexy = "";
				switch (sexId) {
				case R.id.sexy_female:

					sexy = "女";

					break;
				case R.id.sexy_male:
					sexy = "男";

					break;

				default:

					sexy = "";

					break;

				}

				RequestCallTaxi callTaxi = new RequestCallTaxi();

				callTaxi.setDesAddress(mEndLocation.getAddress_address());
				callTaxi.setDesLat(mEndLocation.getLatitude() + "");
				callTaxi.setDesLong(mEndLocation.getLongitude() + "");
				callTaxi.setFlag(" ");
				callTaxi.setPhone(IApplication.getInstance().sharedConfig
						.getMobile());
				callTaxi.setPrice(price);
				callTaxi.setSex(sexy);
				callTaxi.setSourceAddress(mStartLocation.getAddress_address());
				callTaxi.setSourceLat(mStartLocation.getLatitude() + "");
				callTaxi.setSourceLong(mStartLocation.getLongitude() + "");
				callTaxi.setType("0");

				try {
					lib.requestTextTaxi(callTaxi);

					isPublicInfo = true;

				} catch (RemoteException e) {
					// TODO Auto-generated catch block

					ToastUtils.toast(mContext, ErrorInfo.ERRORNET);

					e.printStackTrace();
				}

			}

		} else if (id == R.id.go_speak_view) {

			ActivityUtils.startActivityForSerializable(
					mContext,
					TaxiRecordActivity.class,
					new MyLocation(MyLocation.EndADDRESS, startLocation
							.getStreet(), startLocation.getAddrStr(),
							startLocation.getLongitude(), startLocation
									.getLatitude(), startLocation.getCity(),
							parserTaxiNum.getTaxis()), "LOCATION",
					TaxiMainActivity.this);

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		lib.unbindService();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		Serializable serializableExtra = arg2.getSerializableExtra("LOCATION");
		if (serializableExtra != null
				&& serializableExtra instanceof MyLocation) {
			MyLocation mMyLocation = (MyLocation) serializableExtra;

			if (arg1 == MyLocation.STARTADDRESS) {// 返回起点

				mStartLocation = mMyLocation;

				taxi_main_go_tv.setText(mMyLocation.getAddress_address());

			} else if (arg1 == MyLocation.EndADDRESS) {// 返回终点

				mEndLocation = mMyLocation;

				taxi_main_to_tv.setText(mMyLocation.getAddress_address());
			}
		}
	}

	@Override
	public void acceptResult(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			parserTaxiNum = lib.parserTaxiNum(jsonObject);

			if (isPublicInfo) {
				// 发布信息成功之后，跳转到等待接单界面
				ActivityUtils.startActivityForSerializable(mContext,
						WaitOrderActivity.class, parserTaxiNum,
						"RESPONSETAXINUM", mContext);
			} else {

				taxi_main_car_num_tv2.setText(parserTaxiNum.getTotalTaxi());
			}

		} catch (NetRequestException e) {
			// TODO Auto-generated catch block
			e.getError().print(mContext);
		}
	}

	@Override
	public void onBackPressed() {

		IApplication.getInstance().shutDown(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		lib.unRegisterReciver();
	}

	@Override
	protected void onResume() {
		super.onResume();

		isPublicInfo = false;

		// if (mLocClient != null) {
		// mLocClient.start();
		// }
		lib.registerReciver();
	}

}
