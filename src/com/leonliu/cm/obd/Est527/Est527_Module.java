package com.leonliu.cm.obd.Est527;

import android.os.Handler;

import com.leonliu.cm.obd.ObdInterface;
import com.leonliu.cm.obd.ObdInterface.ObdSendAdapter;
import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.ObdModule;
import com.leonliu.cm.obd.Est527.Est527_Interfaces.ModuleHandle;

public class Est527_Module extends ObdModule {

	private StringBuffer sBuf = new StringBuffer();
	
	public Est527_Module(OnObdData onData) {
		super(onData);
	}

	private void parseLine(ModuleHandle handle, String []cols) {
		if (handle.OnInput(cols, onObdData) == false) {
			msgHandler.obtainMessage(ObdInterface.MSG_OBD_PARSEFAIL).sendToTarget();
		}
		else {
			msgHandler.obtainMessage(ObdInterface.MSG_OBD_READ).sendToTarget();
		}
	}
	
	@Override
	public void OnDataListener(byte[] data, int len) {
		
		if (bStart == false) return;
		
		sBuf.append(new String(data, 0, len));

		ModuleHandle rhandle = Est527_Interfaces.CreateModuleHandle(Est527_Interfaces.OBD_REALTIME);
		ModuleHandle shandle = Est527_Interfaces.CreateModuleHandle(Est527_Interfaces.OBD_STATISTIC);
		
		int lineEnd = sBuf.indexOf("\r\n");
		while (lineEnd != -1) {
			String line = sBuf.substring(0, lineEnd);
			if (line.length() > 0) {
				String []cols = line.split(",");
				if (cols[0].indexOf("OBD-RT") != -1) {
					parseLine(rhandle, cols);
				}
				else if (cols[0].indexOf("OBD-AMT") != -1) {
					parseLine(shandle, cols);
				}
			}

			sBuf.delete(0, lineEnd+2);
			lineEnd = sBuf.indexOf("\r\n");
		}

	}

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		super.StartGetData(out, msgHandler);
		boolean ret = true;
		if (out != null) {
			ret = out.SendData("ATSON\r\n".getBytes());
		}
		if (ret == false) {
			msgHandler.obtainMessage(ObdInterface.MSG_OBD_SENDFAIL).sendToTarget();
		}
	}

	@Override
	public void StopGetData() {
		if (out != null) {
			out.SendData("ATSOFF\r\n".getBytes());
			out = null;
		}
		super.StopGetData();
	}
	
}
