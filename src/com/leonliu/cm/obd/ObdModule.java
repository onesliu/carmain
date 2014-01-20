package com.leonliu.cm.obd;


public class ObdModule {

	private ObdModule() {}
	private static ObdModule self = null;
	
	public static ObdModule instance() {
		if (self == null) {
			self = new ObdModule();
		}
		return self;
	}
	
	// input interface reference
	protected ObdInterface.FlowDataInteface RealtimeData = null;
	
	public ObdInterface.FlowDataInteface getRealtimeData() {
		return RealtimeData;
	}

	public void setRealtimeData(ObdInterface.FlowDataInteface realtimeData) {
		RealtimeData = realtimeData;
	}
	
	protected ObdInterface.AckDataInterface DiagnosisData = null;

	public ObdInterface.AckDataInterface getDiagnosisData() {
		return DiagnosisData;
	}

	public void setDiagnosisData(ObdInterface.AckDataInterface diagnosisData) {
		DiagnosisData = diagnosisData;
	}

	// output interface reference
	protected ObdInterface.OnRealtimeData OnRealtimeData = null;

	public ObdInterface.OnRealtimeData getOnRealtimeData() {
		return OnRealtimeData;
	}

	public void setOnRealtimeData(ObdInterface.OnRealtimeData onRealtimeData) {
		OnRealtimeData = onRealtimeData;
	}

}
