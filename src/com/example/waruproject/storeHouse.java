package com.example.waruproject;

import java.util.Date;

public class storeHouse {

	Date tmpst;

	public Date getTmpst() {
		return tmpst;
	}

	public void setTmpst(Date tmpst) {
		this.tmpst = tmpst;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	int strength;
	String ssid;

	double lat;

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLongd() {
		return longd;
	}

	public void setLongd(double longd) {
		this.longd = longd;
	}

	public double getBatterVal() {
		return batterVal;
	}

	public void setBatterVal(double d) {
		this.batterVal = d;
	}

	double longd;
	double batterVal;

}
