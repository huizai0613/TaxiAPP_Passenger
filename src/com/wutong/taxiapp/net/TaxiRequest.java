package com.wutong.taxiapp.net;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Base64;

import com.iss.utils.StringUtil;
import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.base.MyLogger;
import com.wutong.taxiapp.domain.RequestCallTaxi;
import com.wutong.taxiapp.domain.RequestTaxiNum;
import com.wutong.taxiapp.domain.RequestVersion;
import com.wutong.taxiapp.net.service.IBackService;

public class TaxiRequest {

	public static final String URL = "http://www.zhihuiapp.com:7000/Api/?";// 正式接口
	public static final String IMAGEURL = "http://img.zhihuiapp.com:7001";// 正式接口

	protected Context mContext;

	private MessageBackReciver reciver;
	private IBackService iBackService;

	public TaxiRequest(Context context) {

		mContext = context.getApplicationContext();

	}

	public void unbindService() {
		IApplication.getInstance().unbindService();
	}

	public String getProvinceInfoRequest() throws IOException {
		return StringUtil.inputToString(mContext.getAssets().open("city.json"),
				"utf-8");
	}

	private void addShareParamsAndOpenLoding() {
		if (iBackService == null) {
			iBackService = IApplication.getInstance().getiBackService();
		}

	}

	public void requestVersion() throws RemoteException {
		addShareParamsAndOpenLoding();
		iBackService.sendMessage(new RequestVersion().toJsonString());
	}

	public void requestTaxiNum(double lat, double lon, String address)
			throws RemoteException {
		addShareParamsAndOpenLoding();
		String jsonString = new RequestTaxiNum(lat + "", lon + "", address)
				.toJsonString();
		MyLogger.i("send", jsonString);
		iBackService.sendMessage(jsonString);
	}

	public void requestCallTaxi(RequestCallTaxi callTaxi)
			throws RemoteException {

		addShareParamsAndOpenLoding();
		String jsonString = callTaxi.toJsonString();
		MyLogger.i("send", jsonString);

		try {
			FileOutputStream writer = new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/WifiChat/12345.amr", true);

			writer.write(Base64.decode(callTaxi.getSourceSound(),
					Base64.DEFAULT));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		iBackService.sendMessage(jsonString);

	}

	public void requestTextTaxi(RequestCallTaxi callTaxi) throws RemoteException {
		// TODO Auto-generated method stub
		addShareParamsAndOpenLoding();
		String jsonString = callTaxi.toJsonString();
		MyLogger.i("send", jsonString);

		iBackService.sendMessage(jsonString);
	}

}
