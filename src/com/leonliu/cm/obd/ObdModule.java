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
	protected StringBuffer sBuf;
	
	public ObdModule(OnObdData onData) {
		onObdData = onData;
	}

	@Override
	public void OnDataListener(byte[] data, int len) {
	}

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		bStart = true;
		this.out = out;
		this.msgHandler = msgHandler;
		sBuf = new StringBuffer();
	}

	@Override
	public void StopGetData() {
		out = null;
		msgHandler = null;
		bStart = false;
		sBuf = null;
	}

}
