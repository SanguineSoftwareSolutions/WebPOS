package com.sanguine.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.dao.clsLocationMasterDao;
import com.sanguine.model.clsLocationMasterModel;


@Service("objLocationMasterService")
@Transactional(value="webPOSTransactionManager",propagation = Propagation.SUPPORTS, readOnly = true)
public class clsLocationMasterServiceImpl implements clsLocationMasterService
{
	@Autowired
	private clsLocationMasterDao objLocationMasterDao;
	
	
	public List<clsLocationMasterModel> funGetList()
	{
		return objLocationMasterDao.funGetList();
	}

	public clsLocationMasterModel funGetObject(String code,String clientCode)
	{
		return objLocationMasterDao.funGetObject(code,clientCode);
	}
	
	
	public void funAddUpdate(clsLocationMasterModel objModel) 
	{
		
		objLocationMasterDao.funAddUpdate(objModel);
	}
		
	public long funGetLastNo(String tableName,String masterName,String columnName)
	{		
		return objLocationMasterDao.funGetLastNo(tableName, masterName, columnName);
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	
	public List funGetdtlList(String prodCode,String clientCode) 
	{
		return objLocationMasterDao.funGetdtlList(prodCode, clientCode);
	}

	@Override
	
	public Map<String, String> funGetLocMapPropertyWise(String propertyCode,
			String clientCode,String usercode) {
		// TODO Auto-generated method stub
		return objLocationMasterDao.funGetLocMapPropertyWise( propertyCode,
				 clientCode,usercode);
	}

	
}
