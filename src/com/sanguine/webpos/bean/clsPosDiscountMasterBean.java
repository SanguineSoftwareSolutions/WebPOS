package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

public class clsPosDiscountMasterBean 
{

	private String strDiscountCode;
	private String strDiscountName;
	private String strPosCode;
	private String dteFromDate;
	private String dteToDate;
	private String strDiscountOn;
	private String strDiscountOnCode;
	private String strDiscountType;
	private double dblDiscountValue;
	
	private List<clsDiscountDtlsOnBill> listDiscountDtl=new ArrayList<clsDiscountDtlsOnBill>();
	
	public String getStrDiscountCode() {
		return strDiscountCode;
	}
	public void setStrDiscountCode(String strDiscountCode) {
		this.strDiscountCode = strDiscountCode;
	}
	public String getStrDiscountName() {
		return strDiscountName;
	}
	public void setStrDiscountName(String strDiscountName) {
		this.strDiscountName = strDiscountName;
	}
	public String getStrPosCode() {
		return strPosCode;
	}
	public void setStrPosCode(String strPosCode) {
		this.strPosCode = strPosCode;
	}
	public String getDteFromDate() {
		return dteFromDate;
	}
	public void setDteFromDate(String dteFromDate) {
		this.dteFromDate = dteFromDate;
	}
	public String getDteToDate() {
		return dteToDate;
	}
	public void setDteToDate(String dteToDate) {
		this.dteToDate = dteToDate;
	}
	public String getStrDiscountOn() {
		return strDiscountOn;
	}
	public void setStrDiscountOn(String strDiscountOn) {
		this.strDiscountOn = strDiscountOn;
	}
	public String getStrDiscountOnCode() {
		return strDiscountOnCode;
	}
	public void setStrDiscountOnCode(String strDiscountOnCode) {
		this.strDiscountOnCode = strDiscountOnCode;
	}
	public String getStrDiscountType() {
		return strDiscountType;
	}
	public void setStrDiscountType(String strDiscountType) {
		this.strDiscountType = strDiscountType;
	}
	public double getDblDiscountValue() {
		return dblDiscountValue;
	}
	public void setDblDiscountValue(double dblDiscountValue) {
		this.dblDiscountValue = dblDiscountValue;
	}
	
	
}
