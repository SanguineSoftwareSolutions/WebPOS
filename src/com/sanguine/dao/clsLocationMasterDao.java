package com.sanguine.dao;

import java.util.List;
import java.util.Map;

import com.sanguine.model.clsLocationMasterModel;

public interface clsLocationMasterDao 
{
	public void funAddUpdate(clsLocationMasterModel objModel);

	public List<clsLocationMasterModel> funGetList();
		
	public clsLocationMasterModel funGetObject(String code,String clientCode);
	
	public long funGetLastNo(String tableName,String masterName,String columnName);
	
	public List funGetdtlList(String prodCode,String clientCode);

	public Map<String, String> funGetLocMapPropertyWise(String propertyCode,
			String clientCode,String usercode);

	
}
