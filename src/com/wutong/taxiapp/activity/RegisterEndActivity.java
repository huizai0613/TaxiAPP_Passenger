package com.wutong.taxiapp.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iss.exception.NetRequestException;
import com.iss.view.common.ToastAlone;
import com.iss.view.wheel.OnWheelChangedListener;
import com.iss.view.wheel.WheelView;
import com.wutong.taxiapp.MainActivity;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.adapter.WheelCityAdapter;
import com.wutong.taxiapp.adapter.WheelDistrictAdapter;
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.domain.City;
import com.wutong.taxiapp.domain.Province;
import com.wutong.taxiapp.net.TaxiAsyncTask;
import com.wutong.taxiapp.net.TaxiLib;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.SharedConfig;
import com.wutong.taxiapp_passenger.R;

public class RegisterEndActivity extends BaseActivity implements
		ImBaseSocketNet {

	protected static final String TAG = "RegisterEndActivity";
	private WheelView wheel_province;
	private WheelView wheel_city;
	private TextView registerEnd_ok;
	private TextView but_back;
	private WheelDistrictAdapter adapter;
	private WheelCityAdapter adapter1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registerend);
	}

	@Override
	protected void initView() {

		wheel_province = (WheelView) findViewById(R.id.wheel_province);
		wheel_province.IsFirst(true);
		wheel_province.setScaleItem(true);
		wheel_city = (WheelView) findViewById(R.id.wheel_city);
		wheel_city.IsFirst(false);
		wheel_city.setScaleItem(true);
		registerEnd_ok = (TextView) findViewById(R.id.registerEnd_ok);
		but_back = (TextView) findViewById(R.id.but_back);

	}

	@Override
	protected void initData() {
		new GetDataTask(mContext).execute();
	}

	class GetDataTask extends TaxiAsyncTask<String, String, List<Province>> {

		public GetDataTask(Activity activity) {
			super(activity);

			mTaxiLib = new TaxiLib(mContext);
		}

		@Override
		protected List<Province> doInBackground(String... params) {
			super.doInBackground(params);
			List<Province> info = null;
			try {
				info = mTaxiLib.getProvinceInfo();
			} catch (IOException e) {
				exception = "网络错误";
			} catch (JSONException e) {
				exception = "解析出错";
			} catch (NetRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return info;
		}

		@Override
		protected void onPostExecute(List<Province> result) {
			super.onPostExecute(result);
			if (exception != null) {
				ToastAlone.showToast(getApplicationContext(), exception,
						Toast.LENGTH_SHORT).show();
				return;
			}

			setDate(result);
		}

	}

	@Override
	protected void setListener() {
		registerEnd_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 提交参数
				Province bean = adapter.getBean(wheel_province.getCurrentItem());

				City bean2 = adapter1.getBean(wheel_city.getCurrentItem());

				MyLogger.i(TAG, bean.privinceName + bean2.getCityName());

				IApplication.getInstance().sharedConfig.setString(
						SharedConfig.ADDRESS,
						bean.privinceName + bean2.getCityName());

				IApplication.getInstance().sharedConfig.setBoolean(
						SharedConfig.ISREGISTER, true);

				ActivityUtils.startActivityAndFinish(mContext,
						TaxiMainActivity.class);

			}
		});
		but_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityUtils.finish(mContext);
			}

		});
		wheel_province.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				if (oldValue != newValue) {
					Province areaBean = adapter.getBean(wheel.getCurrentItem());
					ArrayList<City> stationList = areaBean.citys;
					adapter1 = new WheelCityAdapter(mContext, stationList);
					wheel_city.setViewAdapter(adapter1);
					wheel_city.setCurrentItem(0);
				}
			}
		});
	}

	public void setDate(List<Province> result) {

		adapter = new WheelDistrictAdapter(mContext,
				(ArrayList<Province>) result);
		wheel_province.setViewAdapter(adapter);
		wheel_province.setCurrentItem(0);

		adapter1 = new WheelCityAdapter(mContext, result.get(0).citys);
		wheel_city.setViewAdapter(adapter1);
		wheel_city.setCurrentItem(0);

	}

	@Override
	public void acceptResult(JSONObject jsonObject) {

	}

	@Override
	public void onBackPressed() {

		ActivityUtils.startActivityAndFinish(mContext,
				RegisterStartActivity.class);

	}

}
