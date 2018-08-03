package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;





public class clsPOSBillSettlementBean {

	private JSONArray jsonArrForSettleButtons=new JSONArray();

	private String dteExpiryDate;
	
	private double dblDeliveryCharges;
	
	private String strManualBillNo;
	
	private List<clsItemsDtlsInBill>listOfBillItemDtl=new  ArrayList<clsItemsDtlsInBill>();
	
	private List <clsItemsDtlsInBill> listItemsDtlInBill=new ArrayList<clsItemsDtlsInBill>();
	
	private List<clsDiscountDtlsOnBill> listDiscountDtlOnBill=new ArrayList<clsDiscountDtlsOnBill>();
	
	private List<clsTaxDtlsOnBill> listTaxDtlOnBill=new ArrayList<clsTaxDtlsOnBill>();
	
	private List<clsSettlementDtlsOnBill> listSettlementDtlOnBill=new ArrayList<clsSettlementDtlsOnBill>();
	
	private List<clsCustomerDtlsOnBill> listCustomerDtlOnBill=new ArrayList<clsCustomerDtlsOnBill>();
	
	private List<clsDeliveryBoyMasterBean> listDeliveryBoyMasterBeanl=new ArrayList<clsDeliveryBoyMasterBean>();
	
	private double dblSubTotal;
	
	private double dblDicountTotal;
	
	private double dblNetTotal;
	
	private double dblGrandTotal;
	
	private double dblRefund;
	
	private double dblTotalTaxAmt;
	
	private double dblDiscountAmt;
	
	private double dblDiscountPer;
	
	private String strSettlementType;
	
	private String strDisountOn;
	
	private String strRemarks;
	
	public String getDteExpiryDate() {
		return dteExpiryDate;
	}

	public void setDteExpiryDate(String dteExpiryDate) {
		this.dteExpiryDate = dteExpiryDate;
	}

	public JSONArray getJsonArrForSettleButtons() {
		return jsonArrForSettleButtons;
	}

	public void setJsonArrForSettleButtons(JSONArray jsonArrForSettleButtons) {
		this.jsonArrForSettleButtons = jsonArrForSettleButtons;
	}

	public List<clsItemsDtlsInBill> getListOfBillItemDtl() {
		return listOfBillItemDtl;
	}

	public void setListOfBillItemDtl(List<clsItemsDtlsInBill> listOfBillItemDtl) {
		this.listOfBillItemDtl = listOfBillItemDtl;
	}

	public double getDblDeliveryCharges() {
		return dblDeliveryCharges;
	}

	public void setDblDeliveryCharges(double dblDeliveryCharges) {
		this.dblDeliveryCharges = dblDeliveryCharges;
	}

	public String getStrManualBillNo() {
		return strManualBillNo;
	}

	public void setStrManualBillNo(String strManualBillNo) {
		this.strManualBillNo = strManualBillNo;
	}

	

	public List<clsDiscountDtlsOnBill> getListDiscountDtlOnBill() {
		return listDiscountDtlOnBill;
	}

	public void setListDiscountDtlOnBill(
			List<clsDiscountDtlsOnBill> listDiscountDtlOnBill) {
		this.listDiscountDtlOnBill = listDiscountDtlOnBill;
	}

	public List<clsTaxDtlsOnBill> getListTaxDtlOnBill() {
		return listTaxDtlOnBill;
	}

	public void setListTaxDtlOnBill(List<clsTaxDtlsOnBill> listTaxDtlOnBill) {
		this.listTaxDtlOnBill = listTaxDtlOnBill;
	}

	public List<clsSettlementDtlsOnBill> getListSettlementDtlOnBill() {
		return listSettlementDtlOnBill;
	}

	public void setListSettlementDtlOnBill(
			List<clsSettlementDtlsOnBill> listSettlementDtlOnBill) {
		this.listSettlementDtlOnBill = listSettlementDtlOnBill;
	}

	public List<clsCustomerDtlsOnBill> getListCustomerDtlOnBill() {
		return listCustomerDtlOnBill;
	}

	public void setListCustomerDtlOnBill(
			List<clsCustomerDtlsOnBill> listCustomerDtlOnBill) {
		this.listCustomerDtlOnBill = listCustomerDtlOnBill;
	}

	public List<clsDeliveryBoyMasterBean> getListDeliveryBoyMasterBeanl() {
		return listDeliveryBoyMasterBeanl;
	}

	public void setListDeliveryBoyMasterBeanl(
			List<clsDeliveryBoyMasterBean> listDeliveryBoyMasterBeanl) {
		this.listDeliveryBoyMasterBeanl = listDeliveryBoyMasterBeanl;
	}

	public double getDblSubTotal() {
		return dblSubTotal;
	}

	public void setDblSubTotal(double dblSubTotal) {
		this.dblSubTotal = dblSubTotal;
	}

	public double getDblDicountTotal() {
		return dblDicountTotal;
	}

	public void setDblDicountTotal(double dblDicountTotal) {
		this.dblDicountTotal = dblDicountTotal;
	}

	public double getDblNetTotal() {
		return dblNetTotal;
	}

	public void setDblNetTotal(double dblNetTotal) {
		this.dblNetTotal = dblNetTotal;
	}

	public double getDblGrandTotal() {
		return dblGrandTotal;
	}

	public void setDblGrandTotal(double dblGrandTotal) {
		this.dblGrandTotal = dblGrandTotal;
	}

	public double getDblRefund() {
		return dblRefund;
	}

	public void setDblRefund(double dblRefund) {
		this.dblRefund = dblRefund;
	}

	public List<clsItemsDtlsInBill> getListItemsDtlInBill() {
		return listItemsDtlInBill;
	}

	public void setListItemsDtlInBill(List<clsItemsDtlsInBill> listItemsDtlInBill) {
		this.listItemsDtlInBill = listItemsDtlInBill;
	}

	public double getDblTotalTaxAmt() {
		return dblTotalTaxAmt;
	}

	public void setDblTotalTaxAmt(double dblTotalTaxAmt) {
		this.dblTotalTaxAmt = dblTotalTaxAmt;
	}

	public double getDblDiscountAmt() {
		return dblDiscountAmt;
	}

	public void setDblDiscountAmt(double dblDiscountAmt) {
		this.dblDiscountAmt = dblDiscountAmt;
	}

	public double getDblDiscountPer() {
		return dblDiscountPer;
	}

	public void setDblDiscountPer(double dblDiscountPer) {
		this.dblDiscountPer = dblDiscountPer;
	}

	public String getStrSettlementType() {
		return strSettlementType;
	}

	public void setStrSettlementType(String strSettlementType) {
		this.strSettlementType = strSettlementType;
	}

	public String getStrDisountOn() {
		return strDisountOn;
	}

	public void setStrDisountOn(String strDisountOn) {
		this.strDisountOn = strDisountOn;
	}

	public String getStrRemarks() {
		return strRemarks;
	}

	public void setStrRemarks(String strRemarks) {
		this.strRemarks = strRemarks;
	}


	

	
}
