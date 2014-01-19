package com.leonliu.cm.obd.est527;

import com.leonliu.cm.obd.ObdInterface;
import com.leonliu.cm.obd.ObdOutput;

public class Est527 extends ObdOutput implements ObdInterface {

	@Override
	public void InputDataFlow(byte[] buf, int len) {
		String sBuf = new String(buf, 0, len);
		int lineEnd = sBuf.indexOf("\r\n");

	}

	@Override
	public void RequireRealtime() {
	}

	@Override
	public void RequireStatistic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RequireDriverHabit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RequireDiagnostic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RequireInfo() {
		// TODO Auto-generated method stub
		
	}

}
