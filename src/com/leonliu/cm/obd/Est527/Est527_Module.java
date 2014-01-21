package com.leonliu.cm.obd.Est527;

import android.os.Handler;

import com.leonliu.cm.obd.ObdInterface;
import com.leonliu.cm.obd.ObdInterface.FlowDataInteface;
import com.leonliu.cm.obd.ObdInterface.ObdSendAdapter;
import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.Est527.Est527_Interfaces.ModuleHandle;

public class Est527_Module implements FlowDataInteface {

	private final OnObdData onObdData;
	private StringBuffer sBuf = new StringBuffer();
	private ObdSendAdapter out = null;
	
	Est527_Module(OnObdData onData) {
		onObdData = onData;
	}
	
	@Override
	public void OnDataListener(byte[] data, int len) {
		sBuf.append(new String(data, 0, len));

		ModuleHandle handle = null;
		int lineEnd = sBuf.indexOf("\r\n");
		while (lineEnd != -1) {
			String line = sBuf.substring(0, lineEnd);
			if (line.length() > 0) {
				String []cols = line.split(",");
				if (cols[0].indexOf("OBD-RT") != -1) {
					handle = Est527_Interfaces.CreateModuleHandle(Est527_Interfaces.OBD_REALTIME);
				}
				else if (cols[0].indexOf("OBD-AMT") != -1) {
					handle = Est527_Interfaces.CreateModuleHandle(Est527_Interfaces.OBD_STATISTIC);
				}
				if (handle != null)
					handle.OnInput(cols, onObdData);
			}

			sBuf.delete(0, lineEnd+2);
			lineEnd = sBuf.indexOf("\r\n");
		}

	}

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		boolean ret = true;
		this.out = out;
		if (out != null) {
			ret = out.SendData("ATSON\r\n");
		}
		if (ret == false) {
			msgHandler.obtainMessage(ObdInterface.MSG_OBD_SENDFAIL).sendToTarget();
		}
	}

	@Override
	public void StopGetData() {
		if (out != null) {
			out.SendData("ATSOFF\r\n");
			out = null;
		}
	}
	
}