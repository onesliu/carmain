package com.leonliu.cm;

import com.leonliu.cm.obd.ObdDao;

public class MyInterface {
	
	public interface OnReadDataListner {
		void onReading(byte[] buf, int len);
		void onReading(ObdDao data);
	}
	
	public interface OnProgressBarShow {
		void ShowProgressBar(boolean show);
	}
}
