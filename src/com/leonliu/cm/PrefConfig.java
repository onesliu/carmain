package com.leonliu.cm;

import android.content.Context;
import android.content.SharedPreferences;

public final class PrefConfig {

	private static PrefConfig self = null;
	
	private final Context c;
	private String PREF_CFG = "config";
	private SharedPreferences cfgPref;
	
	private final String keyBtDevName = "BluetoothDeviceName";
	private final String keyBtDevMac = "BluetoothDeviceMac";
	
	private PrefConfig(Context c) {
		this.c = c;
	}
	
	public static PrefConfig instance(Context c) {
		if (self == null) {
			self = new PrefConfig(c);
		}
		return self;
	}
	
	public synchronized void getCfg() {
		cfgPref = c.getSharedPreferences(PREF_CFG, 0);
		setDeviceName(cfgPref.getString(keyBtDevName, ""));
		setDeviceMac(cfgPref.getString(keyBtDevMac, ""));
	}
	
	public synchronized void saveBtCfg() {
		cfgPref.edit().putString(keyBtDevName, getDeviceName()).commit();
		cfgPref.edit().putString(keyBtDevMac, getDeviceMac()).commit();
	}
	
	// Dao properties
	private String deviceName;
	private String deviceMac;
	
	public synchronized String getDeviceName() {
		return deviceName;
	}

	public synchronized void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public synchronized String getDeviceMac() {
		return deviceMac;
	}

	public synchronized void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

}
