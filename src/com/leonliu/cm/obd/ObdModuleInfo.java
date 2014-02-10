package com.leonliu.cm.obd;

import java.util.regex.Pattern;

import android.os.Handler;

import com.leonliu.cm.obd.ObdInterface.ObdSendAdapter;
import com.leonliu.cm.obd.ObdInterface.OnObdData;

public class ObdModuleInfo extends ObdModule {

	private Object synclock = new Object();

	public ObdModuleInfo(OnObdData onData) {
		super(onData);
	}
	
	String []Modules = new String[] {"Est527", "Elm327"};
	Pattern []patterns = new Pattern[2];
	private void MakePattern() {
		for (int i = 0; i < Modules.length; i++) {
			patterns[i] = Pattern.compile("(?i)" + Modules[i]);
		}
	}

	@Override
	public void OnDataListener(byte[] data, int len) {
		if (bStart == false) return;
		
		sBuf.append(new String(data, 0, len));
		String str = sBuf.substring(0);
		if (str.length() > 0) {
			MakePattern();
			int i = 0;
			for (i = 0; i < patterns.length; i++) {
				if (patterns[i].matcher(str).find()) {
					stop = true;
					notifySend();
					onObdData.OnModuleName(Modules[i]);
					msgHandler.obtainMessage(ObdInterface.MSG_OBD_INFO).sendToTarget();
				}
			}
			if (i >= patterns.length)
				notifySend();
		}
	}

	private void notifySend() {
		synchronized (synclock) {
			synclock.notifyAll();
		}
	}
	

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		super.StartGetData(out, msgHandler);
		stop = false;
		count = 0;
		new Thread(obdSend, "OBD_ModuleInfo_Thread").start();
	}
	
	@Override
	public void StopGetData() {
		stop = true;
		notifySend();
		super.StopGetData();
	}

	private static final int timeout = 10000;
	private boolean stop = false;
	private int count = 0;
	private Runnable obdSend = new Runnable() {
		@Override
		public void run() {
			while(stop == false) {
				out.SendData("ATI" + "\r\n");
				try {
					long now = System.currentTimeMillis();
					synchronized (synclock) {
						synclock.wait(timeout);
					}
					if (System.currentTimeMillis() - now >= timeout)
						stop = true;
					Thread.sleep(2000);
					count++;
					if (count >= 3)
						stop = true;
				} catch (InterruptedException e) {
				}
			}
		}
	};
}
