package com.leonliu.cm.obd;

public class ObdDao {

	private double Bat;	//电瓶电压
	private int rpm;		//发动机转速
	private int vss;		//行驶时速
	private double tp;		//节气门开度
	private double lod;	//发动机负荷
	private int ect;		//冷却液温度
	private double mpg;	//瞬时油耗
	private double avm;	//平均油耗

	private double Dst;	//本次行驶里程
	private double TDst;	//总里程
	private double Fue;	//本次耗油量
	private double TFue;	//累计耗油量

	private String []codes;

	private String ModuleName;
	
	public synchronized double getBat() {
		return Bat;
	}

	public synchronized void setBat(double bat) {
		Bat = bat;
	}

	public synchronized int getRpm() {
		return rpm;
	}

	public synchronized void setRpm(int rpm) {
		this.rpm = rpm;
	}

	public synchronized int getVss() {
		return vss;
	}

	public synchronized void setVss(int vss) {
		this.vss = vss;
	}

	public synchronized double getTp() {
		return tp;
	}

	public synchronized void setTp(double tp) {
		this.tp = tp;
	}

	public synchronized double getLod() {
		return lod;
	}

	public synchronized void setLod(double lod) {
		this.lod = lod;
	}

	public synchronized int getEct() {
		return ect;
	}

	public synchronized void setEct(int ect) {
		this.ect = ect;
	}

	public synchronized double getMpg() {
		return mpg;
	}

	public synchronized void setMpg(double mpg) {
		this.mpg = mpg;
	}

	public synchronized double getAvm() {
		return avm;
	}

	public synchronized void setAvm(double avm) {
		this.avm = avm;
	}

	public synchronized double getDst() {
		return Dst;
	}

	public synchronized void setDst(double dst) {
		Dst = dst;
	}

	public synchronized double getTDst() {
		return TDst;
	}

	public synchronized void setTDst(double tDst) {
		TDst = tDst;
	}

	public synchronized double getFue() {
		return Fue;
	}

	public synchronized void setFue(double fue) {
		Fue = fue;
	}

	public synchronized double getTFue() {
		return TFue;
	}

	public synchronized void setTFue(double tFue) {
		TFue = tFue;
	}

	public synchronized String[] getCodes() {
		return codes;
	}

	public synchronized void setCodes(String[] codes) {
		this.codes = codes;
	}

	public synchronized String getModuleName() {
		return ModuleName;
	}

	public synchronized void setModuleName(String moduleName) {
		ModuleName = moduleName;
	}

}
