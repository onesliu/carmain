package com.leonliu.cm.obd.Est527;

import com.leonliu.cm.obd.ObdInterface.OnObdData;


public class Est527_Interfaces {

	public static final String OBD_REALTIME = "Realtime";
	public static final String OBD_STATISTIC = "Statistic";
	public static final String OBD_DIAGNOSIS = "Diagnosis";
	public static final String OBD_DRIVERHABIT = "DriverHabit";
	public static final String OBD_INFOMATION = "Infomation";
	
	public interface ModuleHandle {
		boolean OnInput(String []colums, OnObdData onData);
	}
	
	public static ModuleHandle CreateModuleHandle(String model) {
		ModuleHandle m = null;
		String pkg = Est527_Interfaces.class.getPackage().getName();
		try {
			m = (ModuleHandle) Class.forName(pkg + "." + "Est527_" + model).newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return m;
	}
	
}
