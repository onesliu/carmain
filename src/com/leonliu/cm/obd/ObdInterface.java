package com.leonliu.cm.obd;

import java.lang.reflect.Constructor;

import android.os.Handler;

public class ObdInterface {
	
	public static final int MSG_OBD_SENDFAIL = 200;
	
	public interface FlowDataInteface {
		void OnDataListener(byte []data, int len);
		void StartGetData(ObdSendAdapter out, Handler msgHandler);
		void StopGetData();
	}
	
	public interface ObdSendAdapter {
		boolean SendData(String buf);
	}
	
	public interface OnObdData {
		// Realtime Data
		void OnBat(double voltage);	//电瓶电压
		void OnRpm(int rpm);			//发动机转速
		void OnVss(int vss);			//行驶时速
		void OnTp(double tp);			//节气门开度
		void OnLod(double lod);		//发动机负荷
		void OnEct(int ect);			//冷却液温度
		void OnMpg(double mpg);		//瞬时油耗
		void OnAvm(double avm);		//平均油耗
		
		// Statistic Data
		void OnDst(double distance);	//本次行驶里程
		void OnTDst(double distance);	//总里程
		void OnFue(double liter);		//本次耗油量
		void OnTFue(double liter);		//累计耗油量
		
		// Diagnosis Data
		void OnDiagnosis(String []codes);
	}
	
	public static FlowDataInteface CreateObdModule(String model, OnObdData ObdData) {
		FlowDataInteface m = null;
		String pkg = ObdInterface.class.getPackage().getName();
		try {
			Class cls = Class.forName(pkg + "." + model + "_Module");
			Class[] paramTypes = new Class[]{OnObdData.class};
			Object[] params = new Object[]{ObdData};
			Constructor con = cls.getConstructor(paramTypes); 
			m = (FlowDataInteface)con.newInstance(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return m;
	}

}
