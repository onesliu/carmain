package com.leonliu.cm.obd.Est527;

import android.util.Log;

import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.Est527.Est527_Interfaces.ModuleHandle;

public class Est527_Statistic implements ModuleHandle {

	@Override
	public boolean OnInput(String[] colums, OnObdData onData) {
		if (colums[0].indexOf("OBD-AMT") != -1) {
			try {
				if (colums[1].indexOf("DST") != -1) {
					onData.OnDst(Double.parseDouble(colums[1].replaceAll("[^0-9\\.]", "")));
				}
				if (colums[2].indexOf("TDST") != -1) {
					onData.OnTDst(Double.parseDouble(colums[2].replaceAll("[^0-9\\.]", "")));
				}
				if (colums[3].indexOf("FUE") != -1) {
					onData.OnFue(Double.parseDouble(colums[3].replaceAll("[^0-9\\.]", "")));
				}
				if (colums[4].indexOf("TFUE") != -1) {
					onData.OnTFue(Double.parseDouble(colums[4].replaceAll("[^0-9\\.]", "")));
				}
			} catch(Exception e) {
				Log.e(this.getClass().getSimpleName(), "Parse OBD-AMT data error.");
				return false;
			}
			return true;
		}
		return false;
	}

}
