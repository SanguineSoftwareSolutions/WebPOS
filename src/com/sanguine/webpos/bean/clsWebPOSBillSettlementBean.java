package com.sanguine.webpos.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;

public class clsWebPOSBillSettlementBean
{
	/////Direct Biller
	private String strBillNo;
    private String dteBillDate;
    private String strPosName;

    private String strItemCode;
    private String strItemName;
    private double dblQuantity;
    private double dblAmount;


	private double dblBillDiscPer;
    private String strSettelmentMode;
    private double dblTaxAmt;
    private double dblSettlementAmt;
    private String strDiscValue;
    private String strDiscType;
    private String billTransType; 
    private String strCustomerCode;
    private String customerName;
    private String strCustomerType;
    private String takeAway;
    private String custMobileNo;
    private String strDeliveryBoyCode;
    private String strDeliveryBoyName;
    private String strSubGroupCode;
    private String strGroupCode;
    private double dblRate;

    
    private List<clsPricingMasterBean> listOfDirectBillerMenuItemPricing=new  LinkedList<>();
    private List<clsPOSMenuHeadBean> listOfDirectBillerMenuHeads=new  LinkedList<>();
    
    private List<clsBillItemDtlBean>listOfDirectBillerBillItemDtl=new  LinkedList<>();
   
   
        
    private JSONArray jsonArrForDirectBillerMenuItemPricing=new JSONArray();   
	private JSONArray jsonArrForDirectBillerMenuHeads=new JSONArray();
	private JSONArray jsonArrForDirectBillerBillItemDtl=new JSONArray();
	private JSONArray jsonArrForDirectBillerFooterButtons=new JSONArray();
	private JSONArray jsonArrForPopularItems;

	
    
	private JSONArray jsonArrForSettleButtons=new JSONArray();

	////Bill Settlement
	
	
	
	
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
	
	
	
	//// KOT Variable
	
	private String strKOTNo;
	
	private String strTableNo;
	
	private String strWaiter;
	
	private int intPaxNo;
	
	private double strDeditCardBalance;  
	
	private double total;
	
	private JSONArray jsonArrForMenuItemPricing;
	
	private JSONArray jsonArrForMenuHeads;
	
	private JSONArray jsonArrForTableDtl;
	
	private JSONArray jsonArrForWaiterDtl;
	
	private JSONArray jsonArrForButtonList;
	
	private JSONArray jsonArrForTopModifierButton;
	
	private JSONArray jsonArrForModifiers;
	
	private List<clsMakeKotBillItemDtlBean> listOfMakeKOTBillItemDtl;
	
	public String getStrBillNo()
	{
		return strBillNo;
	}
	public void setStrBillNo(String strBillNo)
	{
		this.strBillNo = strBillNo;
	}
	public String getDteBillDate()
	{
		return dteBillDate;
	}
	public void setDteBillDate(String dteBillDate)
	{
		this.dteBillDate = dteBillDate;
	}
	public String getStrPosName()
	{
		return strPosName;
	}
	public void setStrPosName(String strPosName)
	{
		this.strPosName = strPosName;
	}
	public double getDblSubTotal()
	{
		return dblSubTotal;
	}
	public void setDblSubTotal(double dblSubTotal)
	{
		this.dblSubTotal = dblSubTotal;
	}
	public double getDblGrandTotal()
	{
		return dblGrandTotal;
	}
	public void setDblGrandTotal(double dblGrandTotal)
	{
		this.dblGrandTotal = dblGrandTotal;
	}
	public String getStrItemCode()
	{
		return strItemCode;
	}
	public void setStrItemCode(String strItemCode)
	{
		this.strItemCode = strItemCode;
	}
	public String getStrItemName()
	{
		return strItemName;
	}
	public void setStrItemName(String strItemName)
	{
		this.strItemName = strItemName;
	}
	public double getDblQuantity()
	{
		return dblQuantity;
	}
	public void setDblQuantity(double dblQuantity)
	{
		this.dblQuantity = dblQuantity;
	}
	public double getDblAmount()
	{
		return dblAmount;
	}
	public void setDblAmount(double dblAmount)
	{
		this.dblAmount = dblAmount;
	}
	public double getDblDiscountAmt()
	{
		return dblDiscountAmt;
	}
	public void setDblDiscountAmt(double dblDiscountAmt)
	{
		this.dblDiscountAmt = dblDiscountAmt;
	}
	public double getDblDiscountPer()
	{
		return dblDiscountPer;
	}
	public void setDblDiscountPer(double dblDiscountPer)
	{
		this.dblDiscountPer = dblDiscountPer;
	}
	public double getDblBillDiscPer()
	{
		return dblBillDiscPer;
	}
	public void setDblBillDiscPer(double dblBillDiscPer)
	{
		this.dblBillDiscPer = dblBillDiscPer;
	}
	public String getStrSettelmentMode()
	{
		return strSettelmentMode;
	}
	public void setStrSettelmentMode(String strSettelmentMode)
	{
		this.strSettelmentMode = strSettelmentMode;
	}
	public double getDblTaxAmt()
	{
		return dblTaxAmt;
	}
	public void setDblTaxAmt(double dblTaxAmt)
	{
		this.dblTaxAmt = dblTaxAmt;
	}
	public double getDblSettlementAmt()
	{
		return dblSettlementAmt;
	}
	public void setDblSettlementAmt(double dblSettlementAmt)
	{
		this.dblSettlementAmt = dblSettlementAmt;
	}
	public String getStrDiscValue()
	{
		return strDiscValue;
	}
	public void setStrDiscValue(String strDiscValue)
	{
		this.strDiscValue = strDiscValue;
	}
	public String getStrDiscType()
	{
		return strDiscType;
	}
	public void setStrDiscType(String strDiscType)
	{
		this.strDiscType = strDiscType;
	}
	public List<clsPricingMasterBean> getListOfDirectBillerMenuItemPricing()
	{
		return listOfDirectBillerMenuItemPricing;
	}
	public void setListOfDirectBillerMenuItemPricing(List<clsPricingMasterBean> listOfDirectBillerMenuItemPricing)
	{
		this.listOfDirectBillerMenuItemPricing = listOfDirectBillerMenuItemPricing;
	}
	
	public List<clsPOSMenuHeadBean> getListOfDirectBillerMenuHeads()
	{
		return listOfDirectBillerMenuHeads;
	}
	public void setListOfDirectBillerMenuHeads(List<clsPOSMenuHeadBean> listOfDirectBillerMenuHeads)
	{
		this.listOfDirectBillerMenuHeads = listOfDirectBillerMenuHeads;
	}
	
	public JSONArray getJsonArrForDirectBillerMenuItemPricing()
	{
			return jsonArrForDirectBillerMenuItemPricing;
	}
	public void setJsonArrForDirectBillerMenuItemPricing(JSONArray jsonArrForDirectBillerMenuItemPricing)
	{
		this.jsonArrForDirectBillerMenuItemPricing = jsonArrForDirectBillerMenuItemPricing;
	}
	public JSONArray getJsonArrForDirectBillerMenuHeads()
	{
			return jsonArrForDirectBillerMenuHeads;
	}
	public void setJsonArrForDirectBillerMenuHeads(JSONArray jsonArrForDirectBillerMenuHeads)
	{
			this.jsonArrForDirectBillerMenuHeads = jsonArrForDirectBillerMenuHeads;
	}
	public JSONArray getJsonArrForDirectBillerBillItemDtl()
	{
		return jsonArrForDirectBillerBillItemDtl;
	}
	public void setJsonArrForDirectBillerBillItemDtl(JSONArray jsonArrForDirectBillerBillItemDtl)
	{
		this.jsonArrForDirectBillerBillItemDtl = jsonArrForDirectBillerBillItemDtl;
	}
	public List<clsBillItemDtlBean> getListOfDirectBillerBillItemDtl()
	{
		return listOfDirectBillerBillItemDtl;
	}
	public void setListOfDirectBillerBillItemDtl(List<clsBillItemDtlBean> listOfDirectBillerBillItemDtl)
	{
		this.listOfDirectBillerBillItemDtl = listOfDirectBillerBillItemDtl;
	}
	public JSONArray getJsonArrForDirectBillerFooterButtons()
	{
		return jsonArrForDirectBillerFooterButtons;
	}
	public void setJsonArrForDirectBillerFooterButtons(JSONArray jsonArrForDirectBillerFooterButtons)
	{
		this.jsonArrForDirectBillerFooterButtons = jsonArrForDirectBillerFooterButtons;
	}
	public JSONArray getJsonArrForPopularItems() {
		return jsonArrForPopularItems;
	}
	public void setJsonArrForPopularItems(JSONArray jsonArrForPopularItems) {
		this.jsonArrForPopularItems = jsonArrForPopularItems;
	}
	public String getStrCustomerCode() {
		return strCustomerCode;
	}
	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getStrCustomerType() {
		return strCustomerType;
	}
	public void setStrCustomerType(String strCustomerType) {
		this.strCustomerType = strCustomerType;
	}
	
	public String getTakeAway() {
		return takeAway;
	}
	public void setTakeAway(String takeAway) {
		this.takeAway = takeAway;
	}
	public String getBillTransType() {
		return billTransType;
	}
	public void setBillTransType(String billTransType) {
		this.billTransType = billTransType;
	}
	public List<clsItemsDtlsInBill> getListItemsDtlInBill() {
		return listItemsDtlInBill;
	}
	public void setListItemsDtlInBill(List<clsItemsDtlsInBill> listItemsDtlInBill) {
		this.listItemsDtlInBill = listItemsDtlInBill;
	}
	public String getCustMobileNo() {
		return custMobileNo;
	}
	public void setCustMobileNo(String custMobileNo) {
		this.custMobileNo = custMobileNo;
	}
	public String getStrDeliveryBoyCode() {
		return strDeliveryBoyCode;
	}
	public void setStrDeliveryBoyCode(String strDeliveryBoyCode) {
		this.strDeliveryBoyCode = strDeliveryBoyCode;
	}
	public String getStrDeliveryBoyName() {
		return strDeliveryBoyName;
	}
	public void setStrDeliveryBoyName(String strDeliveryBoyName) {
		this.strDeliveryBoyName = strDeliveryBoyName;
	}
	public JSONArray getJsonArrForSettleButtons() {
		return jsonArrForSettleButtons;
	}
	public void setJsonArrForSettleButtons(JSONArray jsonArrForSettleButtons) {
		this.jsonArrForSettleButtons = jsonArrForSettleButtons;
	}
	
	
    public String getDteExpiryDate() {
		return dteExpiryDate;
	}
	public void setDteExpiryDate(String dteExpiryDate) {
		this.dteExpiryDate = dteExpiryDate;
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
	public List<clsItemsDtlsInBill> getListOfBillItemDtl() {
		return listOfBillItemDtl;
	}
	public void setListOfBillItemDtl(List<clsItemsDtlsInBill> listOfBillItemDtl) {
		this.listOfBillItemDtl = listOfBillItemDtl;
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
	public double getDblRefund() {
		return dblRefund;
	}
	public void setDblRefund(double dblRefund) {
		this.dblRefund = dblRefund;
	}
	public double getDblTotalTaxAmt() {
		return dblTotalTaxAmt;
	}
	public void setDblTotalTaxAmt(double dblTotalTaxAmt) {
		this.dblTotalTaxAmt = dblTotalTaxAmt;
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
	public String getStrKOTNo() {
		return strKOTNo;
	}
	public void setStrKOTNo(String strKOTNo) {
		this.strKOTNo = strKOTNo;
	}
	public String getStrTableNo() {
		return strTableNo;
	}
	public void setStrTableNo(String strTableNo) {
		this.strTableNo = strTableNo;
	}
	public String getStrWaiter() {
		return strWaiter;
	}
	public void setStrWaiter(String strWaiter) {
		this.strWaiter = strWaiter;
	}
	public int getIntPaxNo() {
		return intPaxNo;
	}
	public void setIntPaxNo(int intPaxNo) {
		this.intPaxNo = intPaxNo;
	}
	public double getStrDeditCardBalance() {
		return strDeditCardBalance;
	}
	public void setStrDeditCardBalance(double strDeditCardBalance) {
		this.strDeditCardBalance = strDeditCardBalance;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public JSONArray getJsonArrForMenuItemPricing() {
		return jsonArrForMenuItemPricing;
	}
	public void setJsonArrForMenuItemPricing(JSONArray jsonArrForMenuItemPricing) {
		this.jsonArrForMenuItemPricing = jsonArrForMenuItemPricing;
	}
	public JSONArray getJsonArrForMenuHeads() {
		return jsonArrForMenuHeads;
	}
	public void setJsonArrForMenuHeads(JSONArray jsonArrForMenuHeads) {
		this.jsonArrForMenuHeads = jsonArrForMenuHeads;
	}
	public JSONArray getJsonArrForTableDtl() {
		return jsonArrForTableDtl;
	}
	public void setJsonArrForTableDtl(JSONArray jsonArrForTableDtl) {
		this.jsonArrForTableDtl = jsonArrForTableDtl;
	}
	public JSONArray getJsonArrForWaiterDtl() {
		return jsonArrForWaiterDtl;
	}
	public void setJsonArrForWaiterDtl(JSONArray jsonArrForWaiterDtl) {
		this.jsonArrForWaiterDtl = jsonArrForWaiterDtl;
	}
	public JSONArray getJsonArrForButtonList() {
		return jsonArrForButtonList;
	}
	public void setJsonArrForButtonList(JSONArray jsonArrForButtonList) {
		this.jsonArrForButtonList = jsonArrForButtonList;
	}
	public JSONArray getJsonArrForTopModifierButton() {
		return jsonArrForTopModifierButton;
	}
	public void setJsonArrForTopModifierButton(JSONArray jsonArrForTopModifierButton) {
		this.jsonArrForTopModifierButton = jsonArrForTopModifierButton;
	}
	public JSONArray getJsonArrForModifiers() {
		return jsonArrForModifiers;
	}
	public void setJsonArrForModifiers(JSONArray jsonArrForModifiers) {
		this.jsonArrForModifiers = jsonArrForModifiers;
	}
	public List<clsMakeKotBillItemDtlBean> getListOfMakeKOTBillItemDtl() {
		return listOfMakeKOTBillItemDtl;
	}
	public void setListOfMakeKOTBillItemDtl(
			List<clsMakeKotBillItemDtlBean> listOfMakeKOTBillItemDtl) {
		this.listOfMakeKOTBillItemDtl = listOfMakeKOTBillItemDtl;
	}
	public String getStrSubGroupCode() {
		return strSubGroupCode;
	}
	public void setStrSubGroupCode(String strSubGroupCode) {
		this.strSubGroupCode = strSubGroupCode;
	}
	public String getStrGroupCode() {
		return strGroupCode;
	}
	public void setStrGroupCode(String strGroupCode) {
		this.strGroupCode = strGroupCode;
	}
	public double getDblRate() {
		return dblRate;
	}
	public void setDblRate(double dblRate) {
		this.dblRate = dblRate;
	}
	
}
