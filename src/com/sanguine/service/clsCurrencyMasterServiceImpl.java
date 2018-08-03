package com.sanguine.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.dao.clsCurrencyMasterDao;
import com.sanguine.model.clsCurrencyMasterModel;

@Service("clsCurrencyMasterService")
@Transactional(value="webPOSTransactionManager",propagation = Propagation.REQUIRED, readOnly = false)
public class clsCurrencyMasterServiceImpl implements clsCurrencyMasterService{
	@Autowired
	private clsCurrencyMasterDao objCurrencyMasterDao;

	@Override
	
	public void funAddUpdateCurrencyMaster(clsCurrencyMasterModel objMaster){
		objCurrencyMasterDao.funAddUpdateCurrencyMaster(objMaster);
	}

	@Override
	
	public clsCurrencyMasterModel funGetCurrencyMaster(String docCode,String clientCode){
		return objCurrencyMasterDao.funGetCurrencyMaster(docCode,clientCode);
	}
	
	
	@Override
	
	public Map<String,String> funGetAllCurrency(String clientCode)
	{
		return objCurrencyMasterDao.funGetAllCurrency(clientCode);
	}
	
	@Override
	
	public List<clsCurrencyMasterModel> funGetAllCurrencyDataModel(String clientCode)
	{
		return objCurrencyMasterDao.funGetAllCurrencyDataModel(clientCode);
	}



}
