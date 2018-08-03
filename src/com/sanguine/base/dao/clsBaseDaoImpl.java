package com.sanguine.base.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sanguine.base.model.clsBaseModel;
import com.sanguine.webpos.model.clsPricingMasterHdModel;
@Repository("intfBaseDao")
@Transactional(value="webPOSTransactionManager")
public class clsBaseDaoImpl implements intfBaseDao{

	@Autowired
	private SessionFactory webPOSSessionFactory;
	
	@Override
	public String funSave(clsBaseModel objBaseModel) {
		webPOSSessionFactory.getCurrentSession().saveOrUpdate(objBaseModel);
		return objBaseModel.getDocCode();
	}

	@Override
	public clsBaseModel funLoad(clsBaseModel objBaseModel,Serializable key)
	{
		return (clsBaseModel) webPOSSessionFactory.getCurrentSession().load(objBaseModel.getClass(), key);		
	}
	@Override
	public clsBaseModel funGet(clsBaseModel objBaseModel,Serializable key)
	{
		return (clsBaseModel) webPOSSessionFactory.getCurrentSession().get(objBaseModel.getClass(), key);		
	}
	
	@Override
	public List funLoadAll(clsBaseModel objBaseModel,String clientCode)
	{
		Criteria cr=webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass());
		cr.add(Restrictions.eq("strClientCode", clientCode));
		
		return webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass()).list();
	}
	
	
	@Override
	public List funGetSerachList(String sql,String clientCode) throws Exception
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		query.setParameter("clientCode", clientCode);
		
		return query.list(); 
	}
	
	
	public List funGetList(StringBuilder strQuery,String queryType) throws Exception
	{
		Query query;
		if(queryType.equals("sql"))
		{
			query=webPOSSessionFactory.getCurrentSession().createSQLQuery(strQuery.toString());
			return query.list();
		}
		else{
			query=webPOSSessionFactory.getCurrentSession().createQuery(strQuery.toString());
			return query.list();
		}
	}
	
	public int funExecuteUpdate(String strQuery,String queryType) throws Exception
	{
		Query query;
		if(queryType.equalsIgnoreCase("sql"))
		{
			query=webPOSSessionFactory.getCurrentSession().createSQLQuery(strQuery);
			return query.executeUpdate();
		}
		else{
			query=webPOSSessionFactory.getCurrentSession().createQuery(strQuery);
			return query.executeUpdate();
		}
	}
	
	@Override
	public List funLoadAllPOSWise(clsBaseModel objBaseModel,String clientCode,String strPOSCode)throws Exception
	{
		Criteria cr=webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass());
		cr.add(Restrictions.eq("strClientCode", clientCode));
		cr.add(Restrictions.eq("strPOSCode", strPOSCode));
		
		return webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass()).list();
	}
	
	@Override
	public List funLoadAllCriteriaWise(clsBaseModel objBaseModel,String criteriaName,String criteriaValue)
	{
		Criteria cr=webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass());
		cr.add(Restrictions.eq(criteriaName, criteriaValue));
		
		return webPOSSessionFactory.getCurrentSession().createCriteria(objBaseModel.getClass()).list();
	}
	
	@Override
	public clsBaseModel funGetAllMasterDataByDocCodeWise(String sql,Map<String,String> hmParameters) 
   	{
   		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
   		for(Map.Entry<String, String> entrySet:hmParameters.entrySet())
   		{
   			query.setParameter(entrySet.getKey(), entrySet.getValue());
   		}
   		List list=query.list();
   		
   		clsBaseModel model=new clsBaseModel();
   		if(list.size()>0)
   		{
   			model=(clsBaseModel)list.get(0);
   			
   		}
   		return model; 
   	} 
	
	@Override
	public clsBaseModel funGetMenuItemPricingMaster(String sql,long id,String clientCode)
	{
		Query query=webPOSSessionFactory.getCurrentSession().getNamedQuery(sql);
		query.setParameter("longPricingId",id);
		query.setParameter("gClientCode",clientCode);
		List list=query.list();
		
		clsPricingMasterHdModel model=new clsPricingMasterHdModel();
		if(list.size()>0)
		{
			model=(clsPricingMasterHdModel)list.get(0);
			
		}
		return model; 
		
	}
}
