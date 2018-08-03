package com.sanguine.base.model;

import java.io.Serializable;


public class clsBaseModel implements Serializable{
private static final long serialVersionUID = 1L;
	
	clsBaseModel obBaseModel;
	
	private String docCode;

	public clsBaseModel getObBaseModel() {
		return obBaseModel;
	}

	public void setObBaseModel(clsBaseModel obBaseModel) {
		this.obBaseModel = obBaseModel;
	}

	public String getDocCode() {
		return docCode;
	}

	public void setDocCode(String docCode) {
		this.docCode = docCode;
	}
	

}
