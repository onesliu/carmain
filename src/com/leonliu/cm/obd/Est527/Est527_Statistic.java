package com.leonliu.cm.obd.Est527;

import android.util.Log;

import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.Est527.Est527_Interfaces.ModuleHandle;

public class Est527_Statistic implements ModuleHandle {

	@Override
	public boolean OnInput(String[] colums, OnObdData onData) {
		if (colums[0].indexOf("OBD-AMT") != -1) {
			try {
				for (String col : colums) {
					col = col.trim();
					if (col.matches("^TDST=.+")) {
						onData.OnTDst(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")));
					}
					else if (col.matches("^DST=.+")) {
						onData.OnDst(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")));
					}
					else if (col.matches("^TFUE=.+")) {
						onData.OnTFue(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")));
					}
					else if (col.matches("^FUE=.+")) {
						onData.OnFue(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")));
					}
				}
			} catch(Exception e) {
				Log.e(this.getClass().getSimpleName(), "Parse OBD-AMT data error.");
				return false;
			}
		}
		else
			return false;
		return true;
	}

}
