package com.sanguine.service;

import java.util.List;
import java.util.Map;

import com.sanguine.model.clsLocationMasterModel;


public interface clsLocationMasterService 
{
	public void funAddUpdate(clsLocationMasterModel object);

	public List<clsLocationMasterModel> funGetList();
		
	public clsLocationMasterModel funGetObject(String code,String clientCode);
	
	public List funGetdtlList(String locCode,String clientCode);

	public Map<String, String> funGetLocMapPropertyWise(String propertyCode,
			String clientCode,String usercode);

	
	
}
