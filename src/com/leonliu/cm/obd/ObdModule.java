package com.leonliu.cm.obd;

import android.os.Handler;

import com.leonliu.cm.obd.ObdInterface.FlowDataInteface;
import com.leonliu.cm.obd.ObdInterface.ObdSendAdapter;
import com.leonliu.cm.obd.ObdInterface.OnObdData;

public class ObdModule implements FlowDataInteface {

	protected final OnObdData onObdData;
	protected ObdSendAdapter out = null;
	protected Handler msgHandler;
	protected boolean bStart = false;
	
	public ObdModule(OnObdData onData) {
		onObdData = onData;
	}

	@Override
	public void OnDataListener(byte[] data, int len) {
		// TODO Auto-generated method stub

	}

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void StopGetData() {
		// TODO Auto-generated method stub

	}

}
