package com.leonliu.cm;

public class MyInterface {
	
	public interface OnReadDataListner {
		void onReading(byte[] buffer, int len);
	}
}
