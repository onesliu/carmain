package com.leonliu.cm.obd;


public class ObdOutput {

	// output interface reference
	protected ObdRealtimeData OnRealtimeData = null;

	public ObdRealtimeData getOnRealtimeData() {
		return OnRealtimeData;
	}

	public void setOnRealtimeData(ObdRealtimeData onRealtimeData) {
		OnRealtimeData = onRealtimeData;
	}

}
