package com.leonliu.cm.obd.Est527;

import android.util.Log;

import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.Est527.Est527_Interfaces.ModuleHandle;

public class Est527_Realtime implements ModuleHandle {

	@Override
	public boolean OnInput(String[] colums, OnObdData onData) {
		if (colums[0].indexOf("OBD-RT") != -1) {
			try {
				if (colums[1].indexOf("BAT") != -1) {
					onData.OnBat(Double.parseDouble(colums[1].replaceAll("[^0-9\\.]", "")));
				}
				if (colums[2].indexOf("RPM") != -1) {
					onData.OnRpm(Integer.parseInt(colums[2].replaceAll("[^0-9]", "")));
				}
				if (colums[3].indexOf("VSS") != -1) {
					onData.OnVss(Integer.parseInt(colums[3].replaceAll("[^0-9]", "")));
				}
				if (colums[4].indexOf("TP") != -1) {
					onData.OnTp(Double.parseDouble(colums[4].replaceAll("[^0-9\\.]", "")));
				}
				if (colums[5].indexOf("LOD") != -1) {
					onData.OnLod(Double.parseDouble(colums[5].replaceAll("[^0-9\\.]", "")));
				}
				if (colums[6].indexOf("ECT") != -1) {
					onData.OnEct(Integer.parseInt(colums[6].replaceAll("[^0-9]", "")));
				}
				if (colums[7].indexOf("MPG") != -1) {
					onData.OnMpg(Double.parseDouble(colums[7].replaceAll("[^0-9\\.]", "")) - 0.001);
				}
				if (colums[8].indexOf("AVM") != -1) {
					onData.OnAvm(Double.parseDouble(colums[8].replaceAll("[^0-9\\.]", "")) - 0.001);
				}
			} catch(Exception e) {
				Log.e(this.getClass().getSimpleName(), "Parse OBD-RT data error.");
				return false;
			}
			return true;
		}
		return false;
	}

}
