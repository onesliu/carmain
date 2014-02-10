package com.leonliu.cm.obd;

import com.leonliu.cm.obd.ObdInterface.OnObdData;

public class OnObdHandler implements OnObdData {

	private ObdDao dao = new ObdDao();

	public ObdDao getDao() {
		return dao;
	}
	
	@Override
	public void OnBat(double voltage) {
		dao.setBat(voltage);
	}

	@Override
	public void OnRpm(int rpm) {
		dao.setRpm(rpm);
	}

	@Override
	public void OnVss(int vss) {
		dao.setVss(vss);
	}

	@Override
	public void OnTp(double tp) {
		dao.setTp(tp);
	}

	@Override
	public void OnLod(double lod) {
		dao.setLod(lod);
	}

	@Override
	public void OnEct(int ect) {
		dao.setEct(ect);
	}

	@Override
	public void OnMpg(double mpg) {
		dao.setMpg(mpg);
	}

	@Override
	public void OnAvm(double avm) {
		dao.setAvm(avm);
	}

	@Override
	public void OnDiagnosis(String[] codes) {
		dao.setCodes(codes);
	}

	@Override
	public void OnDst(double distance) {
		dao.setDst(distance);
	}

	@Override
	public void OnTDst(double distance) {
		dao.setTDst(distance + prev_dist);
	}
	private double prev_dist = 0;
	public void SetPrevTDst(double dist) {
		prev_dist = dist;
	}

	@Override
	public void OnFue(double liter) {
		dao.setFue(liter);
	}

	@Override
	public void OnTFue(double liter) {
		dao.setTFue(liter);
	}

	@Override
	public void OnModuleName(String name) {
		dao.setModuleName(name);
	}

}
