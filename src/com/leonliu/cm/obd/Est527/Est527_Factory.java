package com.leonliu.cm.obd.Est527;

import com.leonliu.cm.obd.ObdInterface.CreateObdModule;
import com.leonliu.cm.obd.ObdInterface.Diagnosis;
import com.leonliu.cm.obd.ObdInterface.FlowDataInteface;
import com.leonliu.cm.obd.ObdInterface.OnRealtimeData;

public class Est527_Factory implements CreateObdModule {

	@Override
	public FlowDataInteface CreateRealtime(OnRealtimeData onRealtime) {
		return new Est527_Realtime(onRealtime);
	}

	@Override
	public Diagnosis CreateDiagnosis() {
		return new Est527_Diagnosis();
	}

}
