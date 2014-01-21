package com.leonliu.cm.obd.Est527;

import com.leonliu.cm.obd.ObdInterface.FlowDataInteface;
import com.leonliu.cm.obd.ObdInterface.OnObdData;

public class Est527_Module implements FlowDataInteface {

	private final OnObdData onObdData;
	private StringBuffer sBuf = new StringBuffer();
	
	Est527_Module(OnObdData onData) {
		onObdData = onData;
	}
	
	@Override
	public void OnDataListener(byte[] data, int len) {
		sBuf.append(new String(data, 0, len));

		int lineEnd = sBuf.indexOf("\r\n");
		while (lineEnd != -1) {
			String line = sBuf.substring(0, lineEnd);
			if (line.length() > 0) {
				String []cols = line.split(",");
				if (cols[0].indexOf("OBD-RT") != -1) {
					
				}
			}

			sBuf.delete(0, lineEnd+2);
			lineEnd = sBuf.indexOf("\r\n");
		}

	}

	@Override
	public void StartGetData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void StopGetData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SendExpect(int type) {
		// TODO Auto-generated method stub
		
	}

	
}
