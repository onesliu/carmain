package com.leonliu.cm.obd.Elm327;

import android.os.Handler;

import com.leonliu.cm.obd.ObdInterface.FlowDataInteface;
import com.leonliu.cm.obd.ObdInterface.ObdSendAdapter;
import com.leonliu.cm.obd.ObdInterface.OnObdData;

public class Elm327_Module implements FlowDataInteface {

	private final OnObdData onObdData;
	private ObdSendAdapter out = null;
	private Handler msgHandler;
	private boolean bStart = false;
	
	public Elm327_Module(OnObdData onData) {
		onObdData = onData;
	}
	
	@Override
	public void OnDataListener(byte[] data, int len) {
		// TODO Auto-generated method stub

	}

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		if (bStart == false) return;
		
		obdSendHandler.postDelayed(obdSend, 500);
	}

	@Override
	public void StopGetData() {
		obdSendHandler.removeCallbacks(obdSend);
	}
	
	private Handler obdSendHandler = new Handler();
	private Runnable obdSend = new Runnable() {
		@Override
		public void run() {
			obdSendHandler.postDelayed(this, 500);
		}
	};
	
	private void InitModule() {
		
	}
}
