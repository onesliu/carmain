package com.leonliu.cm.obd;

public interface ObdInterface {

	void InputDataFlow(byte []buf, int len);
	void RequireRealtime();
	void RequireStatistic();
	void RequireDriverHabit();
	void RequireDiagnostic();
	void RequireInfo();
}
