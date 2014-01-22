package com.leonliu.cm.obd.Est527;

import android.util.Log;

import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.Est527.Est527_Interfaces.ModuleHandle;

public class Est527_Realtime implements ModuleHandle {

	@Override
	public boolean OnInput(String[] colums, OnObdData onData) {
		if (colums[0].indexOf("OBD-RT") != -1) {
			try {
				for (String col : colums) {
					col = col.trim();
					if (col.matches("^BAT=.+")) {
						onData.OnBat(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")));
					}
					else if (col.matches("^RPM=.+")) {
						onData.OnRpm(Integer.parseInt(col.replaceAll("[^0-9]", "")));
					}
					else if (col.matches("^VSS=.+")) {
						onData.OnVss(Integer.parseInt(col.replaceAll("[^0-9]", "")));
					}
					else if (col.matches("^TP=.+")) {
						onData.OnTp(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")));
					}
					else if (col.matches("^LOD=.+")) {
						onData.OnLod(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")));
					}
					else if (col.matches("^ECT=.+")) {
						onData.OnEct(Integer.parseInt(col.replaceAll("[^0-9]", "")));
					}
					else if (col.matches("^MPG=.+")) {
						onData.OnMpg(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")) - 0.001);
					}
					else if (col.matches("^AVM=.+")) {
						onData.OnAvm(Double.parseDouble(col.replaceAll("[^0-9\\.]", "")) - 0.001);
					}
				}
			} catch(Exception e) {
				Log.e(this.getClass().getSimpleName(), "Parse OBD-RT data error.");
				return false;
			}
		}
		else
			return false;
		return true;
	}

}
