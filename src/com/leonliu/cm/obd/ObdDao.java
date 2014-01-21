package com.leonliu.cm.obd;

public class ObdDao {

	private double Bat;
	private int rpm;
	private int vss;
	private double tp;
	private double lod;
	private int ect;
	private double mpg;
	private double avm;

	private double Dst;
	private double TDst;
	private double Fue;
	private double TFue;

	private String []codes;

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

}
