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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnLod(double lod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnEct(int ect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnMpg(double mpg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAvm(double avm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnDiagnosis(String[] codes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnDst(double distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnTDst(double distance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnFue(double liter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnTFue(double liter) {
		// TODO Auto-generated method stub
		
	}

}
