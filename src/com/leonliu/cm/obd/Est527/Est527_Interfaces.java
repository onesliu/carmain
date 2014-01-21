package com.leonliu.cm.obd.Est527;

public class Est527_Interfaces {

	public static final int OBD_REALTIME = 1;
	public static final int OBD_STATISTIC = 2;
	public static final int OBD_DIAGNOSIS = 3;
	public static final int OBD_DRIVERHABIT = 4;
	public static final int OBD_INFOMATION = 5;
	
	public interface ModuleHandle {
		void OnInput(String []colums);
	}
	
	public ModuleHandle CreateModuleHandle(int type) {
		switch(type) {
		case OBD_REALTIME:
			break;
		case OBD_STATISTIC:
			break;
		}
		
		return null;
	}
}
