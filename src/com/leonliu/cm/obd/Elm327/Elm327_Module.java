package com.leonliu.cm.obd.Elm327;

import android.os.Handler;

import com.leonliu.cm.obd.ObdInterface.ObdSendAdapter;
import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.ObdInterface;
import com.leonliu.cm.obd.ObdModule;

public class Elm327_Module extends ObdModule {

	private Object synclock;
	
	public Elm327_Module(OnObdData onData) {
		super(onData);
	}
	
	@Override
	public void OnDataListener(byte[] data, int len) {
		if (bStart == false) return;

		synclock.notify();
	}

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		/*super.StartGetData(out, msgHandler);
		isInited = false;
		obdSendHandler.post(obdSend);*/
	}

	@Override
	public void StopGetData() {
		/*obdSendHandler.removeCallbacks(obdSend);
		super.StopGetData();*/
	}
	
	private boolean isInited = false;
	private Handler obdSendHandler = new Handler();
	private Runnable obdSend = new Runnable() {
		@Override
		public void run() {
			if (!isInited) {
				InitModule();
				isInited = true;
			}
			SendAllRequest();
			obdSendHandler.postDelayed(this, 1000);
		}
	};

	private void SendSyncData(byte[] buf) {
		if (!bStart) return;
		out.SendData(buf);
		try {
			synclock.wait(500);
		} catch (InterruptedException e) {
		}
	}
	
	private void InitModule() {
		SendSyncData("ATZ\r\n".getBytes());
		SendSyncData("ATWS\r\n".getBytes());
		SendSyncData("ATE0\r\n".getBytes());
		SendSyncData("ATL0\r\n".getBytes());
		SendSyncData("ATH1\r\n".getBytes());
		SendSyncData("ATSP0\r\n".getBytes());
		byte cmd[]={0x01,0x00};
		SendSyncData(cmd);
	}
	
	private void SendAllRequest() {
		byte cmd[] = {0x01,0x05};
		SendSyncData(cmd);
		cmd[1] = 0x06;
		SendSyncData(cmd);
	}
}
