package com.sanguine.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.model.clsLinkUpHdModel;
import com.sanguine.model.clsLinkUpModel_ID;

@Repository("clsARLinkUpDao")
public class clsLinkUpDaoImpl implements clsLinkUpDao{

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	@Transactional(value = "webPOSTransactionManager")
	public void funAddUpdateARLinkUp(clsLinkUpHdModel objMaster){
		sessionFactory.getCurrentSession().saveOrUpdate(objMaster);
	}

	@Override
	@Transactional(value = "webPOSTransactionManager")
	public clsLinkUpHdModel funGetARLinkUp(String docCode,String clientCode,String propCode){
		return (clsLinkUpHdModel) sessionFactory.getCurrentSession().get(clsLinkUpHdModel.class,new clsLinkUpModel_ID(docCode,clientCode, propCode));
	}
	
	@Override
	public int funExecute(String sql)
	{
		return sessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
	}


}
