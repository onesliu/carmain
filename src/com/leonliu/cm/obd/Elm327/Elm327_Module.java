package com.leonliu.cm.obd.Elm327;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;

import com.leonliu.cm.obd.ObdInterface;
import com.leonliu.cm.obd.ObdInterface.ObdSendAdapter;
import com.leonliu.cm.obd.ObdInterface.OnObdData;
import com.leonliu.cm.obd.ObdModule;

public class Elm327_Module extends ObdModule {

	private Object synclock = new Object();
	private Map<String, String> obdCmds;
	private String retRegex;
	private String cmd;
	private Elm327_Parser parser;
	
	public Elm327_Module(OnObdData onData) {
		super(onData);
		InitObdCmds();
		parser = new Elm327_Parser(onObdData);
	}

	private void InitObdCmds() {
		obdCmds = new TreeMap<String,String>();
		String hex2 = "([0-9a-fA-F]{2})";
		String hex4 = "([0-9a-fA-F]{4})";
		obdCmds.put("ATZ", "[ \t]*");
		//obdCmds.put("ATE0", "^ATE0.*");
		obdCmds.put("ATL0", "^OK.*");
		obdCmds.put("ATSP0", "^OK.*");
		obdCmds.put("0104", "4104"+hex2+"$"); //发动机负荷
		obdCmds.put("0105", "4105"+hex2+"$"); //冷却液温度
		obdCmds.put("010A", "410A"+hex2+"$"); //燃油管路压力
		obdCmds.put("010B", "410B"+hex2+"$"); //进气岐管压力 MAP
		obdCmds.put("010C", "410C"+hex4+"$"); //发动机转速
		obdCmds.put("010D", "410D"+hex2+"$"); //行驶时速
		obdCmds.put("010E", "410E"+hex2+"$"); //点火提前角
		obdCmds.put("010F", "410F"+hex2+"$"); //进气温度
		obdCmds.put("0110", "4110"+hex4+"$"); //进气速度 MAF
		obdCmds.put("0111", "4111"+hex2+"$"); //节气门开度
		obdCmds.put("011F", "411F"+hex4+"$"); //引擎启动时间
		obdCmds.put("0121", "4121"+hex4+"$"); //故障灯亮起后行驶的距离
		obdCmds.put("0122", "4122"+hex4+"$"); //油路相对进气岐管压力 FRP
		obdCmds.put("0123", "4123"+hex4+"$"); //油路压力 FRP
		obdCmds.put("012F", "412F"+hex2+"$"); //燃油液位输入
		obdCmds.put("0130", "4130"+hex2+"$"); //故障码清除后的启动次数
		obdCmds.put("0131", "4131"+hex4+"$"); //清除故障码后行驶的距离
		obdCmds.put("0132", "4132"+hex4+"$"); //燃油蒸发系统压力
		obdCmds.put("0133", "4133"+hex2+"$"); //大气压力 BARO
		obdCmds.put("0142", "4142"+hex4+"$"); //控制系统电压 VPWR
		obdCmds.put("0143", "4143"+hex4+"$"); //引擎绝对负载
		obdCmds.put("0145", "4145"+hex4+"$"); //节气门相对位置
		obdCmds.put("014D", "414D"+hex4+"$"); //故障灯亮起后引擎运转时间
		obdCmds.put("014E", "414E"+hex4+"$"); //故障码清除后引擎运转时间
		obdCmds.put("0150", "4150"+hex2+"$"); //进气速度系数
	}
	
	private boolean CheckError(String line) {
		if (line.indexOf("<DATA ERROR") != -1 ||
				//line.indexOf("NODATA") != -1 ||
				line.indexOf("UNABLETOCONNECT") != -1 ||
				line.indexOf("BUSBUSY") != -1 ||
				line.indexOf("DATAERROR") != -1 ||
				line.indexOf("BUSERROR") != -1 ||
				line.indexOf("FBERROR") != -1 ||
				line.indexOf("CANERROR") != -1 ||
				line.indexOf("BUFFERFULL") != -1 ||
				line.indexOf("ERROR") != -1) {
			return true;
		}
		return false;
	}
	
	@Override
	public void OnDataListener(byte[] data, int len) {
		if (bStart == false) return;
		if (retRegex == null || retRegex.equals("")) return;
		String buf = new String(data, 0, len);
		sBuf.append(buf.replaceAll("[ \t\r\n]", ""));
		
		int lineEnd = sBuf.indexOf(">");
		if (lineEnd != -1) {
			String line = sBuf.substring(0, lineEnd);
			if (CheckError(line) == false) {
				Pattern pat = Pattern.compile(retRegex);
				Matcher m = pat.matcher(line);
				if (m.find()) {
					if (m.groupCount() >= 1) {
						if (parser.Parse(cmd, m.group(1)) == false) {
							msgHandler.obtainMessage(ObdInterface.MSG_OBD_PARSEFAIL).sendToTarget();
						}
						else {
							msgHandler.obtainMessage(ObdInterface.MSG_OBD_READ).sendToTarget();
						}
					}
				}
			}
			else {
				reInit = true;
			}
			sBuf.delete(0, lineEnd+1);

			synchronized (synclock) {
				synclock.notifyAll();
			}
		}
	}

	@Override
	public void StartGetData(ObdSendAdapter out, Handler msgHandler) {
		super.StartGetData(out, msgHandler);
		stop = false;
		new Thread(obdSend, "OBD_Parse_Thread").start();
	}

	@Override
	public void StopGetData() {
		stop = true;
		super.StopGetData();
	}
	
	private boolean reInit = true;
	private boolean stop = false;
	private Runnable obdSend = new Runnable() {
		@Override
		public void run() {
			while(stop == false) {
				if (reInit) {
					InitModule();
					reInit = false;
				}
				SendRealtime();
				/*msgHandler.obtainMessage(ObdInterface.MSG_OBD_READ).sendToTarget();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}*/
			}
		}
	};

	private static final int timeout = 10000;
	private boolean SendSyncData(String cmd) {
		if (!bStart) return false;
		this.cmd = cmd;
		retRegex = obdCmds.get(cmd);
		if (retRegex == null) return false;
		out.SendData(cmd + "\r");
		try {
			long now = System.currentTimeMillis();
			synchronized (synclock) {
				synclock.wait(timeout);
			}
			if (System.currentTimeMillis() - now >= timeout)
				return false;
		} catch (InterruptedException e) {
		}
		return true;
	}
	
	private void InitModule() {
		SendSyncData("ATZ");
		SendSyncData("ATE0");
		SendSyncData("ATL0");
		SendSyncData("ATSP0");
	}
	
	private void SendRealtime() {
		Set<String> keys = obdCmds.keySet();
		for(String cmd : keys) {
			if (cmd.charAt(0) == '0') {
				if (SendSyncData(cmd) == false || reInit)
					break;
			}
		}
	}
}
