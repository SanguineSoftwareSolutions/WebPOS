package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.List;

public class clsBillSeriesTypeMasterBean 
{
	private String strGroupName;
	private String strGroupCode;
	private String strSelect;
	private String strPrintGTOfOtherBills;
	private String bllSeries;
	private String strPrintInclusiveOfTaxOnBill;
	private String strType;
	private String strPOSName;
	
	
	private List<clsBillSeriesTypeMasterBean> listDtl=new ArrayList<clsBillSeriesTypeMasterBean>();
	
	private List<clsBillSeriesDtlBean> listBillSeriesDtl=new ArrayList<clsBillSeriesDtlBean>();
	
	public String getStrPrintGTOfOtherBills() {
		return strPrintGTOfOtherBills;
	}

	public void setStrPrintGTOfOtherBills(String strPrintGTOfOtherBills) {
		this.strPrintGTOfOtherBills = strPrintGTOfOtherBills;
	}

	public String getBllSeries() {
		return bllSeries;
	}

	public void setBllSeries(String bllSeries) {
		this.bllSeries = bllSeries;
	}

	public String getStrPrintInclusiveOfTaxOnBill() {
		return strPrintInclusiveOfTaxOnBill;
	}

	public void setStrPrintInclusiveOfTaxOnBill(String strPrintInclusiveOfTaxOnBill) {
		this.strPrintInclusiveOfTaxOnBill = strPrintInclusiveOfTaxOnBill;
	}

	

	public String getStrGroupName() {
		return strGroupName;
	}

	public void setStrGroupName(String strGroupName) {
		this.strGroupName = strGroupName;
	}

	public String getStrGroupCode() {
		return strGroupCode;
	}

	public void setStrGroupCode(String strGroupCode) {
		this.strGroupCode = strGroupCode;
	}

	public String getStrSelect() {
		return strSelect;
	}

	public void setStrSelect(String strSelect) {
		this.strSelect = strSelect;
	}

	public List<clsBillSeriesTypeMasterBean> getListDtl() {
		return listDtl;
	}

	public void setListDtl(List<clsBillSeriesTypeMasterBean> listDtl) {
		this.listDtl = listDtl;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public List<clsBillSeriesDtlBean> getListBillSeriesDtl() {
		return listBillSeriesDtl;
	}

	public void setListBillSeriesDtl(List<clsBillSeriesDtlBean> listBillSeriesDtl) {
		this.listBillSeriesDtl = listBillSeriesDtl;
	}

	public String getStrPOSName() {
		return strPOSName;
	}

	public void setStrPOSName(String strPOSName) {
		this.strPOSName = strPOSName;
	}

	

	

}
