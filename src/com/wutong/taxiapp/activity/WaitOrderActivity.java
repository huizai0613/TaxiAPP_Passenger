package com.wutong.taxiapp.activity;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.domain.ResponseTaxiNum;
import com.wutong.taxiapp_passenger.R;

public class WaitOrderActivity extends BaseActivity {

	private TextView waittaxi_tv;
	private ProgressBar progressBar;
	private View wait_but;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			int type = msg.what;

			switch (type) {
			case 1:
				int progress = progressBar.getProgress();
				progressBar.setProgress(++(progress));
				
				handler.sendEmptyMessageDelayed(1, 1000);
				break;
			}

		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_waitorder);

	}

	@Override
	protected void initView() {

		initTitle(true, "等待司机抢单", false, null);

		waittaxi_tv = (TextView) findViewById(R.id.waittaxi_tv);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		wait_but = findViewById(R.id.wait_but);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

		Intent intent = getIntent();

		ResponseTaxiNum mResponseTaxiNum = (ResponseTaxiNum) intent
				.getSerializableExtra("RESPONSETAXINUM");

		waittaxi_tv.setText("已发送给" + mResponseTaxiNum.getTotalTaxi() + "辆车");

		handler.sendEmptyMessage(1);

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void acceptResult(JSONObject jsonObject) {

		lib.parserWiteOrder(jsonObject);

	}

}
