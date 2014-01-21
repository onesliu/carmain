package com.leonliu.cm.obd;

public interface ObdInterface {
	
	public interface FlowDataInteface {
		void OnDataListener(byte []data, int len);
		void StartGetData();
		void StopGetData();
	}
	
	public interface Diagnosis {
		String[] GetDiagnosis();
	}
	
	public interface OnRealtimeData {
		void OnBat(double voltage);	//电瓶电压
		void OnRpm(int rpm);			//发动机转速
		void OnVss(int vss);			//行驶时速
		void OnTp(double tp);			//节气门开度
		void OnLod(double lod);		//发动机负荷
		void OnEct(int ect);			//冷却液温度
		void OnMpg(double mpg);		//瞬时油耗
		void OnAvm(double avm);		//平均油耗
	}
	
	public interface CreateObdModule {
		FlowDataInteface CreateRealtime(OnRealtimeData onRealtime);
		Diagnosis CreateDiagnosis();
	}
}
