package com.leonliu.cm.obd;

import com.leonliu.cm.obd.ObdInterface.CreateObdModule;


public final class ObdContext {

	public static CreateObdModule CreateObdFactory(String model) {
		CreateObdModule fac = null;
		String pkg = ObdContext.class.getPackage().getName();
		try {
			fac = (CreateObdModule) Class.forName(pkg + "." + model + "_Factory").newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return fac;
	}
}
