package com.leonliu.cm.obd.est527;

import com.leonliu.cm.obd.ObdInterface;

public class Est527_Realtime implements ObdInterface.FlowDataInteface {

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
