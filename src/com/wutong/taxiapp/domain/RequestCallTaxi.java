package com.wutong.taxiapp.domain;

import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.iss.exception.NetRequestException;
import com.wutong.taxiapp.base.BaseDomain;

public class RequestCallTaxi extends BaseDomain<RequestCallTaxi> {

	private int request = 3;
	private String type;
	private String sourceAddress;
	private String sourceLat;
	private String sourceLong;
	private String desAddress;
	private String desLat;
	private String desLong;
	private String sex;
	private String price;
	private String phone;
	private String sourceSound;
	private String flag;
	
	

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	private int getRequest() {
		return request;
	}

	private void setRequest(int request) {
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public String getSourceLat() {
		return sourceLat;
	}

	public void setSourceLat(String sourceLat) {
		this.sourceLat = sourceLat;
	}

	public String getSourceLong() {
		return sourceLong;
	}

	public void setSourceLong(String sourceLong) {
		this.sourceLong = sourceLong;
	}

	public String getDesAddress() {
		return desAddress;
	}

	public void setDesAddress(String desAddress) {
		this.desAddress = desAddress;
	}

	public String getDesLat() {
		return desLat;
	}

	public void setDesLat(String desLat) {
		this.desLat = desLat;
	}

	public String getDesLong() {
		return desLong;
	}

	public void setDesLong(String desLong) {
		this.desLong = desLong;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSourceSound() {
		return sourceSound;
	}

	public void setSourceSound(String sourceSound) {
		this.sourceSound = sourceSound;
	}

	@Override
	public String toJsonString() {
		Gson gson = new Gson();

		return gson.toJson(this);
	}

	@Override
	public RequestCallTaxi parseJSON(JSONObject jsonObj)
			throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List parseArrayJSON(JSONObject jsonObj) throws NetRequestException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestCallTaxi cursorToBean(Cursor cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues beanToValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
