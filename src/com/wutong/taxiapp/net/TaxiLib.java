package com.wutong.taxiapp.net;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.domain.Province;
import com.wutong.taxiapp.domain.RequestCallTaxi;
import com.wutong.taxiapp.domain.ResponseTaxiNum;
import com.wutong.taxiapp.domain.ResponseVersion;
import com.wutong.taxiapp.net.service.BackService;

public class TaxiLib {
	private static TaxiLib mLib;
	private TaxiRequest mRequest;
	private TaxiParse mParse;
	private Context mContext;
	private LocalBroadcastManager mLocalBroadcastManager;
	private MessageBackReciver reciver;
	private IntentFilter mIntentFilter;
	private LoadingDialog mLoadingDialog;

	public TaxiLib(Context context, ImBaseSocketNet baseSocketNet) {
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BackService.HEART_BEAT_ACTION);
		mIntentFilter.addAction(BackService.NET_BAD_ACTION);
		mIntentFilter.addAction(BackService.MESSAGE_ACTION);
		this.mContext = context;
		mLoadingDialog = new LoadingDialog(mContext);
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
		mRequest = new TaxiRequest(context);
		mParse = new TaxiParse(context);
		if (baseSocketNet != null) {
			reciver = new MessageBackReciver(baseSocketNet);
		}
	}

	public TaxiLib(Context context) {
		mRequest = new TaxiRequest(context);
		mParse = new TaxiParse(context);
	}

	public List<Province> getProvinceInfo() throws JSONException,
			NetRequestException, IOException {

		String json = mRequest.getProvinceInfoRequest();

		return mParse.parseProvinceInfo(json);
	}

	private void parserCom() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	private void requestCom() {
		parserCom();
		mLoadingDialog.show();
	}

	// 注册接受者
	public void registerReciver() {
		mLocalBroadcastManager.registerReceiver(reciver, mIntentFilter);
	}

	// 注销接受者
	public void unRegisterReciver() {
		mLocalBroadcastManager.unregisterReceiver(reciver);
	}

	// 取消绑定
	public void unbindService() {
		unRegisterReciver();
		mRequest.unbindService();
	}

	// 请求版本
	public void requestVersion() throws RemoteException {

		mRequest.requestVersion();
	}

	// 解析版本
	public ResponseVersion parserVersion(JSONObject jsonObject)
			throws NetRequestException {

		return mParse.parserVersion(jsonObject);

	}

	// 请求出租车数量与坐标
	public void requestTaxiNum(double lat, double lon, String address)
			throws RemoteException {

		requestCom();

		mRequest.requestTaxiNum(lat, lon, address);

	}

	// 解析出租车数量与坐标
	public ResponseTaxiNum parserTaxiNum(JSONObject jsonObject)
			throws NetRequestException {

		parserCom();

		return mParse.parserTaxiNum(jsonObject);

	}

	public void requestCallTaxi(RequestCallTaxi callTaxi) throws RemoteException {

		mRequest.requestCallTaxi(callTaxi);
	}

	public void requestTextTaxi(RequestCallTaxi callTaxi) throws RemoteException {
		// TODO Auto-generated method stub
		mRequest.requestTextTaxi(callTaxi);
	}

	//解析等待接待的数据
	public void parserWiteOrder(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
		 mParse.parserWiteOrder(jsonObject);
		
	}

	// // 提交留言
	// public BackMsg submitFeed(String leaveType, String fb_pId, String
	// fb_content)
	// throws HttpRequestException, JSONException, NetRequestException {
	//
	// String json = mRequest.submitFeedRequest(LEAVEMSG, leaveType, fb_pId,
	// fb_content);
	//
	// return mParse.parseBackMsg(json);
	//
	// }

}
