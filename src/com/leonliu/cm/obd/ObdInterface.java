package com.leonliu.cm.obd;

public class ObdInterface {
	
	public interface FlowDataInteface {
		void OnDataListener(byte []data, int len);
		void StartGetData();
		void StopGetData();
		void SendExpect(int type);
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
		
		// Diagnosis Data
		void OnDiagnosis(String []codes);
	}
	
	public static FlowDataInteface CreateObdModule(String model) {
		FlowDataInteface m = null;
		String pkg = ObdInterface.class.getPackage().getName();
		try {
			m = (FlowDataInteface) Class.forName(pkg + "." + model + "_Module").newInstance();
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
