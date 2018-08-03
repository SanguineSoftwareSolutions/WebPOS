package com.sanguine.dao;

import com.sanguine.model.clsLinkUpHdModel;

public interface clsLinkUpDao{

	public void funAddUpdateARLinkUp(clsLinkUpHdModel objMaster);

	public clsLinkUpHdModel funGetARLinkUp(String docCode,String clientCode,String propCode);
	
	public int funExecute(String sql);

}
