package com.wutong.taxiapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.RegexUtils;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_passenger.R;

public class RegisterStartActivity extends BaseActivity {

	private EditText edit_mobileNum;
	private ImageButton but_next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registerstart);
	}

	@Override
	protected void initView() {
		edit_mobileNum = (EditText) findViewById(R.id.edit_mobileNum);
		but_next = (ImageButton) findViewById(R.id.but_next);
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void setListener() {

		but_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String num = edit_mobileNum.getText().toString();

				boolean checkMobile = RegexUtils.checkMobile(num);

				if (checkMobile) {// 手机号码正确

					IApplication.getInstance().sharedConfig.setMobile(num);

					ActivityUtils.startActivityAndFinish(mContext,
							RegisterEndActivity.class);

				} else {// 不正确

					ToastUtils.toast(mContext, "请填写正确的手机号码");

				}
			}
		});

	}

}
