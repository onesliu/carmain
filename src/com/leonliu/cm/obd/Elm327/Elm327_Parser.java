package com.leonliu.cm.obd.Elm327;

import java.util.HashMap;
import java.util.Map;

import com.leonliu.cm.obd.ObdInterface.OnObdData;

public class Elm327_Parser {

	private final OnObdData onData;
	private Map<String, Parser> obdCmds;
	
	public Elm327_Parser(OnObdData obdData) {
		onData = obdData;
		InitCmds();
	}
	
	private void InitCmds() {
		obdCmds = new HashMap<String, Elm327_Parser.Parser>();
		obdCmds.put("0104", parser0104); //发动机负荷
		obdCmds.put("0105", parser0105); //冷却液温度
		obdCmds.put("010A", parser010A); //燃油管路压力
		obdCmds.put("010B", parser010B); //进气岐管压力 MAP
		obdCmds.put("010C", parser010C); //发动机转速
		obdCmds.put("010D", parser010D); //行驶时速
		obdCmds.put("010E", parser010E); //点火提前角
		obdCmds.put("010F", parser010F); //进气温度
		obdCmds.put("0110", parser0110); //进气速度 MAF
		obdCmds.put("0111", parser0111); //节气门开度
		obdCmds.put("011F", parser011F); //引擎启动时间
		obdCmds.put("0121", parser0121); //故障灯亮起后行驶的距离
		obdCmds.put("0122", parser0122); //油路相对进气岐管压力 FRP
		obdCmds.put("0123", parser0123); //油路压力 FRP
		obdCmds.put("012F", parser012F); //燃油液位输入
		obdCmds.put("0130", parser0130); //故障码清除后的启动次数
		obdCmds.put("0131", parser0131); //清除故障码后行驶的距离
		obdCmds.put("0132", parser0132); //燃油蒸发系统压力
		obdCmds.put("0133", parser0133); //大气压力 BARO
		obdCmds.put("0142", parser0142); //控制系统电压 VPWR
		obdCmds.put("0143", parser0143); //引擎绝对负载
		obdCmds.put("0145", parser0145); //节气门相对位置
		obdCmds.put("014D", parser014D); //故障灯亮起后引擎运转时间
		obdCmds.put("014E", parser014E); //故障码清除后引擎运转时间
		obdCmds.put("0150", parser0150); //进气速度系数
	}
	
	public boolean Parse(String cmd, String codes) {
		return obdCmds.get(cmd).parse_standard_obd(codes);
	}
	
	private int last_tdist = 0; 
	private void Calc_TDist() {
		if (last_tdist == 0)
			last_tdist = mil_dist + clr_dist;
		if (mil_dist + clr_dist - last_tdist > 0) {
			last_tdist = mil_dist + clr_dist;
			onData.OnTDst(last_tdist);
		}
	}

	private interface Parser {
		boolean parse_standard_obd(String codes);
	}
	
	Parser parser0104 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			onData.OnLod((double)c1/255*100);
			return true;
		}
	};
	Parser parser0105 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			onData.OnEct(c1-40);
			return true;
		}
	};
	private int frp_relative;
	Parser parser010A = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			frp_relative = c1 * 3;
			return true;
		}
	};
	private int map_absolute;
	Parser parser010B = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			map_absolute = Integer.parseInt(codes, 16);
			return true;
		}
	};
	Parser parser010C = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			onData.OnRpm(c1/4);
			return true;
		}
	};
	Parser parser010D = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			onData.OnVss(Integer.parseInt(codes, 16));
			return true;
		}
	};
	private double sparkadv;
	Parser parser010E = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			sparkadv = c1/2;
			return true;
		}
	};
	private int iat;
	Parser parser010F = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			iat = c1-40;
			return true;
		}
	};
	private double maf;
	Parser parser0110 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			maf = (double)c1/100;
			return true;
		}
	};
	Parser parser0111 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			onData.OnTp((double)c1/255*100);
			return true;
		}
	};
	private int engine_start_time;
	Parser parser011F = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			engine_start_time = Integer.parseInt(codes, 16);
			return true;
		}
	};
	private int mil_dist;
	Parser parser0121 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			mil_dist = Integer.parseInt(codes, 16);
			return true;
		}
	};
	private double frp_relative_mv;
	Parser parser0122 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			frp_relative_mv = (double)c1 * 0.079;
			return true;
		}
	};
	private int ftp_absolute;
	Parser parser0123 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			ftp_absolute = c1 * 10;
			return true;
		}
	};
	private double fli;
	Parser parser012F = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			fli = (double)c1/255*100;
			return true;
		}
	};
	private int warm_ups;
	Parser parser0130 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			warm_ups = Integer.parseInt(codes, 16);
			return true;
		}
	};
	private int clr_dist;
	Parser parser0131 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			clr_dist = Integer.parseInt(codes, 16);
			Calc_TDist(); //计算总里程
			return true;
		}
	};
	private double evap_vp;
	Parser parser0132 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			evap_vp = (double)c1 * 0.25;
			return true;
		}
	};
	private int baro;
	Parser parser0133 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			baro = Integer.parseInt(codes, 16);
			return true;
		}
	};
	Parser parser0142 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			onData.OnBat((double)c1 * 0.001);
			return true;
		}
	};
	private double load_abs;
	Parser parser0143 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			load_abs = (double)c1/255*100;
			return true;
		}
	};
	private double tp_relative;
	Parser parser0145 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			tp_relative = (double)c1/255*100;
			return true;
		}
	};
	private int mil_time;
	Parser parser014D = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			mil_time = Integer.parseInt(codes, 16);
			return true;
		}
	};
	private int clr_time;
	Parser parser014E = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			clr_time = Integer.parseInt(codes, 16);
			return true;
		}
	};
	private int maf_rate;
	Parser parser0150 = new Parser() {
		@Override
		public boolean parse_standard_obd(String codes) {
			int c1 = Integer.parseInt(codes, 16);
			maf_rate = c1 * 10;
			return true;
		}
	};
	
}
