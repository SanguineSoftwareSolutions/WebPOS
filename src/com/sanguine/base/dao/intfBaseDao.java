package com.sanguine.base.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sanguine.base.model.clsBaseModel;



public interface intfBaseDao {
	
public String funSave(clsBaseModel objBaseModel);
	
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key);
	
	public clsBaseModel funGet(clsBaseModel objBaseModel,Serializable key);
	
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode);
	
	public List funGetSerachList(String query,String clientCode) throws Exception;
	
	public List funGetList(StringBuilder query,String queryType) throws Exception;

	public int funExecuteUpdate(String query,String queryType) throws Exception;
	
	public List funLoadAllPOSWise(clsBaseModel objBaseModel,String clientCode,String strPOSCode) throws Exception;

	public List funLoadAllCriteriaWise(clsBaseModel objBaseModel, String criteriaName,
			String criteriaValue);

	public clsBaseModel funGetAllMasterDataByDocCodeWise(String sql,Map<String,String> hmParameters);
	
	public clsBaseModel funGetMenuItemPricingMaster(String sql,long id,String clientCode);
	
}
