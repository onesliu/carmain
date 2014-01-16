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
	
	public String deviceName;
	public String deviceMac;
	
	private PrefConfig(Context c) {
		this.c = c;
	}
	
	public static PrefConfig instance(Context c) {
		if (self == null) {
			self = new PrefConfig(c);
		}
		return self;
	}
	
	public void getCfg() {
		cfgPref = c.getSharedPreferences(PREF_CFG, 0);
		deviceName = cfgPref.getString(keyBtDevName, "");
		deviceMac = cfgPref.getString(keyBtDevMac, "");
	}
	
	public void saveBtCfg() {
		cfgPref.edit().putString(keyBtDevName, deviceName).commit();
		cfgPref.edit().putString(keyBtDevMac, deviceMac).commit();
	}

}
