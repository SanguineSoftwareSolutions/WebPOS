package com.sanguine.dao;

public interface clsStructureUpdateDao {
	public void funUpdateStructure(String clientCode);

	public void funClearTransaction(String clientCode,String[] str);
	
	public void funClearMaster(String clientCode,String[] str);
	
	public void	funClearTransactionByPropertyAndLoction(String clientCode,String[] str,String propName,String locName);
	
	
}
