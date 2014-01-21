package com.leonliu.cm.obd.Est527;

import com.leonliu.cm.obd.ObdInterface.FlowDataInteface;
import com.leonliu.cm.obd.ObdInterface.OnRealtimeData;

public class Est527_Realtime implements FlowDataInteface {

	private final OnRealtimeData onRealtimeData;
	
	Est527_Realtime(OnRealtimeData onRealtime) {
		onRealtimeData = onRealtime;
	}
	
	@Override
	public void OnDataListener(byte[] data, int len) {
		String sBuf = new String(data, 0, len);
		int lineEnd = sBuf.indexOf("\r\n");

	}

	@Override
	public void StartGetData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StopGetData() {
		// TODO Auto-generated method stub
		
	}

	
}
