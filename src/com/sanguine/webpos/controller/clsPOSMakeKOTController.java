package com.sanguine.webpos.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.base.service.intfBaseService;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsDiscountDtlsOnBill;
import com.sanguine.webpos.bean.clsItemDtlForTax;
import com.sanguine.webpos.bean.clsItemsDtlsInBill;
import com.sanguine.webpos.bean.clsMakeKotBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSItemDetailFrTaxBean;
import com.sanguine.webpos.bean.clsPOSMakeKOTBean;
import com.sanguine.webpos.bean.clsPromotionItems;
import com.sanguine.webpos.bean.clsSettelementOptions;
import com.sanguine.webpos.bean.clsSettlementDtlsOnBill;
import com.sanguine.webpos.bean.clsTaxCalculationBean;
import com.sanguine.webpos.bean.clsTaxCalculationDtls;
import com.sanguine.webpos.bean.clsTaxDtlsOnBill;
import com.sanguine.webpos.bean.clsWebPOSBillSettlementBean;
import com.sanguine.webpos.model.clsBillDiscDtlModel;
import com.sanguine.webpos.model.clsBillDtlModel;
import com.sanguine.webpos.model.clsBillHdModel;
import com.sanguine.webpos.model.clsBillModifierDtlModel;
import com.sanguine.webpos.model.clsBillPromotionDtlModel;
import com.sanguine.webpos.model.clsBillSettlementDtlModel;
import com.sanguine.webpos.model.clsBillTaxDtl;
import com.sanguine.webpos.model.clsHomeDeliveryDtlModel;
import com.sanguine.webpos.model.clsHomeDeliveryHdModel;
import com.sanguine.webpos.model.clsMakeKOTHdModel;
import com.sanguine.webpos.model.clsMakeKOTModel_ID;
import com.sanguine.webpos.model.clsNonChargableKOTHdModel;
import com.sanguine.webpos.model.clsNonChargableKOTModel_ID;
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsSortMapOnValue;
import com.sanguine.webpos.util.clsTextFileGenerator;
import com.sanguine.webpos.util.clsUtilityController;

@Controller
public class clsPOSMakeKOTController
{

	@Autowired
	intfBaseService objBaseService;

	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;

	@Autowired
	private clsGlobalFunctions objGlobal;

	@Autowired
	private clsUtilityController objUtility;

	@Autowired
	private clsPOSSetupUtility objPOSSetupUtility;

	@Autowired
	clsTextFileGenerator objTextFileGeneration;

	String gGetWebserviceURL = (String) clsPOSGlobalFunctionsController.hmPOSSetupValues.get("GetWebserviceURL");
	String gOutletUID = (String) clsPOSGlobalFunctionsController.hmPOSSetupValues.get("OutletUID");;

	double taxAmt = 0.00;
	String globalTableNo = "",strCounterCode = "",gAreaCodeForTrans = "",
			clsAreaCode = "",gInrestoPOSIntegrationYN = "";
	ArrayList<String> ListTDHOnModifierItem = new ArrayList<>();
	ArrayList<Double> ListTDHOnModifierItemMaxQTY = new ArrayList<>();
	// String clientCode="",posCode="",posDate="",userCode="",posClientCode="";

	private Map<String, clsPromotionItems> hmPromoItem = new HashMap<String, clsPromotionItems>();
	JSONArray listReasonCode,listReasonName;

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;

	@RequestMapping(value = "/frmPOSRestaurantBill", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String clientCode = "", posCode = "", posDate = "", userCode = "", posClientCode = "";
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
			request.getSession().setAttribute("customerMobile", ""); // mobile
																		// no
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}

		clsWebPOSBillSettlementBean obBillSettlementBean = new clsWebPOSBillSettlementBean();
		clientCode = request.getSession().getAttribute("gClientCode").toString();
		posClientCode = request.getSession().getAttribute("gClientCode").toString();
		posCode = request.getSession().getAttribute("gPOSCode").toString();
		posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		userCode = request.getSession().getAttribute("gUserCode").toString();

		JSONArray jsonArrForTableDtl = funLoadTableDtl(clientCode, posCode);
		obBillSettlementBean.setJsonArrForTableDtl(jsonArrForTableDtl);

		JSONArray jsonArrForButtonList = funGetButttonList("MakeKOT", posCode, posClientCode);
		obBillSettlementBean.setJsonArrForButtonList(jsonArrForButtonList);

		JSONArray jsonArrForWaiterDtl = funGetWaiterList(posCode);
		obBillSettlementBean.setJsonArrForWaiterDtl(jsonArrForWaiterDtl);

		JSONArray jsonArrForMenuHeads = funGetMenuHeads(posCode, userCode);
		obBillSettlementBean.setJsonArrForMenuHeads(jsonArrForMenuHeads);

		JSONArray jsonArrForPopularItems = funGetPopularItem(clientCode, posDate, posCode);
		obBillSettlementBean.setJsonArrForPopularItems(jsonArrForPopularItems);

		JSONArray jsonArrForMenuItemPricing = funGetItemPricingDtl(clientCode, posDate, posCode);
		obBillSettlementBean.setJsonArrForMenuItemPricing(jsonArrForMenuItemPricing);

		model.put("gCheckDebitCardBalanceOnTrans", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("strCheckDebitCardBalOnTransactions"));
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCMSMemberCodeForKOTJPOS", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSMemberForKOTJPOS"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));
		model.put("gGetWebserviceURL", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("GetWebserviceURL"));
		model.put("gCustAddressSelectionForBill", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CustAddressSelectionForBill"));
		model.put("gOutletUID", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("OutletUID"));
		model.put("gPrintType", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("PrintType"));
		model.put("gSkipPax", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("SkipPax"));
		model.put("gSkipWaiter", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("SkipWaiter"));
		model.put("gSelectWaiterFromCardSwipe", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("SelectWaiterFromCardSwipe"));
		model.put("gMenuItemSortingOn", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("MenuItemSortingOn"));
		model.put("gMultiWaiterSelOnMakeKOT", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("MultiWaiterSelectionOnMakeKOT"));

		// ///Bill Settlement

		// ///////////Bill Settlement tab ////////////////////////////////////

		List listSettlementObject = new ArrayList<clsSettelementOptions>();
		try
		{
			String posURL = "";
			posURL = clsPOSGlobalFunctionsController.POSWSURL + "/WebPOSTransactions/funGetSettleButtons"
					+ "?posCode=" + posCode + "&userCode=" + userCode + "&clientCode=" + clientCode;
			JSONObject jObj1 = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);
			JSONArray jArr = new JSONArray();// (JSONArray)jObj.get("SettleDesc");
			Gson gson = new Gson();
			Type objectType = new TypeToken<clsSettelementOptions>()
			{
			}.getType();
			JSONObject jsSettelementOptionsDtl = (JSONObject) jObj1.get("SettleObj");

			Type listType = new TypeToken<List<clsSettelementOptions>>()
			{
			}.getType();
			listSettlementObject = gson.fromJson(jObj1.get("listSettleObj").toString(), listType);

			JSONObject jsSettle = new JSONObject();
			for (int j = 0; j < listSettlementObject.size(); j++)
			{
				// if(jArr.size()==listSettlementObject.size())
				// jsSettle.put(jArr.get(j).toString(),
				// listSettlementObject.get(j));
				jArr.add(listSettlementObject.get(j));
			}
			model.put("ObSettleObject", jsSettle);
			obBillSettlementBean.setJsonArrForSettleButtons(jArr);
			// objDirectBillerBean.setListOfBillItemDtl(listOfPunchedItemsDtl);
			obBillSettlementBean.setDteExpiryDate(posDate);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		String operationFrom = "MakeKOT";
		model.put("operationFrom", operationFrom);
		model.put("billDate", posDate);
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));
		String gPopUpToApplyPromotionsOnBill = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gPopUpToApplyPromotionsOnBill");
		model.put("gPopUpToApplyPromotionsOnBill", gPopUpToApplyPromotionsOnBill);
		model.put("gCreditCardSlipNo", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardSlipNoCompulsoryYN"));
		model.put("gCreditCardExpiryDate", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardExpiryDateCompulsoryYN"));

		funLoadAllReasonMasterData(request);

		model.put("listReasonCode", listReasonCode);
		model.put("listReasonName", listReasonName);

		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.setLength(0);
		sqlBuilder.append("select dblMaxDiscount ,strApplyDiscountOn from tblsetup where (strPOSCode='" + posCode + "'  OR strPOSCode='All') ");
		List list1 = null;
		try
		{
			list1 = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double gMaxDiscount = 0;
		String gApplyDiscountOn = "";
		if (list1 != null && list1.size() > 0)
		{
			Object[] obj = (Object[]) list1.get(0);
			gMaxDiscount = Double.parseDouble(obj[0].toString());
			gApplyDiscountOn = obj[1].toString();
		}

		model.put("urlHits", urlHits);

		return new ModelAndView("frmBillSettlementMakeKOT", "command", obBillSettlementBean);
	}

	public void funLoadAllReasonMasterData(HttpServletRequest request)
	{
		Map<String, String> mapModBill = new HashMap<String, String>();
		Map<String, String> mapComplementry = new HashMap<String, String>();
		Map<String, String> mapDiscount = new HashMap<String, String>();
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posURl = clsPOSGlobalFunctionsController.POSWSURL + "/WebPOSTransactions/funLoadAllReasonMasterData"
				+ "?clientCode=" + clientCode;
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURl);
		if (jObj != null)
		{
			JSONObject JObjBill = new JSONObject();
			// JSONObject JObjComplementry=new JSONObject();
			// JSONObject JObjDiscount=new JSONObject();
			JSONArray jArr = new JSONArray();

			jArr = (JSONArray) jObj.get("ModifyBill");
			if (jArr != null)
			{
				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					mapModBill.put(JObjBill.get("strReasonCode").toString(), JObjBill.get("strReasonName").toString());
				}
			}

			jArr = new JSONArray();
			jArr = (JSONArray) jObj.get("Complementry");
			if (jArr != null)
			{

				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					mapComplementry.put(JObjBill.get("strReasonCode").toString(), JObjBill.get("strReasonName").toString());
				}
			}
			jArr = new JSONArray();
			jArr = (JSONArray) jObj.get("Discount");
			if (jArr != null)
			{

				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					mapDiscount.put(JObjBill.get("strReasonCode").toString(), JObjBill.get("strReasonName").toString());
				}
			}
			listReasonCode = new JSONArray();
			listReasonName = new JSONArray();

			jArr = new JSONArray();
			jArr = (JSONArray) jObj.get("AllReason");
			if (jArr != null)
			{

				for (int i = 0; i < jArr.size(); i++)
				{
					JObjBill = (JSONObject) jArr.get(i);
					listReasonCode.add(JObjBill.get("strReasonCode"));
					listReasonName.add(JObjBill.get("strReasonName"));
				}
			}
		}
	}

	public JSONArray funLoadTableDtl(String clientCode, String posCode)
	{
		List list = null;
		String sql;
		Map<String, Integer> hmTableSeq = new HashMap<String, Integer>();
		JSONArray jArrData = new JSONArray();
		try
		{
			String gCMSIntegrationY = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");
			if (gCMSIntegrationY.equalsIgnoreCase("Y"))
			{
				String gTreatMemberAsTable = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gTreatMemberAsTable");
				if (gTreatMemberAsTable.equalsIgnoreCase("Y"))
				{
					sql = "select strTableNo,strTableName from tbltablemaster "
							+ " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
							+ " and strOperational='Y' and strStatus!='Normal' "
							+ " order by strTableName";
				}
				else
				{
					sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
							+ " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
							+ " and strOperational='Y' "
							+ " order by intSequence";
				}
			}
			else
			{
				sql = "select strTableNo,strTableName,intSequence from tbltablemaster "
						+ " where (strPOSCode='" + posCode + "' or strPOSCode='All') "
						+ " and strOperational='Y' "
						+ " order by intSequence";
			}

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");

			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					hmTableSeq.put(obj[0].toString() + "!" + obj[1].toString(), (int) obj[2]);

				}
			}
			clsSortMapOnValue objsort = new clsSortMapOnValue();
			hmTableSeq = objsort.funSortMapOnValues(hmTableSeq);
			Object[] arrObjTables = hmTableSeq.entrySet().toArray();
			jArrData = new JSONArray();
			for (int cntTable = 0; cntTable < hmTableSeq.size(); cntTable++)
			{
				if (cntTable == hmTableSeq.size())
				{
					break;
				}
				String tblInfo = arrObjTables[cntTable].toString().split("=")[0];
				String tblNo = tblInfo.split("!")[0];
				String tableName = tblInfo.split("!")[1];
				sql = "select strTableNo,strStatus,intPaxNo from tbltablemaster "
						+ " where strTableNo='" + tblNo + "' "
						+ " and strOperational='Y' "
						+ " order by intSequence";

				list = objBaseService.funGetList(new StringBuilder(sql), "sql");
				if (list.size() > 0)
				{
					Object[] obj = (Object[]) list.get(0);

					JSONObject jobj = new JSONObject();

					jobj.put("strTableName", tableName);
					jobj.put("strTableNo", obj[0].toString());
					jobj.put("strStatus", obj[1].toString());
					jobj.put("intPaxNo", obj[2].toString());
					jArrData.add(jobj);
				}
			}
			// jObjTableData.put("tableDtl",jArrData);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jArrData;
		}
	}

	public JSONArray funGetButttonList(String transName, String posCode, String posClientCode)
	{
		List list = null;
		JSONArray jArrData = new JSONArray();
		try
		{

			String sql = "select strButtonName from tblbuttonsequence where strTransactionName='" + transName + "' and (strPOSCode='All' or strPOSCode='" + posCode + "') and strClientCode='" + posClientCode + "' "
					+ "  order by intSeqNo ";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object obj = (Object) list.get(i);
					jArrData.add(obj.toString());
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jArrData;
		}
	}

	public JSONArray funGetWaiterList(String posCode)
	{
		List list = null;
		JSONArray jArrData = new JSONArray();
		try
		{

			String sql = "select strWaiterNo,strWShortName,strWFullName "
					+ " from tblwaitermaster where strOperational='Y' and (strPOSCode='All' or strPOSCode='" + posCode + "')  ";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					objSettle.put("strWaiterNo", obj[0].toString());
					objSettle.put("strWShortName", obj[1].toString());
					objSettle.put("strWFullName", obj[2].toString());
					jArrData.add(objSettle);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jArrData;
		}
	}

	public JSONArray funGetMenuHeads(String strPOSCode, String userCode)
	{
		LinkedHashMap<String, ArrayList<JSONObject>> mapBillHd;
		mapBillHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		JSONArray jArr = new JSONArray();
		List list = null;
		String strCounterWiseBilling = "";
		try
		{
			StringBuilder sql = new StringBuilder("select strCounterWiseBilling from tblposmaster");

			list = objBaseService.funGetList(sql, "sql");
			if (list.size() > 0)
				strCounterWiseBilling = (String) list.get(0);

			sql = new StringBuilder("select strCounterCode from tblcounterhd "
					+ " where strUserCode='" + userCode + "' ");
			list = objBaseService.funGetList(sql, "sql");
			if (list.size() > 0)
				strCounterCode = (String) list.get(0);

			if (strCounterWiseBilling.equalsIgnoreCase("Yes"))
			{

				sql = new StringBuilder("select distinct(a.strMenuCode),b.strMenuName "
						+ "from tblmenuitempricingdtl a left outer join tblmenuhd b on a.strMenuCode=b.strMenuCode "
						+ "left outer join tblcounterdtl c on b.strMenuCode=c.strMenuCode "
						+ "left outer join tblcounterhd d on c.strCounterCode=d.strCounterCode "
						+ "where d.strOperational='Yes' "
						+ "and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='ALL') "
						+ "and c.strCounterCode='" + strCounterCode + "' "
						+ "order by b.intSequence ");
			}
			else
			{
				sql = new StringBuilder("select distinct(a.strMenuCode),b.strMenuName "
						+ "from tblmenuitempricingdtl a left outer join tblmenuhd b "
						+ "on a.strMenuCode=b.strMenuCode "
						+ "where  b.strOperational='Y' "
						+ "and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='ALL') "
						+ "order by b.intSequence");
			}

			list = objBaseService.funGetList(sql, "sql");

			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strMenuName = obj[1].toString();//.replace(" ", "&#x00A;");
					objSettle.put("strMenuCode", obj[0].toString());
					objSettle.put("strMenuName", strMenuName);
					jArr.add(objSettle);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jArr;
	}

	public JSONArray funGetPopularItem(String clientCode, String posDate, String strPOSCode)
	{

		JSONArray jArr = new JSONArray();
		List list = null;
		try
		{
			String sql = "select strAreaCode from tblareamaster where strAreaName='All'";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list.size() > 0)
				gAreaCodeForTrans = (String) list.get(0);
			String gDirectAreaCode = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, strPOSCode, "gDirectAreaCode");

			sql = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
					+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
					+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
					+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable "
					+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
					+ " where a.strPopular='Y' and  a.strItemCode= b.strItemCode "
					+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
					+ " and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='All') "
					+ " and (a.strAreaCode='" + gDirectAreaCode + "' or a.strAreaCode='" + gAreaCodeForTrans + "') ";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strItemName = obj[1].toString();//.replace(" ", "&#x00A;");
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", strItemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());

					jArr.add(objSettle);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return jArr;
	}

	public JSONArray funGetItemPricingDtl(String clientCode, String posDate, String strPOSCode)
	{
		LinkedHashMap<String, ArrayList<JSONObject>> mapBillHd;
		mapBillHd = new LinkedHashMap<String, ArrayList<JSONObject>>();
		JSONArray jArr = new JSONArray();
		List list = null;
		String sql_ItemDtl;
		try
		{
			String gAreaWisePricing = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, strPOSCode, "gAreaWisePricing");
			if (gAreaWisePricing.equalsIgnoreCase("N"))
			{
				sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode ,b.strSubGroupCode,c.strGroupCode ,c.strSubGroupName,d.strGroupName "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b left outer join tblsubgrouphd c on b.strSubGroupCode=c.strSubGroupCode "
						+ " left outer join  tblgrouphd d  on c.strGroupCode= d.strGroupCode  "
						+ " WHERE  a.strItemCode=b.strItemCode "
						+ " and a.strAreaCode='" + gAreaCodeForTrans + "' "
						+ " and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='All') "
						+ " and date(dteFromDate)<='" + posDate + "' and date(dteToDate)>='" + posDate + "' "
						+ " ORDER BY b.strItemName ASC";
			}
			else
			{
				sql_ItemDtl = "SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate,b.strStockInEnable ,a.strMenuCode,b.strSubGroupCode,c.strGroupCode,c.strSubGroupName,d.strGroupName   "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b left outer join tblsubgrouphd c on b.strSubGroupCode=c.strSubGroupCode "
						+ " left outer join  tblgrouphd d  on c.strGroupCode= d.strGroupCode  "
						+ " WHERE a.strAreaCode='" + clsAreaCode + "' "
						+ "  and a.strItemCode=b.strItemCode "
						// + "WHERE (a.strAreaCode='" + clsAreaCode + "') "
						+ " and (a.strPosCode='" + strPOSCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
						+ " ORDER BY b.strItemName ASC";
			}
			list = objBaseService.funGetList(new StringBuilder(sql_ItemDtl), "sql");
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					String itemName = obj[1].toString();//.replace(" ", "&#x00A;");
					JSONObject objSettle = new JSONObject();
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", itemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());
					objSettle.put("strMenuCode", obj[20].toString());
					objSettle.put("strSubGroupCode", obj[21].toString());
					objSettle.put("strGroupcode", obj[22].toString());
					objSettle.put("strSubGroupName", obj[23].toString());
					objSettle.put("strGroupName", obj[24].toString());

					jArr.add(objSettle);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return jArr;
	}

	@RequestMapping(value = "/funLoadModifiers", method = RequestMethod.GET)
	public @ResponseBody JSONObject funLoadModifiers(@RequestParam("itemCode") String itemCode, HttpServletRequest request)
	{
		JSONObject jObj = new JSONObject();
		List list = null;

		try
		{

			String sql = "select a.strModifierName,a.strModifierCode"
					+ " ,b.dblRate,a.strModifierGroupCode,b.strDefaultModifier "
					+ " from tblmodifiermaster a,tblitemmodofier b "
					+ " where a.strModifierCode=b.strModifierCode "
					+ " and b.strItemCode='" + itemCode + "' "
					+ " group by a.strModifierCode;";
			list = objBaseService.funGetList(new StringBuilder(sql), "sql");

			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);
					JSONObject objSettle = new JSONObject();
					String strItemName = obj[0].toString();//.replace(" ", "&#x00A;");
					objSettle.put("strModifierName", obj[0].toString());
					objSettle.put("strModifierCode", obj[1].toString());
					objSettle.put("dblRate", obj[2]);
					objSettle.put("strModifierGroupCode", obj[3].toString());
					objSettle.put("strDefaultModifier", obj[4].toString());

					jArr.add(objSettle);
				}
			}
			jObj.put("Modifiers", jArr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jObj;
		}

	}

	@RequestMapping(value = "/funFillTopModifierButtonList", method = RequestMethod.GET)
	public @ResponseBody JSONObject funFillTopModifierButtonList(@RequestParam("itemCode") String itemCode, HttpServletRequest request)
	{
		List list = null;
		JSONObject jObj = new JSONObject();
		try
		{

			String sql = "select a.strModifierGroupCode,a.strModifierGroupShortName,a.strApplyMaxItemLimit,"
					+ "a.intItemMaxLimit,a.strApplyMinItemLimit,a.intItemMinLimit  from tblmodifiergrouphd a,tblmodifiermaster b,tblitemmodofier c "
					+ "where a.strOperational='YES' and a.strModifierGroupCode=b.strModifierGroupCode and "
					+ "b.strModifierCode=c.strModifierCode and c.strItemCode='" + itemCode + "' group by a.strModifierGroupCode";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strItemName = obj[1].toString();//.replace(" ", "&#x00A;");
					objSettle.put("strModifierGroupCode", obj[0].toString());
					objSettle.put("strModifierGroupShortName", strItemName);
					objSettle.put("strApplyMaxItemLimit", obj[2].toString());
					objSettle.put("intItemMaxLimit", obj[3].toString());
					objSettle.put("strApplyMinItemLimit", obj[4].toString());
					objSettle.put("intItemMinLimit", obj[5]);

					jArr.add(objSettle);
				}
			}
			jObj.put("topButtonModifier", jArr);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return jObj;
		}
	}

	@RequestMapping(value = "/funCalculateTax", method = RequestMethod.POST)
	public @ResponseBody String funCalculateTax(@RequestParam("arrKOTItemDtlList") List<String> arrKOTItemDtlList, HttpServletRequest request)
	{
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];

		String total = "";
		double amt = 0.00;
		StringBuilder sqlBuilder = new StringBuilder();
		List<clsItemDtlForTax> arrListItemDtls = new ArrayList<clsItemDtlForTax>();
		List list = null;
		try
		{
			for (int i = 0; i < arrKOTItemDtlList.size(); i++)
			{
				String itemDtl = arrKOTItemDtlList.get(i);
				String[] arrItemDtl = itemDtl.split("_");
				double dblAmount = Double.parseDouble(arrItemDtl[2]);
				if (dblAmount >= 0)
				{
					clsItemDtlForTax objItemDtl = new clsItemDtlForTax();
					objItemDtl.setItemCode(arrItemDtl[0]);
					objItemDtl.setItemName(arrItemDtl[1]);
					objItemDtl.setAmount(dblAmount);
					objItemDtl.setDiscAmt(0);
					arrListItemDtls.add(objItemDtl);

					amt += dblAmount;
				}
			}
			if (clsAreaCode.equals(""))
			{
				sqlBuilder.setLength(0);
				sqlBuilder.append("select strDirectAreaCode from tblsetup where (strPOSCode='" + posCode + "'  OR strPOSCode='All') and strClientCode='" + clientCode + "'");
				List listAreCode = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if (listAreCode.size() > 0)
				{
					for (int cnt = 0; cnt < listAreCode.size(); cnt++)
					{
						Object obj = (Object) listAreCode.get(cnt);
						clsAreaCode = (obj.toString());
					}
				}
			}
			String gCalculateTaxOnMakeKOT = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gCalculateTaxOnMakeKOT");
			if (gCalculateTaxOnMakeKOT.equalsIgnoreCase("Y"))
			{
				String dtPOSDate = posDate.split(" ")[0];
				List<clsTaxCalculationDtls> listTax = objUtility.funCalculateTax(arrListItemDtls, posCode, dtPOSDate, clsAreaCode, "DineIn", 0, 0, "");
				taxAmt = 0;
				for (clsTaxCalculationDtls objTaxDtl : listTax)
				{
					if (objTaxDtl.getTaxCalculationType().equalsIgnoreCase("Forward"))
					{
						taxAmt = taxAmt + objTaxDtl.getTaxAmount();
					}
				}
				amt += taxAmt;

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return String.valueOf(amt);
	}

	@RequestMapping(value = "/funChekReservation", method = RequestMethod.GET)
	public @ResponseBody JSONObject funChekReservation(@RequestParam("strTableNo") String strTableNo, HttpServletRequest request)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select a.strTableNo,a.strTableName,a.strStatus "
					+ "from tbltablemaster a "
					+ "where a.strTableNo='" + strTableNo + "' "
					+ "and a.strStatus='Reserve' ";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list.size() > 0)
			{
				sql = "select a.strResCode,a.strCustomerCode,b.strCustomerName,b.longMobileNo "
						+ "from tblreservation a,tblcustomermaster b "
						+ "where a.strTableNo='" + strTableNo + "' "
						+ "and a.strCustomerCode=b.strCustomerCode "
						+ "order by a.strResCode desc "
						+ "limit 1; ";

				list = objBaseService.funGetList(new StringBuilder(sql), "sql");
				if (list.size() > 0)
				{
					Object[] obj = (Object[]) list.get(0);
					jObjTableData.put("strCustomerCode", obj[1].toString());
					jObjTableData.put("strCustomerName", obj[2].toString());
					jObjTableData.put("MobileNo", obj[3]);
					jObjTableData.put("flag", true);

				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}

	}

	@RequestMapping(value = "/funFillMapWithHappyHourItems", method = RequestMethod.GET)
	public @ResponseBody JSONObject funFillMapWithHappyHourItems(HttpServletRequest request)
	{
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		String posDate = request.getSession().getAttribute("gPOSDate").toString();

		List list = null;
		StringBuilder sql = new StringBuilder();
		JSONObject jObjTableData = new JSONObject();

		try
		{
			String gAreaWisePricing = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
			if (gAreaWisePricing.equalsIgnoreCase("N"))
			{
				sql = new StringBuilder("SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday, "
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate"
						+ " ,b.strStockInEnable "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
						+ " WHERE a.strItemCode=b.strItemCode "
						+ " and a.strAreaCode='" + gAreaCodeForTrans + "' "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
						+ " and a.strHourlyPricing='Yes'");
			}
			else
			{
				sql = new StringBuilder("SELECT a.strItemCode,b.strItemName,a.strTextColor,a.strPriceMonday,a.strPriceTuesday,"
						+ " a.strPriceWednesday,a.strPriceThursday,a.strPriceFriday,"
						+ " a.strPriceSaturday,a.strPriceSunday,a.tmeTimeFrom,a.strAMPMFrom,a.tmeTimeTo,a.strAMPMTo,"
						+ " a.strCostCenterCode,a.strHourlyPricing,a.strSubMenuHeadCode,a.dteFromDate,a.dteToDate"
						+ ",b.strStockInEnable "
						+ " FROM tblmenuitempricingdtl a ,tblitemmaster b "
						+ " WHERE a.strAreaCode='" + gAreaCodeForTrans + "' "
						+ " and a.strItemCode=b.strItemCode "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' "
						+ " and a.strHourlyPricing='Yes'");
			}

			list = objBaseService.funGetList(sql, "sql");
			JSONArray jArrData = new JSONArray();
			JSONArray jArrItemCodeData = new JSONArray();
			if (list != null)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					String itemName = obj[1].toString();//.replace(" ", "&#x00A;");
					JSONObject objSettle = new JSONObject();
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", itemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());

					jArrData.add(objSettle);
					jArrItemCodeData.add(obj[0].toString());
				}
			}
			jObjTableData.put("ItemPriceDtl", jArrData);
			jObjTableData.put("ItemCode", jArrItemCodeData);

			jObjTableData.put("CurrentDate", objUtility.funGetCurrentDate());
			jObjTableData.put("CurrentTime", objUtility.funGetCurrentTime());
			jObjTableData.put("DayForPricing", objUtility.funGetDayForPricing());
			jObjTableData.put("ListTDHOnModifierItem", ListTDHOnModifierItem);
			jObjTableData.put("ListTDHOnModifierItemMaxQTY", ListTDHOnModifierItemMaxQTY);

			sql = new StringBuilder("select strPosCode,strPosName,strPosType,strDebitCardTransactionYN"
					+ " ,strPropertyPOSCode,strCounterWiseBilling,strDelayedSettlementForDB,strBillPrinterPort"
					+ " ,strAdvReceiptPrinterPort,strPrintVatNo,strPrintServiceTaxNo,strVatNo,strServiceTaxNo"
					+ " ,strEnableShift from tblposmaster");

			list = list = objBaseService.funGetList(sql, "sql");
			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);
				jObjTableData.put("gDebitCardPayment", obj[3].toString());
			}
			gInrestoPOSIntegrationYN = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gInrestoPOSIntegrationYN");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}

	}

	@RequestMapping(value = "/funChekCustomerDtl", method = RequestMethod.GET)
	public @ResponseBody JSONObject funChekCustomerDtl(@RequestParam("strTableNo") String strTableNo, HttpServletRequest request)
	{
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posCode = request.getSession().getAttribute("gPOSCode").toString();

		List list = null;
		globalTableNo = strTableNo;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			StringBuilder sql = new StringBuilder(" select a.strWaiterNo,a.intPaxNo,sum(a.dblAmount),a.strCardNo,if(a.strCustomerCode='null','',a.strCustomerCode) "
					+ ",ifnull(b.strCustomerName,''),ifnull(b.strBuldingCode,''),a.strHomeDelivery "
					+ " from tblitemrtemp a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
					+ " where a.strTableNo='" + strTableNo + "' and a.strPrintYN='Y' and a.strNCKotYN='N' "
					+ " group by a.strTableNo");

			list = objBaseService.funGetList(sql, "sql");
			if (list.size() > 0)
			{

				Object[] obj = (Object[]) list.get(0);
				jObjTableData.put("strWaiterNo", obj[0].toString());
				jObjTableData.put("intPaxNo", obj[1].toString());
				jObjTableData.put("strCustomerCode", obj[4].toString());
				jObjTableData.put("strCustomerName", obj[5].toString());
				jObjTableData.put("strHomeDelivery", obj[7].toString());
				jObjTableData.put("flag", true);

				sql = new StringBuilder("select strWaiterNo from tbltablemaster where strTableNo='" + strTableNo + "'");
				String waiterNo = "";
				list = objBaseService.funGetList(sql, "sql");
				if (list.size() > 0)
				{
					Object objTime = (Object) list.get(0);
					waiterNo = objTime.toString();
					jObjTableData.put("waiterNo", waiterNo);
				}
			}
			String gCMSIntegrationYN = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gCMSIntegrationYN");

			if (gCMSIntegrationYN.equalsIgnoreCase("Y"))
			{
				sql = new StringBuilder("select strCustomerCode ,strCustomerName "
						+ "from tblitemrtemp where strtableno = '" + strTableNo + "' and strCustomerCode <>'' ");
				list = objBaseService.funGetList(sql, "sql");
				if (list.size() > 0)
				{
					Object[] objTime = (Object[]) list.get(0);
					jObjTableData.put("CustomerCode", objTime[0].toString());
					jObjTableData.put("CustomerName", objTime[1].toString());
				}

			}
			sql = new StringBuilder("select a.strAreaCode,b.strAreaName from tbltablemaster a,tblareamaster b "
					+ "where a.strTableNo='" + strTableNo + "' and a.strAreaCode=b.strAreaCode");

			list = objBaseService.funGetList(sql, "sql");
			if (list.size() > 0)
			{
				Object[] objTime = (Object[]) list.get(0);
				clsAreaCode = objTime[0].toString();
				jObjTableData.put("AreaName", objTime[1].toString());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}

	}

	@RequestMapping(value = "/funChekCMSCustomerDtl", method = RequestMethod.GET)
	public @ResponseBody JSONObject funChekCMSCustomerDtl(@RequestParam("strTableNo") String strTableNo, HttpServletRequest request)
	{
		List list = null;
		JSONObject objSettle = new JSONObject();
		try
		{

			String sql = "select ifnull(sum(dblAmount),0),ifnull(strCustomerCode,''),ifnull(strCustomerName,'') "
					+ "from tblitemrtemp where strtableno = '" + strTableNo + "' and strCustomerCode <>'' ";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list != null)
			{
				Object[] obj = (Object[]) list.get(0);
				objSettle.put("dblAmount", obj[0]);
				objSettle.put("strCustomerCode", obj[1].toString());
				objSettle.put("strCustomerName", obj[2].toString());
				objSettle.put("flag", true);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}
	}

	@RequestMapping(value = "/funChekCardDtl", method = RequestMethod.GET)
	public @ResponseBody JSONObject funChekCardDtl(@RequestParam("strTableNo") String strTableNo, HttpServletRequest request)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = " select a.strWaiterNo,a.intPaxNo,sum(a.dblAmount),b.dblRedeemAmt,a.strCardNo "
					+ " from tblitemrtemp a,tbldebitcardmaster b "
					+ " where a.strCardNo=b.strCardNo and strTableNo='" + strTableNo + "' "
					+ " and strPrintYN='Y' and strNCKotYN='N' "
					+ " group by strTableNo";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);
				if ((double) obj[3] > 0)
				{
					double debitCardBal = (double) obj[3];
					debitCardBal = objUtility.funGetKOTAmtOnTable(obj[4].toString());

					jObjTableData.put("cardBalnce", debitCardBal);
					jObjTableData.put("kotAmt", (double) obj[2]);
					jObjTableData.put("balAmt", (double) obj[3]);
					jObjTableData.put("flag", true);
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	/* need cms data from cms webservice so this function was not changed */
	@RequestMapping(value = "/funCheckMemeberBalance", method = RequestMethod.GET)
	public @ResponseBody JSONObject funCheckMemeberBalance(@RequestParam("strCustomerCode") String strCustomerCode, HttpServletRequest request)
	{

		JSONObject jObj = objGlobal.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL + "/clsMakeKOTController/funCheckMemeberBalance?strCustomerCode=" + strCustomerCode);

		return jObj;
	}

	@RequestMapping(value = "/funFillOldKOTItems", method = RequestMethod.GET)
	public @ResponseBody JSONObject funFillOldKOTItems(@RequestParam("strTableNo") String strTableNo, HttpServletRequest request)
	{
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];

		double amt = 0.00;
		List<clsItemDtlForTax> arrListItemDtls = new ArrayList<clsItemDtlForTax>();
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			StringBuilder sql = new StringBuilder("select distinct(strKOTNo) "
					+ " from tblitemrtemp "
					+ " where (strPosCode='" + posCode + "' or strPosCode='All') "
					+ " and strTableNo='" + strTableNo + "' and strPrintYN='Y' and strNCKotYN='N' "
					+ " order by strKOTNo DESC");
			list = objBaseService.funGetList(sql, "sql");
			if (list.size() > 0)
			{
				JSONArray jArr = new JSONArray();
				for (int i = 0; i < list.size(); i++)
				{
					JSONObject jObjData = new JSONObject();

					String kotNo = (String) list.get(i);
					jObjData.put("kotNo", kotNo);

					String sqlKot = "select DATE_FORMAT(dteDateCreated,'%H:%i') from tblitemrtemp where strKOTNo='" + kotNo + "' limit 1";

					List timelist = objBaseService.funGetList(new StringBuilder(sqlKot), "sql");
					if (timelist.size() > 0)
					{
						Object objTime = (Object) timelist.get(0);
						jObjData.put("kotTime", objTime.toString());
					}
					jArr.add(jObjData);
				}
				jObjTableData.put("OldKOTTimeDtl", jArr);

				sql = new StringBuilder("SELECT a.strKOTNo,a.strTableNo,a.strWaiterNo,a.strItemName,a.strItemCode,a.dblItemQuantity, "
						+ " a.dblAmount,a.intPaxNo,a.strPrintYN,a.tdhComboItemYN,a.strSerialNo,a.strNcKotYN,a.dblRate ,ifNull(b.strSubGroupCode,''),ifNull(c.strGroupCode,'') ,ifNull(c.strSubGroupName,''),ifNull(d.strGroupName,'') "
						+ " FROM tblitemrtemp a left outer join tblitemmaster b on a.strItemCode=b.strItemCode  "
						+ " left outer join tblsubgrouphd c on b.strSubGroupCode=c.strSubGroupCode "
						+ " left outer join tblgrouphd d on c.strGroupCode=d.strGroupCode where strTableNo='" + strTableNo + "' "
						+ " and (strPosCode='" + posCode + "' or strPosCode='All') "
						+ " and strNcKotYN='N' "
						+ " order by strKOTNo desc ,strSerialNo");
				list = objBaseService.funGetList(sql, "sql");
				if (list.size() > 0)
				{
					boolean flag = false;
					JSONArray jArrData = new JSONArray();
					for (int i = 0; i < list.size(); i++)
					{
						Object[] obj = (Object[]) list.get(i);
						JSONObject objSettle = new JSONObject();
						BigDecimal dblAmount = (BigDecimal) obj[6];
						if (dblAmount.doubleValue() >= 0)
						{
							objSettle.put("strKOTNo", obj[0].toString());
							objSettle.put("strTableNo", obj[1].toString());
							objSettle.put("strWaiterNo", obj[2].toString());
							objSettle.put("strItemName", obj[3].toString());
							objSettle.put("strItemCode", obj[4].toString());
							objSettle.put("dblItemQuantity", obj[5]);
							objSettle.put("dblAmount", dblAmount.doubleValue());
							objSettle.put("intPaxNo", obj[7].toString());
							objSettle.put("strPrintYN", obj[8].toString());
							objSettle.put("tdhComboItemYN", obj[9].toString());
							objSettle.put("strSerialNo", obj[10].toString());
							objSettle.put("strNcKotYN", obj[11].toString());
							objSettle.put("dblRate", obj[12].toString());

							objSettle.put("strSubGroupCode", obj[13].toString());
							objSettle.put("strGroupcode", obj[14].toString());
							objSettle.put("strSubGroupName", obj[15].toString());
							objSettle.put("strGroupName", obj[16].toString());

							clsItemDtlForTax objItemDtl = new clsItemDtlForTax();
							objItemDtl.setItemCode(obj[4].toString());
							objItemDtl.setItemName(obj[3].toString());
							objItemDtl.setAmount(dblAmount.doubleValue());
							objItemDtl.setDiscAmt(0);
							arrListItemDtls.add(objItemDtl);
							amt += dblAmount.doubleValue();
							flag = true;
							jArrData.add(objSettle);
						}
					}
					jObjTableData.put("OldKOTItems", jArrData);
					jObjTableData.put("flag", flag);

					String gCalculateTaxOnMakeKOT = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gCalculateTaxOnMakeKOT");
					if (gCalculateTaxOnMakeKOT.equalsIgnoreCase("Y"))
					{
						String dtPOSDate = posDate.split(" ")[0];
						List<clsTaxCalculationDtls> listTax = objUtility.funCalculateTax(arrListItemDtls, posCode, dtPOSDate, clsAreaCode, "DineIn", 0, 0, "");
						taxAmt = 0;
						for (clsTaxCalculationDtls objTaxDtl : listTax)
						{
							if (objTaxDtl.getTaxCalculationType().equalsIgnoreCase("Forward"))
							{
								taxAmt = taxAmt + objTaxDtl.getTaxAmount();
							}
						}
						amt += taxAmt;
					}
					jObjTableData.put("Total", amt);
					jObjTableData.put("TaxTotal", taxAmt);
				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}

	}

	@RequestMapping(value = "/funCheckDebitCardString", method = RequestMethod.GET)
	public @ResponseBody JSONObject funCheckDebitCardString(@RequestParam("debitCardNo") String debitCardNo, HttpServletRequest request)
	{
		String cardString = funGetSingleTrackData(debitCardNo);
		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String waiterNo = "";
		JSONObject jObj = new JSONObject();
		try
		{

			String sql = "select strWaiterNo,strWShortName,strWFullName,strOperational "
					+ " from tblwaitermaster "
					+ " where strDebitCardString='" + debitCardNo + "' and (strPOSCode='All' or strPOSCode='" + posCode + "') ";
			List list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);
				if (!obj[3].toString().equals("N"))
				{
					waiterNo = obj[0].toString() + "#" + obj[1].toString();
				}
			}

			jObj.put("waiterNo", waiterNo);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			return jObj;
		}

	}

	@RequestMapping(value = "/funLoadPopularItems", method = RequestMethod.GET)
	public @ResponseBody JSONObject funPopularItem(HttpServletRequest request)
	{
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];

		JSONObject jObjTableData = new JSONObject();
		JSONArray jsonArrForPopularItems = funGetPopularItem(clientCode, posDate, posCode);
		jObjTableData.put("PopularItems", jsonArrForPopularItems);

		return jObjTableData;
	}

	@RequestMapping(value = "/funFillTopButtonList", method = RequestMethod.GET)
	public @ResponseBody JSONObject funFillTopButtonList(@RequestParam("menuHeadCode") String menuHeadCode, HttpServletRequest request)
	{

		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		String clientCode = request.getSession().getAttribute("gClientCode").toString();
		String posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		JSONObject jObj = new JSONObject();
		try
		{
			String sqlItems = "";
			String gMenuItemSortingOn = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gMenuItemSortingOn");
			if (gMenuItemSortingOn.equalsIgnoreCase("subgroupWise"))
			{
				sqlItems = "select c.strSubGroupName,b.strSubGroupCode "
						+ " from tblmenuitempricingdtl a,tblitemmaster b,tblsubgrouphd c "
						+ " where a.strItemCode=b.strItemCode "
						+ " and b.strSubGroupCode=c.strSubGroupCode "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' ";

				if (menuHeadCode.equalsIgnoreCase("Popular"))
				{
					sqlItems += " and a.strPopular='Y' and a.strAreaCode='" + clsAreaCode + "' "
							+ " group by c.strSubGroupCode ORDER by c.strSubGroupName";
				}
				else
				{
					sqlItems += " and a.strMenuCode='" + menuHeadCode + "' "
							+ " group by c.strSubGroupCode ORDER by c.strSubGroupName";
				}
			}
			else if (gMenuItemSortingOn.equalsIgnoreCase("subMenuHeadWise"))
			{
				sqlItems = "select b.strSubMenuHeadName,a.strSubMenuHeadCode "
						+ " from tblmenuitempricingdtl a left outer join tblsubmenuhead b "
						+ " on a.strSubMenuHeadCode=b.strSubMenuHeadCode and a.strMenuCode=b.strMenuCode "
						+ " where b.strSubMenuHeadName is not null and b.strSubMenuOperational='Y' "
						+ " and (a.strPosCode='" + posCode + "' or a.strPosCode='All') "
						+ " and date(a.dteFromDate)<='" + posDate + "' and date(a.dteToDate)>='" + posDate + "' ";

				if (menuHeadCode.equalsIgnoreCase("Popular"))
				{
					sqlItems += " and a.strPopular='Y' "
							+ "group by a.strSubMenuHeadCode";
				}
				else
				{
					sqlItems += " and a.strMenuCode='" + menuHeadCode + "' group by a.strSubMenuHeadCode";
				}
			}
			List list = objBaseService.funGetList(new StringBuilder(sqlItems), "sql");
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					objSettle.put("strCode", obj[1].toString());
					objSettle.put("strName", obj[0].toString());

					jArr.add(objSettle);
				}
			}
			jObj.put("topButtonList", jArr);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return jObj;
		}

	}

	@RequestMapping(value = "/funCheckHomeDelivery", method = RequestMethod.GET)
	public @ResponseBody JSONObject funCheckHomeDelivery(@RequestParam("strTableNo") String strTableNo, HttpServletRequest request)
	{

		String posCode = request.getSession().getAttribute("gPOSCode").toString();
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		try
		{

			String sql = "select a.strTableNo,ifnull(a.strCustomerCode,''),ifnull(b.strCustomerName,'ND')"
					+ " ,ifnull(b.strBuldingCode,''),ifnull(a.strDelBoyCode,'NA'),ifnull(c.strDPName,'NA') "
					+ " from tblitemrtemp a left outer join tblcustomermaster b on a.strCustomerCode=b.strCustomerCode "
					+ " left outer join tbldeliverypersonmaster c on a.strDelBoyCode=c.strDPCode "
					+ " where a.strHomeDelivery='Yes' and a.strTableNo='" + strTableNo + "' "
					+ " and a.strPOSCode='" + posCode + "' ";

			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list.size() > 0)
			{
				Object[] obj = (Object[]) list.get(0);
				jObjTableData.put("strCustomerCode", obj[1].toString());
				jObjTableData.put("strCustomerName", obj[2].toString());
				jObjTableData.put("strBuldingCode", obj[3].toString());
				jObjTableData.put("strDelBoyCode", obj[4].toString());
				jObjTableData.put("strDPName", obj[5].toString());
				jObjTableData.put("flag", true);

			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}

	}

	@RequestMapping(value = "/funCheckCustomer", method = RequestMethod.GET)
	public @ResponseBody JSONObject fuCkeckCustomer(@RequestParam("strMobNo") String strMobNo, HttpServletRequest request)
	{
		List list = null;
		JSONObject objSettle = new JSONObject();
		try
		{

			String sql = "select count(strCustomerCode) from tblcustomermaster where longMobileNo like '%" + strMobNo + "%'";
			list = objBaseService.funGetList(new StringBuilder(sql), "sql");

			if (list != null)
			{
				int cnt = ((BigInteger) list.get(0)).intValue();
				if (cnt > 0)
				{
					sql = "select strCustomerCode,strCustomerName,strBuldingCode "
							+ "from tblcustomermaster where longMobileNo like '%" + strMobNo + "%'";

					list = objBaseService.funGetList(new StringBuilder(sql), "sql");
					if (list != null)
					{
						Object[] obj = (Object[]) list.get(0);
						objSettle.put("strCustomerCode", obj[0].toString());
						objSettle.put("strCustomerName", obj[1].toString());
						objSettle.put("strBuldingCode", obj[2].toString());
						objSettle.put("flag", true);
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return objSettle;
		}

	}

	@RequestMapping(value = "/funCheckKOTSave", method = RequestMethod.GET)
	public @ResponseBody boolean funCheckKOTSave(@RequestParam("strKOTNo") String strKOTNo, HttpServletRequest request)
	{
		List list = null;
		boolean flag = false;

		try
		{

			String sql = "select strTableNo from tblitemrtemp where strTableNo='" + globalTableNo + "'";
			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list.size() > 0)
			{
				sql = "select strPrintYN from tblitemrtemp where strKOTNo='" + strKOTNo + "' "
						+ "and strTableNo='" + globalTableNo + "' and strPrintYN='N' group by  strPrintYN";

				list = objBaseService.funGetList(new StringBuilder(sql), "sql");
				if (list.size() > 0)
				{
					flag = true;
				}
			}
			else
			{

				flag = true;
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			return flag;
		}
	}

	@RequestMapping(value = "/funFillitemsSubMenuWise", method = RequestMethod.GET)
	public @ResponseBody JSONObject funFillitemsSubMenuWise(HttpServletRequest req)
	{
		String strMenuCode = req.getParameter("strMenuCode");
		String flag = req.getParameter("flag");
		String selectedButtonCode = req.getParameter("selectedButtonCode");

		String posCode = req.getSession().getAttribute("gPOSCode").toString();
		String clientCode = req.getSession().getAttribute("gClientCode").toString();
		String posDate = req.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		JSONObject jObj = new JSONObject();
		try
		{
			String sqlItems = "";
			String gMenuItemSortingOn = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gMenuItemSortingOn");
			if (gMenuItemSortingOn.equalsIgnoreCase("subgroupWise"))
			{
				sqlItems = "SELECT b.strItemCode,c.strItemName,b.strTextColor,b.strPriceMonday,b.strPriceTuesday,"
						+ "b.strPriceWednesday,b.strPriceThursday,b.strPriceFriday,  "
						+ "b.strPriceSaturday,b.strPriceSunday,b.tmeTimeFrom,b.strAMPMFrom,b.tmeTimeTo,b.strAMPMTo,"
						+ "b.strCostCenterCode,b.strHourlyPricing,b.strSubMenuHeadCode,b.dteFromDate,b.dteToDate,c.strStockInEnable "
						+ "FROM tblmenuhd a LEFT OUTER JOIN tblmenuitempricingdtl b ON a.strMenuCode = b.strMenuCode "
						+ "RIGHT OUTER JOIN tblitemmaster c ON b.strItemCode = c.strItemCode "
						+ "WHERE ";

				if (flag.equalsIgnoreCase("Popular"))
				{
					sqlItems += " b.strPopular = 'Y' and c.strSubGroupCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')   "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";

				}
				else
				{
					sqlItems += " a.strMenuCode = '" + strMenuCode + "' and c.strSubGroupCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')  "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";
				}
				String gAreaWisePricing = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
				if (gAreaWisePricing.equalsIgnoreCase("Y"))
				{
					sqlItems = sqlItems + " and (b.strAreaCode='" + gAreaCodeForTrans + "' or b.strAreaCode='" + clsAreaCode + "')";
				}
				sqlItems = sqlItems + " ORDER BY c.strItemName ASC";
			}
			else if (gMenuItemSortingOn.equalsIgnoreCase("subMenuHeadWise"))
			{
				sqlItems = "SELECT b.strItemCode,c.strItemName,b.strTextColor,b.strPriceMonday,b.strPriceTuesday,"
						+ "b.strPriceWednesday,b.strPriceThursday,b.strPriceFriday,  "
						+ "b.strPriceSaturday,b.strPriceSunday,b.tmeTimeFrom,b.strAMPMFrom,b.tmeTimeTo,b.strAMPMTo,"
						+ "b.strCostCenterCode,b.strHourlyPricing,b.strSubMenuHeadCode,b.dteFromDate,b.dteToDate,c.strStockInEnable "
						+ "FROM tblmenuitempricingdtl b,tblitemmaster c "
						+ "WHERE ";

				if (flag.equalsIgnoreCase("Popular"))
				{
					sqlItems += " b.strMenuCode = '" + selectedButtonCode + "' and b.strItemCode=c.strItemCode and b.strSubMenuHeadCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')  "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";
				}
				else
				{
					sqlItems += " b.strMenuCode = '" + strMenuCode + "' and b.strItemCode=c.strItemCode and b.strSubMenuHeadCode='" + selectedButtonCode + "' and (b.strPosCode='" + posCode + "' or b.strPosCode='All')   "
							+ " and date(b.dteFromDate)<='" + posDate + "' and date(b.dteToDate)>='" + posDate + "' ";

				}
				String gAreaWisePricing = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gAreaWisePricing");
				if (gAreaWisePricing.toString().equalsIgnoreCase("Y"))
				{
					sqlItems = sqlItems + " and (b.strAreaCode='" + gAreaCodeForTrans + "' or b.strAreaCode='" + clsAreaCode + "')";
				}
				sqlItems = sqlItems + " ORDER BY c.strItemName ASC";
			}
			List list = objBaseService.funGetList(new StringBuilder(sqlItems), "sql");
			JSONArray jArr = new JSONArray();
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] obj = (Object[]) list.get(i);

					JSONObject objSettle = new JSONObject();
					String strItemName = obj[1].toString();//.replace(" ", "&#x00A;");
					objSettle.put("strItemCode", obj[0].toString());
					objSettle.put("strItemName", strItemName);
					objSettle.put("strTextColor", obj[2].toString());
					objSettle.put("strPriceMonday", obj[3].toString());
					objSettle.put("strPriceTuesday", obj[4].toString());
					objSettle.put("strPriceWednesday", obj[5]);

					objSettle.put("strPriceThursday", obj[6].toString());
					objSettle.put("strPriceFriday", obj[7].toString());
					objSettle.put("strPriceSaturday", obj[8].toString());
					objSettle.put("strPriceSunday", obj[9].toString());
					objSettle.put("tmeTimeFrom", obj[10].toString());
					objSettle.put("strAMPMFrom", obj[11].toString());
					objSettle.put("tmeTimeTo", obj[12].toString());
					objSettle.put("strAMPMTo", obj[13].toString());
					objSettle.put("strCostCenterCode", obj[14].toString());
					objSettle.put("strHourlyPricing", obj[15].toString());
					objSettle.put("strSubMenuHeadCode", obj[16].toString());
					objSettle.put("dteFromDate", obj[17].toString());
					objSettle.put("dteToDate", obj[18].toString());
					objSettle.put("strStockInEnable", obj[19].toString());

					jArr.add(objSettle);
				}
			}

			jObj.put("SubMenuWiseItemList", jArr);
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			return jObj;
		}

	}

	@RequestMapping(value = "/funGenerateKOTNo", method = RequestMethod.GET)
	public @ResponseBody JSONObject funGenerateKOTNo(HttpServletRequest request)
	{
		List list = null;
		JSONObject jObjTableData = new JSONObject();
		String kotNo = "";
		try
		{
			long code = 0;
			String sql = "select dblLastNo from tblinternal where strTransactionType='KOTNo'";
			list = objBaseService.funGetList(new StringBuilder(sql), "sql");

			if (list.size() > 0)
			{
				code = ((BigInteger) list.get(0)).longValue();
				code = code + 1;
				kotNo = "KT" + String.format("%07d", code);
			}
			else
			{
				kotNo = "KT0000001";
			}
			jObjTableData.put("strKOTNo", kotNo);

			sql = "update tblinternal set dblLastNo='" + code + "' where strTransactionType='KOTNo'";

			objBaseService.funExecuteUpdate(sql, "sql");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return jObjTableData;
		}
	}

	private String funGetSingleTrackData(String cardString)
	{
		String cardNo = "";
		if (cardString.contains("?") || cardString.contains(";"))
		{
			if (cardString.length() > 0)
			{
				StringBuilder sb = new StringBuilder(cardString);
				int percIndex = sb.indexOf("%");
				String allTracks = "";
				if (sb.toString().contains("?"))
				{
					allTracks = sb.substring(percIndex, sb.lastIndexOf("?") + 1);
				}
				else
				{
					allTracks = sb.toString();
				}
				String[] arrText = allTracks.split(";");
				String track1 = "", track2 = "", track3 = "";

				if (arrText.length > 0)
				{
					if (sb.toString().contains("?"))
					{
						track1 = arrText[0].substring(1, arrText[0].indexOf("?")).replaceAll("%", "");
						if (arrText.length > 1)
						{
							track2 = arrText[1].substring(1, arrText[1].indexOf("?")).replaceAll("%", "");
						}
						if (arrText.length > 2)
						{
							track3 = arrText[2].substring(1, arrText[2].indexOf("?")).replaceAll("%", "");
						}
					}
					else
					{
						track1 = arrText[0].replaceAll("%", "");
						track2 = arrText[1].replaceAll("%", "");
						track3 = arrText[2].replaceAll("%", "");
					}
				}

				if (!track1.isEmpty())
				{
					cardNo = track1;
				}
				else if (!track2.isEmpty())
				{
					cardNo = track2;
				}
				else if (!track3.isEmpty())
				{
					cardNo = track2;
				}

			}
		}
		else
		{
			cardNo = cardString;
		}

		return cardNo;
	}

	// on direct biller action performed
	// @RequestMapping(value = "/saveKOT", method = RequestMethod.POST)
	// public ModelAndView funSaveKOT(@ModelAttribute("command") @Valid
	// clsPOSMakeKOTBean objBean,BindingResult result,HttpServletRequest
	// req,@RequestParam("ncKot") String strNCKotYN,@RequestParam("takeAway")
	// String strTakeAwayYesNo,@RequestParam("globalDebitCardNo") String
	// globalDebitCardNo,@RequestParam("cmsMemCode") String
	// cmsMemCode,@RequestParam("cmsMemName") String
	// cmsMemName,@RequestParam("reasonCode") String
	// reasonCode,@RequestParam("homeDeliveryForTax") String
	// homeDeliveryForTax,@RequestParam("arrListHomeDelDetails") List<String>
	// arrListHomeDelDetails)
	// {
	@RequestMapping(value = "/saveKOT", method = RequestMethod.POST, headers =
	{ "Content-type=application/json" })
	private @ResponseBody String funSaveKOT(@RequestBody List listItmeDtl, HttpServletRequest req, @RequestParam("ncKot") String strNCKotYN, @RequestParam("takeAway") String strTakeAwayYesNo, @RequestParam("globalDebitCardNo") String globalDebitCardNo, @RequestParam("cmsMemCode") String cmsMemCode, @RequestParam("cmsMemName") String cmsMemName, @RequestParam("reasonCode") String reasonCode, @RequestParam("homeDeliveryForTax") String homeDeliveryForTax, @RequestParam("total") double total, @RequestParam("arrListHomeDelDetails") List<String> arrListHomeDelDetails, @RequestParam("custcode") String custcode, @RequestParam("custName") String custName) throws Exception
	{
		String result = "true";
		try
		{

			String posCode = req.getSession().getAttribute("gPOSCode").toString();
			String webStockUserCode = req.getSession().getAttribute("usercode").toString();
			String clientCode = req.getSession().getAttribute("gClientCode").toString();
			String posDate = req.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
			String strTableNo = "";
			int strPaxNo = 0;
			String strKOTNo = "";
			String strWaiterNo = "";

			Date dt = new Date();
			String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
			String dateTime = posDate + " " + currentDateTime.split(" ")[1];

			String homeDelivery = "No", strDelBoyCode = "", strCounterCode = "";
			double rate = 0;
			String sqlCounter = "select strCounterCode from tblcounterhd "
					+ " where strUserCode='" + webStockUserCode + "' ";
			List list = objBaseService.funGetList(new StringBuilder(sqlCounter), "sql");
			if (list.size() > 0)
				strCounterCode = (String) list.get(0);

			Map<String, String> hmSerNoForItems = new HashMap<String, String>();
			for (int cnt = 0; cnt < listItmeDtl.size(); cnt++)
			{
				Map listItemDtl = (Map) listItmeDtl.get(cnt);

				String strItemName = listItemDtl.get("itemName").toString();
				double dblQuantity = Double.parseDouble(listItemDtl.get("quantity").toString());
				double dblAmount = Double.parseDouble(listItemDtl.get("amount").toString());
				String strSubGroupCode = listItemDtl.get("strSubGroupCode").toString();
				String strGroupCode = listItemDtl.get("strGroupcode").toString();
				String strItemCode = listItemDtl.get("itemCode").toString();

				strTableNo = listItemDtl.get("tableNo").toString();
				strPaxNo = Integer.parseInt(listItemDtl.get("PaxNo").toString());
				strKOTNo = listItemDtl.get("kotNo").toString();
				strWaiterNo = listItemDtl.get("WaiterNo").toString();

				int serNo = cnt + 1;
				/*
				 * int serNo=1; if(hmSerNoForItems.containsKey(strItemName)) {
				 * if(strItemName.contains("-->")) {
				 * serNo=Integer.parseInt(hmSerNoForItems.get(strItemName));
				 * serNo++; } else {
				 * serNo=Integer.parseInt(hmSerNoForItems.get(strItemName));
				 * serNo++; } } hmSerNoForItems.put(strItemName,
				 * String.valueOf(serNo));
				 */

				clsMakeKOTHdModel objModel = new clsMakeKOTHdModel(new clsMakeKOTModel_ID(String.valueOf(serNo), strTableNo, strItemCode, strItemName, strKOTNo));

				objModel.setStrActiveYN("");
				objModel.setStrCardNo("");
				objModel.setStrCardType(" ");
				objModel.setStrCounterCode(strCounterCode);
				objModel.setStrCustomerCode(custcode);
				objModel.setStrCustomerName(custName);

				if (homeDeliveryForTax.equalsIgnoreCase("Y"))
				{
					if (arrListHomeDelDetails.get(2).toString().equals("HomeDelivery"))
					{
						homeDelivery = "Yes";
					}
				}
				if (homeDelivery == "Yes")
				{
					strDelBoyCode = arrListHomeDelDetails.get(4);
				}
				objModel.setStrDelBoyCode(strDelBoyCode);
				objModel.setStrHomeDelivery(homeDelivery);

				objModel.setStrManualKOTNo(" ");
				objModel.setStrNCKotYN(strNCKotYN);
				objModel.setStrOrderBefore(" ");
				objModel.setStrPOSCode(posCode);
				objModel.setStrPrintYN("N");
				objModel.setStrPromoCode(" ");
				objModel.setStrReason(reasonCode);
				objModel.setStrWaiterNo(strWaiterNo);
				objModel.setStrTakeAwayYesNo(strTakeAwayYesNo);
				objModel.setDblAmount(dblAmount);
				objModel.setDblBalance(0.00);
				objModel.setDblCreditLimit(0.00);
				objModel.setDblItemQuantity(dblQuantity);
				rate = dblAmount / dblQuantity;
				objModel.setDblRate(rate);
				objModel.setDblRedeemAmt(0);
				objModel.setDblTaxAmt(taxAmt);
				objModel.setIntId(0);
				objModel.setIntPaxNo(strPaxNo);

				objModel.setDteDateCreated(dateTime);
				objModel.setDteDateEdited(dateTime);

				objModel.setStrUserCreated(webStockUserCode);
				objModel.setStrUserEdited(webStockUserCode);

				objBaseService.funSave(objModel);
				if ("Y".equals(strNCKotYN))
				{

					clsNonChargableKOTHdModel objNCModel = new clsNonChargableKOTHdModel(new clsNonChargableKOTModel_ID(strTableNo, strItemCode, strKOTNo));

					objNCModel.setDblQuantity(dblQuantity);
					objNCModel.setDblRate(dblAmount / dblQuantity);
					objNCModel.setDteNCKOTDate(dateTime);
					objNCModel.setStrClientCode(clientCode);
					objNCModel.setStrDataPostFlag("Y");
					objNCModel.setStrEligibleForVoid("Y");
					objNCModel.setStrPOSCode(posCode);
					objNCModel.setStrReasonCode(reasonCode);
					objNCModel.setStrRemark("");
					objNCModel.setStrUserCreated(webStockUserCode);
					objNCModel.setStrUserEdited(webStockUserCode);

					objBaseService.funSave(objNCModel);

				}
			}
			funUpdateKOT(dateTime, strKOTNo, strTableNo, cmsMemCode, homeDelivery, strNCKotYN, strPaxNo, total);
			funInsertIntoTblItemRTempBck(strTableNo, strKOTNo);

		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
		return result;

	}

	public void funUpdateKOT(String dateTime, String strKOTNo, String strTableNo,
			String strCustomerCode, String strHomeDelivery, String strNCKotYN, int intPaxNo, double KOTAmt)
	{

		try
		{

			String sql = "insert into tblkottaxdtl "
					+ "values ('" + strTableNo + "','" + strKOTNo + "'," + KOTAmt + "," + taxAmt + ")";

			objBaseService.funExecuteUpdate(sql, "sql");

			String sql_update = "update tblitemrtemp set strPrintYN='Y',dteDateCreated='" + dateTime + "' "
					+ "where strKOTNo='" + strKOTNo + "' and strTableNo='" + strTableNo + "'";

			objBaseService.funExecuteUpdate(sql_update, "sql");

			if (strHomeDelivery.equals("Yes"))
			{
				sql = "update tblitemrtemp set strHomeDelivery='Yes',strCustomerCode='" + strCustomerCode + "' "
						+ "where strTableNo='" + strTableNo + "'";
				objBaseService.funExecuteUpdate(sql, "sql");
			}

			else
			{
				sql = "update tblitemrtemp set strHomeDelivery='No',strCustomerCode='" + strCustomerCode + "' "
						+ "where strTableNo='" + strTableNo + "'";
				objBaseService.funExecuteUpdate(sql, "sql");
			}
			sql = "update tbldebitcardtabletemp set strPrintYN='Y' where strTableNo='" + strTableNo + "'";
			objBaseService.funExecuteUpdate(sql, "sql");

			if ("Y".equals(strNCKotYN))
			{

				sql = "update tbltablemaster set strStatus='Normal' "
						+ " where strTableNo='" + strTableNo + "' and strStatus='Normal' ";
				objBaseService.funExecuteUpdate(sql, "sql");
			}
			else
			{

				sql = "update tbltablemaster set strStatus='Occupied' where strTableNo='" + strTableNo + "'";
				objBaseService.funExecuteUpdate(sql, "sql");
			}

			sql = "update tbltablemaster set intPaxNo='" + intPaxNo + "' where strTableNo='" + strTableNo + "'";
			objBaseService.funExecuteUpdate(sql, "sql");

			/*
			 * if(gInrestoPOSIntegrationYN.equalsIgnoreCase("Y")) {
			 * 
			 * }
			 */
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void funInsertIntoTblItemRTempBck(String tableNo, String kotNo)
	{
		try
		{
			String sql = "delete from tblitemrtemp_bck where strTableNo='" + tableNo + "' and strKOTNo='" + kotNo + "' ";
			objBaseService.funExecuteUpdate(sql, "sql");

			sql = "insert into tblitemrtemp_bck (select * from tblitemrtemp where strTableNo='" + tableNo + "' and strKOTNo='" + kotNo + "' )";
			objBaseService.funExecuteUpdate(sql, "sql");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	@RequestMapping(value = "/funCallWebService", method = RequestMethod.GET)
	public @ResponseBody JSONObject funCallWebService(@RequestParam("strMobNo") String strMobNo, HttpServletRequest request)
	{
		String strCustomerCode = "";
		List list = null;
		JSONObject obj = new JSONObject();
		try
		{
			String sql = "select strCustomerCode from tblcustomermaster where longMobileNo=" + strMobNo;
			list = objBaseService.funGetList(new StringBuilder(sql), "sql");
			if (list != null)
			{
				strCustomerCode = (String) list.get(0).toString();
			}

			DefaultHttpClient httpClient = new DefaultHttpClient();
			String getWebServiceURL = gGetWebserviceURL;
			getWebServiceURL += "" + strMobNo + "/outlet/" + gOutletUID + "/";
			HttpGet getRequest = new HttpGet(getWebServiceURL);
			HttpResponse response = httpClient.execute(getRequest);
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output = "", op = "";

			while ((output = br.readLine()) != null)
			{
				op += output;
			}
			// System.out.println(op);
			JSONParser p = new JSONParser();
			Object objJSON = p.parse(op);
			obj = (JSONObject) objJSON;

			return obj;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
		finally
		{
			return obj;
		}
	}

	// ///////Promotion Calculation

	@RequestMapping(value = "/promotionCalculateForKOT", method = RequestMethod.POST, headers =
	{ "Content-type=application/json" })
	private @ResponseBody Map funPromotionCalculate(@RequestBody List listItmeDtl, HttpServletRequest request) throws Exception
	{
		String clientCode = "", posCode = "", posDate = "", userCode = "", posClientCode = "";
		String areaCode = "";
		double dblDiscountAmt = 0.0;
		StringBuilder sqlBuilder = new StringBuilder();
		if (areaCode.equals(""))
		{
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strDirectAreaCode from tblsetup where (strPOSCode='" + posCode + "'  OR strPOSCode='All') and strClientCode='" + clientCode + "'");
			List listAreCode = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
			if (listAreCode.size() > 0)
			{
				for (int cnt = 0; cnt < listAreCode.size(); cnt++)
				{
					Object obj = (Object) listAreCode.get(cnt);
					areaCode = (obj.toString());
				}
			}
		}

		Map mapResult = new HashMap();
		String checkPromotion = "N";
		List<clsWebPOSBillSettlementBean> listofItems = new ArrayList<clsWebPOSBillSettlementBean>();
		for (int cnt = 0; cnt < listItmeDtl.size(); cnt++)
		{
			Map listItemDtl = (Map) listItmeDtl.get(cnt);
			System.out.println(listItemDtl.get("itemName"));
			JSONObject objRows = new JSONObject();
			if (listItemDtl.get("itemCode") != null)
			{
				clsWebPOSBillSettlementBean objWebPOSBillSettlementBean = new clsWebPOSBillSettlementBean();
				objWebPOSBillSettlementBean.setStrItemCode(listItemDtl.get("itemCode").toString());
				objWebPOSBillSettlementBean.setStrItemName(listItemDtl.get("itemName").toString());
				objWebPOSBillSettlementBean.setDblQuantity(Double.parseDouble(listItemDtl.get("quantity").toString()));
				objWebPOSBillSettlementBean.setDblAmount(Double.parseDouble(listItemDtl.get("amount").toString()));
				objWebPOSBillSettlementBean.setStrSubGroupCode(listItemDtl.get("strSubGroupCode").toString());
				objWebPOSBillSettlementBean.setStrGroupCode(listItemDtl.get("strGroupcode").toString());
				objWebPOSBillSettlementBean.setStrItemCode(listItemDtl.get("itemCode").toString());
				listofItems.add(objWebPOSBillSettlementBean);
			}
		}

		Map<String, clsPromotionItems> hmPromoItemDtl = null;
		boolean flgApplyPromoOnBill = false;
		String gActivePromotions = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gActivePromotions");
		String gPopUpToApplyPromotionsOnBill = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gPopUpToApplyPromotionsOnBill");
		if (gActivePromotions.equalsIgnoreCase("Y"))
		{
			hmPromoItemDtl = objUtility.funCalculatePromotions("DirectBiller", "", "", new ArrayList(), "", areaCode, "", request, listofItems);
			if (null != hmPromoItemDtl)
			{
				if (hmPromoItemDtl.size() > 0)
				{
					checkPromotion = "Y";
					flgApplyPromoOnBill = true;
				}
			}
		}

		Map<String, clsPromotionItems> hmPromoItemsToDisplay = new HashMap<String, clsPromotionItems>();

		List<clsItemDtlForTax> arrListItemDtls = new ArrayList<clsItemDtlForTax>();
		List<clsWebPOSBillSettlementBean> listOfPromotionItem = new ArrayList<clsWebPOSBillSettlementBean>();
		Map<String, clsWebPOSBillSettlementBean> mapPromoItemDisc = new HashMap<String, clsWebPOSBillSettlementBean>();
		// Make KOT

		for (int cnt = 0; cnt < listItmeDtl.size(); cnt++)
		{
			Map listItemDtl = (Map) listItmeDtl.get(cnt);
			double freeAmount = 0.00;
			String item = listItemDtl.get("itemName").toString().trim();

			double quantity = Double.parseDouble(listItemDtl.get("quantity").toString());
			double amount = Double.parseDouble(listItemDtl.get("amount").toString());
			String groupCode = listItemDtl.get("strGroupcode").toString().trim();
			String subGroupCode = listItemDtl.get("strSubGroupCode").toString().trim();
			int i = item.compareTo(groupCode);
			double rate = Double.parseDouble(listItemDtl.get("rate").toString().trim());
			String itemCode = listItemDtl.get("itemCode").toString();

			clsItemDtlForTax objItemDtlForTax = new clsItemDtlForTax();
			objItemDtlForTax.setItemCode(itemCode);
			objItemDtlForTax.setItemName(item);
			objItemDtlForTax.setAmount(0);
			objItemDtlForTax.setDiscAmt(0);

			if (gActivePromotions.equalsIgnoreCase("Y") && flgApplyPromoOnBill)
			{
				if (null != hmPromoItemDtl)
				{
					if (hmPromoItemDtl.containsKey(itemCode))
					{
						if (null != hmPromoItemDtl.get(itemCode))
						{
							clsPromotionItems objPromoItemsDtl = hmPromoItemDtl.get(itemCode);
							if (objPromoItemsDtl.getPromoType().equals("ItemWise"))
							{
								double freeQty = objPromoItemsDtl.getFreeItemQty();
								if (freeQty > 0)
								{
									freeAmount = freeAmount + (rate * freeQty);
									amount = amount - freeAmount;
									hmPromoItem.put(itemCode, objPromoItemsDtl);
									hmPromoItemsToDisplay.put(itemCode + "!" + item, objPromoItemsDtl);
									hmPromoItemDtl.remove(itemCode);
								}
							}
							else if (objPromoItemsDtl.getPromoType().equals("Discount"))
							{
								double discA = 0;
								double discP = 0;
								if (objPromoItemsDtl.getDiscType().equals("Value"))
								{
									discA = objPromoItemsDtl.getDiscAmt();
									discP = (discA / amount) * 100;
									hmPromoItem.put(itemCode, objPromoItemsDtl);
									hmPromoItemsToDisplay.put(itemCode + "!" + item, objPromoItemsDtl);
									hmPromoItemDtl.remove(itemCode);

									clsWebPOSBillSettlementBean objItemPromoDiscount = new clsWebPOSBillSettlementBean();
									objItemPromoDiscount.setStrItemCode(itemCode);
									objItemPromoDiscount.setStrItemName(item);
									objItemPromoDiscount.setDblDiscountAmt(discA);
									objItemPromoDiscount.setDblDiscountPer(discP);
									objItemPromoDiscount.setDblAmount(amount);

									mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
								}
								else
								{
									discP = objPromoItemsDtl.getDiscPer();
									discA = (discP / 100) * amount;
									// hmPromoItem.put(itemCode,
									// objPromoItemsDtl);
									hmPromoItemDtl.remove(itemCode);
									clsWebPOSBillSettlementBean objItemPromoDiscount = new clsWebPOSBillSettlementBean();
									objItemPromoDiscount.setStrItemCode(itemCode);
									objItemPromoDiscount.setStrItemName(item);
									objItemPromoDiscount.setDblDiscountAmt(discA);
									objItemPromoDiscount.setDblDiscountPer(discP);
									objItemPromoDiscount.setDblAmount(amount);

									mapPromoItemDisc.put(itemCode, objItemPromoDiscount);
								}
							}
						}
					}
				}
			}

			// temp_Total += amount;
			objItemDtlForTax.setAmount(objItemDtlForTax.getAmount() + amount);
			arrListItemDtls.add(objItemDtlForTax);

			// listItemCode.add(itemCode);
			// hmItemList.put(item, itemCode);

			if (gActivePromotions.equalsIgnoreCase("Y") && flgApplyPromoOnBill)
			{
				double discAmt = 0;
				double discPer = 0;
				if (mapPromoItemDisc.containsKey(itemCode))
				{
					clsWebPOSBillSettlementBean objItemPromoDiscount = mapPromoItemDisc.get(itemCode);
					// discAmt = objItemPromoDiscount.getDiscountAmt();
					// discPer = objItemPromoDiscount.getDiscountPer();

					objItemPromoDiscount.setDblQuantity(quantity);
					objItemPromoDiscount.setStrSubGroupCode(subGroupCode);
					objItemPromoDiscount.setStrGroupCode(groupCode);
					objItemPromoDiscount.setDblRate(rate);
					listOfPromotionItem.add(objItemPromoDiscount);
					// funFillListForItemRow(item, quantity, amount, itemCode,
					// discAmt, discPer);
					dblDiscountAmt = dblDiscountAmt + discAmt;
					// txtDiscountAmt.setText(String.valueOf(dblDiscountAmt));
					// txtDiscountPer.setText(String.valueOf(discPer));
				}
				else
				{
					clsWebPOSBillSettlementBean objItemPromoDiscount = new clsWebPOSBillSettlementBean();
					objItemPromoDiscount.setStrItemCode(itemCode);
					objItemPromoDiscount.setStrItemName(item);
					objItemPromoDiscount.setDblDiscountAmt(discAmt);
					objItemPromoDiscount.setDblDiscountPer(discPer);
					objItemPromoDiscount.setDblAmount(amount);
					objItemPromoDiscount.setDblQuantity(quantity);
					objItemPromoDiscount.setStrSubGroupCode(subGroupCode);
					objItemPromoDiscount.setStrGroupCode(groupCode);
					objItemPromoDiscount.setDblRate(rate);
					listOfPromotionItem.add(objItemPromoDiscount);
					// funFillListForItemRow(item, quantity, amount, itemCode,
					// discAmt, discPer);
					// dblDiscountAmt = dblDiscountAmt + discAmt;
					// txtDiscountAmt.setText(String.valueOf(dblDiscountAmt));
					// txtDiscountPer.setText(String.valueOf(discPer));
				}

				// Iterator<Map.Entry<String, clsWebPOSBillSettlementBean>>
				// itPromoDisc = mapPromoItemDisc.entrySet().iterator();
				// while (clsWebPOSBillSettlementBean listOfPromotionItem)
				// {
				// clsWebPOSBillSettlementBean objItemDtl =
				// itPromoDisc.next().getValue();
				// if (mapPromoItemDisc.containsKey(objItemDtl.getItemCode()))
				// {
				// mapBillDiscDtl.put("ItemWise!" + objItemDtl.getItemName() +
				// "!P", new clsBillDiscountDtl("Promotion Discount", "R01",
				// objItemDtl.getDiscountPercentage(),
				// objItemDtl.getDiscountAmount(), objItemDtl.getAmount()));
				// }
				// }
			}
			else
			{
				checkPromotion = "N";
				clsWebPOSBillSettlementBean objItemPromoDiscount = new clsWebPOSBillSettlementBean();
				objItemPromoDiscount.setStrItemCode(itemCode);
				objItemPromoDiscount.setStrItemName(item);
				objItemPromoDiscount.setDblDiscountAmt(0);
				objItemPromoDiscount.setDblDiscountPer(0);
				objItemPromoDiscount.setDblAmount(amount);
				objItemPromoDiscount.setDblQuantity(quantity);
				objItemPromoDiscount.setStrSubGroupCode(subGroupCode);
				objItemPromoDiscount.setStrGroupCode(groupCode);
				objItemPromoDiscount.setDblRate(rate);
				listOfPromotionItem.add(objItemPromoDiscount);
				// funFillListForItemRow(item, quantity, amount, itemCode, 0,
				// 0);
			}
			//
			// Object[] rows =
			// {
			// item, df.format(quantity), df.format(amount)
			// };
			// dm.addRow(rows);
		}

		mapResult.put("listOfPromotionItem", listOfPromotionItem);
		mapResult.put("checkPromotion", checkPromotion);
		return mapResult;
	}

	@RequestMapping(value = "/actionBillSettlementKOT", method = RequestMethod.GET)
	public ModelAndView printBill(@ModelAttribute("command") clsWebPOSBillSettlementBean objBean, BindingResult result, HttpServletRequest request) throws Exception
	{
		String clientCode = "", POSCode = "", POSDate = "", userCode = "", posClientCode = "";

		clientCode = request.getSession().getAttribute("gClientCode").toString();
		POSCode = request.getSession().getAttribute("gPOSCode").toString();
		POSDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		userCode = request.getSession().getAttribute("gUserCode").toString();

		// Insert into tblbillhd table
		String voucherNo = "";
		voucherNo = funGenerateBillNo(POSCode);

		String split = POSDate;
		String billDateTime = split;
		String custCode = "";

		Date dt = new Date();
		String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
		String dateTime = POSDate + " " + currentDateTime.split(" ")[1];

		List<clsBillDiscDtlModel> listBillDiscDtlModel = funSaveBillDiscountDetail(voucherNo, objBean, dateTime, POSCode, clientCode);

		// For Home Delivery
		funSaveHomeDelivery(voucherNo, objBean, POSCode, POSDate, clientCode);

		List<clsBillDtlModel> listObjBillDtl = new ArrayList<clsBillDtlModel>();
		List<clsBillModifierDtlModel> listObjBillModBillDtls = new ArrayList<clsBillModifierDtlModel>();

		List<clsBillPromotionDtlModel> listBillPromotionDtlModel = new ArrayList<clsBillPromotionDtlModel>();
		String custName = "", cardNo = "", orderProcessTime, orderPickupTime;
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		sbSql.append("select strItemCode,upper(strItemName),dblItemQuantity "
				+ " ,dblAmount,strKOTNo,strManualKOTNo,concat(Time(dteDateCreated),''),strCustomerCode "
				+ " ,strCustomerName,strCounterCode,strWaiterNo,strPromoCode,dblRate,strCardNo,concat(tmeOrderProcessing,''),concat(tmeOrderPickup,'') "
				+ " from tblitemrtemp "
				+ " where strPosCode='" + POSCode + "' "
				+ " and strTableNo='" + objBean.getStrTableNo() + "' and strNCKotYN='N' "
				+ " order by strTableNo ASC");

		String kot = "";
		List listItemKOTDtl = objBaseServiceImpl.funGetList(sbSql, "sql");
		if (listItemKOTDtl.size() > 0)
		{
			for (int i = 0; i < listItemKOTDtl.size(); i++)
			{
				Object[] objM = (Object[]) listItemKOTDtl.get(i);
				String iCode = objM[0].toString();
				String iName = objM[1].toString();
				double iQty = new Double(objM[2].toString());
				String iAmt = objM[3].toString();

				double rate = Double.parseDouble(objM[12].toString());
				kot = objM[4].toString();
				String manualKOTNo = objM[5].toString();
				billDateTime = split + " " + objM[6].toString();
				custCode = objM[7].toString();
				custName = objM[8].toString();
				String promoCode = objM[11].toString();
				cardNo = objM[13].toString();
				orderProcessTime = objM[14].toString();
				orderPickupTime = objM[15].toString();
				String sqlInsertBillDtl = "";

				if (!iCode.contains("M"))
				{
					if (null != hmPromoItem)
					{
						clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
						if (null != hmPromoItem.get(iCode))
						{
							clsPromotionItems objPromoItemDtl = hmPromoItem.get(iCode);
							if (objPromoItemDtl.getPromoType().equals("ItemWise"))
							{
								double freeQty = objPromoItemDtl.getFreeItemQty();
								double freeAmt = freeQty * rate;

								promoCode = objPromoItemDtl.getPromoCode();
								objPromortion.setStrItemCode(iCode);
								objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
								objPromortion.setDblAmount(freeAmt);
								objPromortion.setDblDiscountAmt(0);
								objPromortion.setDblDiscountPer(0);
								objPromortion.setDblQuantity(freeQty);
								objPromortion.setDblRate(rate);
								objPromortion.setStrDataPostFlag("N");
								objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
								objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
								hmPromoItem.remove(iCode);
							}
							else if (objPromoItemDtl.getPromoType().equals("Discount"))
							{
								if (objPromoItemDtl.getDiscType().equals("Value"))
								{
									double freeQty = objPromoItemDtl.getFreeItemQty();
									double amount = freeQty * rate;
									double discAmt = objPromoItemDtl.getDiscAmt();
									promoCode = objPromoItemDtl.getPromoCode();

									objPromortion.setStrItemCode(iCode);
									objPromortion.setStrPromotionCode("");
									objPromortion.setDblAmount(amount);
									objPromortion.setDblDiscountAmt(discAmt);
									objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
									objPromortion.setDblQuantity(0);
									objPromortion.setDblRate(rate);
									objPromortion.setStrDataPostFlag("N");
									objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
									objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
									hmPromoItem.remove(iCode);
								}
								else
								{
									iAmt = String.valueOf(iQty * rate);
									double amount = iQty * rate;
									double discAmt = amount * (objPromoItemDtl.getDiscPer() / 100);
									promoCode = objPromoItemDtl.getPromoCode();

									objPromortion.setStrItemCode(iCode);
									objPromortion.setStrPromotionCode("");
									objPromortion.setDblAmount(amount);
									objPromortion.setDblDiscountAmt(discAmt);
									objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
									objPromortion.setDblQuantity(0);
									objPromortion.setDblRate(rate);
									objPromortion.setStrDataPostFlag("N");
									objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
									objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

									hmPromoItem.remove(iCode);
								}
							}
							listBillPromotionDtlModel.add(objPromortion);
						}
					}
					String amt = "0.00";
					double discAmt = 0.00;
					double discPer = 0.00;

					if (!iCode.contains("M"))
					{
						for (clsItemsDtlsInBill obj : objBean.getListOfBillItemDtl())
						{
							if (iCode.equalsIgnoreCase(obj.getItemCode()))
							{
								discAmt = obj.getDiscountAmt();
								discPer = obj.getDiscountPer();
								break;
							}
						}
					}

					if (iQty > 0)
					{
						clsBillDtlModel objBillDtl = new clsBillDtlModel();
						if (iName.startsWith("=>"))
						{
							objBillDtl.setStrItemCode(iCode);
							objBillDtl.setStrItemName(iName);
							objBillDtl.setStrAdvBookingNo("");
							objBillDtl.setDblRate(rate);
							objBillDtl.setDblQuantity(iQty);
							objBillDtl.setDblAmount(Double.parseDouble(amt));
							objBillDtl.setDblTaxAmount(0.00);
							objBillDtl.setDteBillDate(billDateTime);
							objBillDtl.setStrKOTNo(kot);
							objBillDtl.setStrCounterCode(objM[9].toString());
							objBillDtl.setTmeOrderProcessing(orderProcessTime);
							objBillDtl.setStrDataPostFlag("N");
							objBillDtl.setStrMMSDataPostFlag("N");
							objBillDtl.setStrManualKOTNo(manualKOTNo);

							objBillDtl.setTdhYN("N");
							objBillDtl.setStrPromoCode(promoCode);

							objBillDtl.setStrWaiterNo(objM[10].toString());
							objBillDtl.setSequenceNo("");
							objBillDtl.setTmeOrderPickup(orderPickupTime);

							objBillDtl.setDblDiscountAmt(discAmt);
							objBillDtl.setDblDiscountPer(discPer);
						}
						else
						{
							objBillDtl.setStrItemCode(iCode);
							objBillDtl.setStrItemName(iName);
							objBillDtl.setStrAdvBookingNo("");
							objBillDtl.setDblRate(rate);
							objBillDtl.setDblQuantity(iQty);
							objBillDtl.setDblAmount(Double.parseDouble(amt));
							objBillDtl.setDblTaxAmount(0.00);
							objBillDtl.setDteBillDate(billDateTime);
							objBillDtl.setStrKOTNo(kot);
							objBillDtl.setStrCounterCode(objM[9].toString());
							objBillDtl.setTmeOrderProcessing(orderProcessTime);
							objBillDtl.setStrDataPostFlag("N");
							objBillDtl.setStrMMSDataPostFlag("N");
							objBillDtl.setStrManualKOTNo(manualKOTNo);
							objBillDtl.setTdhYN("N");
							objBillDtl.setStrPromoCode(promoCode);

							objBillDtl.setStrWaiterNo(objM[10].toString());
							objBillDtl.setSequenceNo("");
							objBillDtl.setTmeOrderPickup(orderPickupTime);

							objBillDtl.setDblDiscountAmt(discAmt);
							objBillDtl.setDblDiscountPer(discPer);
						}
						listObjBillDtl.add(objBillDtl);
					}
				}
				if (iCode.contains("M"))
				{
					StringBuilder sb1 = new StringBuilder(iCode);
					int seq = sb1.lastIndexOf("M");// break the string(if
													// itemcode contains
													// Itemcode with modifier
													// code then break the
													// string into substring )
					String modifierCode = sb1.substring(seq, sb1.length());// SubString
																			// modifier
																			// Code
					double amt = Double.parseDouble(iAmt);
					double modDiscAmt = 0, modDiscPer = 0;

					for (clsItemsDtlsInBill obj : objBean.getListOfBillItemDtl())
					{
						if (iCode.equalsIgnoreCase(obj.getItemCode()))
						{
							modDiscAmt = obj.getDiscountAmt();
							modDiscPer = obj.getDiscountPer();
						}
					}
					StringBuilder sbTemp = new StringBuilder(iCode);

					clsBillModifierDtlModel objBillModDtl = new clsBillModifierDtlModel();
					objBillModDtl.setStrItemCode(iCode);
					objBillModDtl.setStrModifierCode(modifierCode);
					objBillModDtl.setStrModifierName(iName);
					objBillModDtl.setDblRate(rate);
					objBillModDtl.setDblQuantity(iQty);
					objBillModDtl.setDblAmount(amt);
					objBillModDtl.setStrCustomerCode("");
					objBillModDtl.setStrDataPostFlag("N");
					objBillModDtl.setStrMMSDataPostFlag("N");
					objBillModDtl.setStrDefaultModifierDeselectedYN("N");
					objBillModDtl.setSequenceNo("");

					objBillModDtl.setDblDiscAmt(modDiscPer);
					objBillModDtl.setDblDiscPer(modDiscAmt);

					listObjBillModBillDtls.add(objBillModDtl);
				}
			}
		}

		String deleteBillTaxDTL = "delete from tblbilltaxdtl where strBillNo='" + voucherNo + "'";
		objBaseServiceImpl.funExecuteUpdate(deleteBillTaxDTL, "sql");

		List<clsBillTaxDtl> listObjBillTaxBillDtls = new ArrayList<clsBillTaxDtl>();
		for (clsTaxDtlsOnBill objTaxCalculationDtls : objBean.getListTaxDtlOnBill())
		{
			double dblTaxAmt = objTaxCalculationDtls.getTaxAmount();

			clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();
			objBillTaxDtl.setStrTaxCode(objTaxCalculationDtls.getTaxCode());
			objBillTaxDtl.setDblTaxableAmount(objTaxCalculationDtls.getTaxableAmount());
			objBillTaxDtl.setDblTaxAmount(dblTaxAmt);
			objBillTaxDtl.setStrDataPostFlag("");
			listObjBillTaxBillDtls.add(objBillTaxDtl);
		}

		clsBillHdModel objBillHd = new clsBillHdModel();
		objBillHd.setStrBillNo(voucherNo);
		objBillHd.setStrAdvBookingNo("");
		objBillHd.setDteBillDate(POSDate);
		objBillHd.setStrPOSCode(POSCode);
		objBillHd.setStrSettelmentMode("");
		objBillHd.setDblDiscountAmt(0.0);
		objBillHd.setDblDiscountPer(0.0);
		objBillHd.setDblTaxAmt(0.0);
		objBillHd.setDblSubTotal(objBean.getDblSubTotal());
		objBillHd.setDblGrandTotal(objBean.getDblGrandTotal());
		objBillHd.setStrTakeAway(objBean.getTakeAway());
		objBillHd.setStrOperationType(objBean.getBillTransType());
		objBillHd.setStrUserCreated(userCode);
		objBillHd.setStrUserEdited(userCode);
		objBillHd.setDteDateCreated(dateTime);
		objBillHd.setDteDateEdited(dateTime);
		objBillHd.setStrClientCode(clientCode);
		objBillHd.setStrTableNo(objBean.getStrTableNo());
		objBillHd.setStrWaiterNo(objBean.getStrWaiter());
		objBillHd.setStrCustomerCode(objBean.getStrCustomerCode());
		objBillHd.setStrManualBillNo("");
		objBillHd.setIntShiftCode(0);// /////////////////////////
		objBillHd.setIntPaxNo(objBean.getIntPaxNo());
		objBillHd.setStrDataPostFlag("N");
		objBillHd.setStrReasonCode("");
		objBillHd.setStrRemarks(objBean.getStrRemarks());
		objBillHd.setDblTipAmount(0.0);
		objBillHd.setDteSettleDate(POSDate);
		objBillHd.setStrCounterCode("");
		objBillHd.setDblDeliveryCharges(objBean.getDblDeliveryCharges());
		objBillHd.setStrAreaCode("");
		objBillHd.setStrDiscountRemark("");
		objBillHd.setStrTakeAwayRemarks("");
		objBillHd.setStrTransactionType("");
		objBillHd.setIntOrderNo(0);
		objBillHd.setStrCouponCode("");
		objBillHd.setStrJioMoneyRRefNo("");
		objBillHd.setStrJioMoneyAuthCode("");
		objBillHd.setStrJioMoneyTxnId("");
		objBillHd.setStrJioMoneyTxnDateTime("");
		objBillHd.setStrJioMoneyCardNo("");
		objBillHd.setStrJioMoneyCardType("");
		objBillHd.setDblRoundOff(0.00);
		objBillHd.setIntBillSeriesPaxNo(0);
		objBillHd.setDtBillDate(POSDate);
		objBillHd.setIntOrderNo(0);

		String discountOn = "";
		String chckDiscounton = objBean.getStrDisountOn();
		if (chckDiscounton != null)
		{
			if (chckDiscounton.equals("Total"))
			{
				discountOn = "All";
			}
			if (chckDiscounton.equals("item"))
			{
				discountOn = "Item";
			}
			if (chckDiscounton.equals("group"))
			{
				discountOn = "Group";
			}
			if (chckDiscounton.equals("subGroup"))
			{
				discountOn = "SubGroup";
			}
		}
		objBillHd.setStrDiscountOn(discountOn);
		objBillHd.setStrCardNo("");

		String gCMSIntegrationY = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, POSCode, "gCMSIntegrationYN");
		if (gCMSIntegrationY.equalsIgnoreCase("Y"))
		{
			if (objBean.getStrCustomerCode().trim().length() > 0)
			{
				String sqlDeleteCustomer = "delete from tblcustomermaster where strCustomerCode='" + custCode + "' "
						+ "and strClientCode='" + clientCode + "'";
				objBaseServiceImpl.funExecuteUpdate(sqlDeleteCustomer, "sql");

				String sqlInsertCustomer = "insert into tblcustomermaster (strCustomerCode,strCustomerName,strUserCreated"
						+ ",strUserEdited,dteDateCreated,dteDateEdited,strClientCode) "
						+ "values('" + custCode + "','" + custName + "','" + userCode + "','" + userCode + "'"
						+ ",'" + dateTime + "','" + dateTime + "'"
						+ ",'" + clientCode + "')";
				objBaseServiceImpl.funExecuteUpdate(sqlInsertCustomer, "sql");
			}
		}

		objBillHd.setListBillDtlModel(listObjBillDtl);
		objBillHd.setListBillModifierDtlModel(listObjBillModBillDtls);
		List<clsBillSettlementDtlModel> listBillSettlementDtlModel = funInsertBillSettlementDtlTable(objBean.getListSettlementDtlOnBill(), userCode, dateTime, voucherNo);

		objBillHd.setListBillDiscDtlModel(listBillDiscDtlModel);
		objBillHd.setListBillSettlementDtlModel(listBillSettlementDtlModel);
		objBillHd.setListBillDtlModel(listObjBillDtl);
		objBillHd.setListBillTaxDtl(listObjBillTaxBillDtls);
		objBillHd.setListBillPromotionDtlModel(listBillPromotionDtlModel);
		objBaseServiceImpl.funSave(objBillHd);

		objUtility.funUpdateBillDtlWithTaxValues(voucherNo, "Live", POSDate);
		sbSql.setLength(0);
		sbSql.append("select dblQuantity,dblRate,strItemCode "
				+ "from tblbillpromotiondtl "
				+ " where strBillNo='" + voucherNo + "' and strPromoType='ItemWise' ");

		List listBillPromo = objBaseServiceImpl.funGetList(sbSql, "sql");
		if (listBillPromo.size() > 0)
		{
			for (int i = 0; i < listBillPromo.size(); i++)
			{
				Object[] objPrmo = (Object[]) listBillPromo.get(i);
				double freeQty = Double.parseDouble(objPrmo[0].toString());
				sbSql.setLength(0);
				sbSql.append("select strItemCode,dblQuantity,strKOTNo,dblAmount "
						+ " from tblbilldtl "
						+ " where strItemCode='" + objPrmo[2].toString() + "'"
						+ " and strBillNo='" + voucherNo + "'");

				List listBillDetail = objBaseServiceImpl.funGetList(sbSql, "sql");
				if (listBillDetail.size() > 0)
				{
					for (int j = 0; j < listBillDetail.size(); j++)
					{
						Object[] objBillDtl = (Object[]) listBillDetail.get(j);
						if (freeQty > 0)
						{
							double saleQty = Double.parseDouble(objBillDtl[1].toString());
							double saleAmt = Double.parseDouble(objBillDtl[3].toString());
							if (saleQty <= freeQty)
							{
								freeQty = freeQty - saleQty;
								double amtToUpdate = saleAmt - (saleQty * Double.parseDouble(objPrmo[1].toString()));
								String sqlUpdate = "update tblbilldtl set dblAmount= " + amtToUpdate + " "
										+ " where strItemCode='" + objBillDtl[0].toString() + "' "
										+ "and strKOTNo='" + objBillDtl[2].toString() + "'";
								objBaseServiceImpl.funExecuteUpdate(sqlUpdate, "sql");
							}
							else
							{
								double amtToUpdate = saleAmt - (freeQty * Double.parseDouble(objPrmo[1].toString()));
								String sqlUpdate = "update tblbilldtl set dblAmount= " + amtToUpdate + " "
										+ " where strItemCode='" + objBillDtl[0].toString() + "' "
										+ "and strKOTNo='" + objBillDtl[2].toString() + "'";
								objBaseServiceImpl.funExecuteUpdate(sqlUpdate, "sql");
								freeQty = 0;
							}
						}
					}
				}
			}
		}
		String sqlTableStatus = "update tbltablemaster set strStatus='Billed' where strTableNo='" + objBean.getStrTableNo() + "';";
		objBaseServiceImpl.funExecuteUpdate(sqlTableStatus, "sql");

		String sqlDeleteKOT = "delete from tblitemrtemp where strTableNo='" + objBean.getStrTableNo() + "' and strPOSCode='" + POSCode + "'";
		objBaseServiceImpl.funExecuteUpdate(sqlDeleteKOT, "sql");

		Map<String, Object> model = new HashMap<String, Object>();
		objTextFileGeneration.funGenerateAndPrintBill(voucherNo, POSCode, clientCode);

		return funOpenForm(model, request);
	}

	// generate bill no.
	private String funGenerateBillNo(String POSCode)
	{
		String voucherNo = "";
		try
		{
			long code = 0;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strBillNo from tblstorelastbill where strPosCode='" + POSCode + "'");
			List listItemDtl = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

			if (listItemDtl != null && listItemDtl.size() > 0)
			{
				Object objItemDtl = (Object) listItemDtl.get(0);
				code = Math.round(Double.parseDouble(objItemDtl.toString()));
				code = code + 1;
				voucherNo = POSCode + String.format("%05d", code);
				objBaseServiceImpl.funExecuteUpdate("update tblstorelastbill set strBillNo='" + code + "' where strPosCode='" + POSCode + "'", "sql");
			}
			else
			{
				voucherNo = POSCode + "00001";
				sqlBuilder.setLength(0);
				objBaseServiceImpl.funExecuteUpdate("insert into tblstorelastbill values('" + POSCode + "','1')", "sql");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return voucherNo;
		}
	}

	private List funSaveBillDiscountDetail(String voucherNo, clsWebPOSBillSettlementBean objBean, String dateTime, String POSCode, String userCode)
	{
		List<clsBillDiscDtlModel> listBillDiscDtlModel = new ArrayList<clsBillDiscDtlModel>();
		try
		{
			double totalDiscAmt = 0.00, finalDiscPer = 0.00;
			for (clsDiscountDtlsOnBill objBillDiscDtl : objBean.getListDiscountDtlOnBill())
			{
				String discOnType = objBillDiscDtl.getDiscountOnType();
				String discOnValue = objBillDiscDtl.getDiscountOnValue();
				String remark = objBillDiscDtl.getDiscountRemarks();
				String reason = objBillDiscDtl.getDiscountReasonCode();
				double discPer = objBillDiscDtl.getDiscountPer();
				double discAmt = objBillDiscDtl.getDiscountAmt();
				double discOnAmt = objBillDiscDtl.getDiscountOnAmt();

				clsBillDiscDtlModel objDiscModel = new clsBillDiscDtlModel();
				objDiscModel.setStrPOSCode(POSCode);
				objDiscModel.setDblDiscAmt(discAmt);
				objDiscModel.setDblDiscPer(discPer);
				objDiscModel.setDblAmount(discOnAmt);
				objDiscModel.setStrDiscOnType(discOnType);
				objDiscModel.setStrDiscOnValue(discOnValue);
				objDiscModel.setDteDateCreated(dateTime);
				objDiscModel.setDteDateEdited(dateTime);
				objDiscModel.setStrUserCreated(userCode);
				objDiscModel.setStrUserEdited(userCode);
				objDiscModel.setStrDiscReasonCode(reason);
				objDiscModel.setStrDiscRemarks(remark);
				objDiscModel.setStrDataPostFlag("N");
				listBillDiscDtlModel.add(objDiscModel);
				totalDiscAmt += discAmt;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return listBillDiscDtlModel;
		}
	}

	private List funInsertBillSettlementDtlTable(List<clsSettlementDtlsOnBill> listObjBillSettlementDtl, String userCode, String dtCurrentDate, String voucherNo) throws Exception
	{
		String sqlDelete = "delete from tblbillsettlementdtl where strBillNo='" + voucherNo + "'";
		objBaseServiceImpl.funExecuteUpdate(sqlDelete, "sql");
		List<clsBillSettlementDtlModel> listBillSettlementDtlModel = new ArrayList<clsBillSettlementDtlModel>();
		for (clsSettlementDtlsOnBill objBillSettlementDtl : listObjBillSettlementDtl)
		{
			clsBillSettlementDtlModel objSettleModel = new clsBillSettlementDtlModel();
			// objSettleModel.setStrBillNo(
			// objBillSettlementDtl.getStrBillNo());
			objSettleModel.setStrSettlementCode(objBillSettlementDtl.getStrSettelmentCode());
			objSettleModel.setDblSettlementAmt(objBillSettlementDtl.getDblSettlementAmt());
			objSettleModel.setDblPaidAmt(objBillSettlementDtl.getDblPaidAmt());
			objSettleModel.setStrExpiryDate("");
			objSettleModel.setStrCardName("");
			objSettleModel.setStrRemark("");
			// objSettleModel.setStrClientCode(objBillSettlementDtl.getStrClientCode());
			objSettleModel.setStrCustomerCode("");
			objSettleModel.setDblActualAmt(objBillSettlementDtl.getDblActualAmt());
			objSettleModel.setDblRefundAmt(objBillSettlementDtl.getDblRefundAmt());
			objSettleModel.setStrGiftVoucherCode("");
			objSettleModel.setStrDataPostFlag("");
			// objSettleModel.setDteBillDate(dtCurrentDate);
			objSettleModel.setStrFolioNo("");
			objSettleModel.setStrRoomNo("");
			listBillSettlementDtlModel.add(objSettleModel);
			// objBaseService.funSave(objSettleModel);

		}
		return listBillSettlementDtlModel;
		// StringBuilder sb1 = new StringBuilder(sqlInsertBillSettlementDtl);
		// int index1 = sb1.lastIndexOf(",");
		// sqlInsertBillSettlementDtl = sb1.delete(index1,
		// sb1.length()).toString();

	}

	public clsBillPromotionDtlModel funInsertIntoPromotion(String voucherNo, String iCode, String promoCode, double rate, double iQty)
	{

		clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
		clsPromotionItems objPromoItemDtl = hmPromoItem.get(iCode);
		if (objPromoItemDtl.getPromoType().equals("ItemWise"))
		{
			double freeQty = objPromoItemDtl.getFreeItemQty();
			double freeAmt = freeQty * rate;

			promoCode = objPromoItemDtl.getPromoCode();
			objPromortion.setStrItemCode(iCode);
			objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
			objPromortion.setDblAmount(freeAmt);
			objPromortion.setDblDiscountAmt(0);
			objPromortion.setDblDiscountPer(0);
			objPromortion.setDblQuantity(freeQty);
			objPromortion.setDblRate(rate);
			objPromortion.setStrDataPostFlag("N");
			objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
			objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
			hmPromoItem.remove(iCode);
		}
		else if (objPromoItemDtl.getPromoType().equals("Discount"))
		{
			if (objPromoItemDtl.getDiscType().equals("Value"))
			{
				double freeQty = objPromoItemDtl.getFreeItemQty();
				double amount = freeQty * rate;
				double discAmt = objPromoItemDtl.getDiscAmt();

				promoCode = objPromoItemDtl.getPromoCode();
				objPromortion.setStrItemCode(iCode);
				objPromortion.setStrPromotionCode("");
				objPromortion.setDblAmount(amount);
				objPromortion.setDblDiscountAmt(discAmt);
				objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
				objPromortion.setDblQuantity(0);
				objPromortion.setDblRate(rate);
				objPromortion.setStrDataPostFlag("N");
				objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
				objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
				hmPromoItem.remove(iCode);
			}
			else
			{

				double amount = iQty * rate;
				double discAmt = amount * (objPromoItemDtl.getDiscPer() / 100);

				promoCode = objPromoItemDtl.getPromoCode();

				objPromortion.setStrItemCode(iCode);
				objPromortion.setStrPromotionCode("");
				objPromortion.setDblAmount(amount);
				objPromortion.setDblDiscountAmt(discAmt);
				objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
				objPromortion.setDblQuantity(0);
				objPromortion.setDblRate(rate);
				objPromortion.setStrDataPostFlag("N");
				objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
				objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

				hmPromoItem.remove(iCode);
			}
		}

		return objPromortion;
	}

	public void funSaveHomeDelivery(String voucherNo, clsWebPOSBillSettlementBean objBean, String POSCode, String POSDate, String clientCode) throws Exception
	{
		StringBuilder sbSql = new StringBuilder();
		sbSql.setLength(0);
		sbSql.append("select strHomeDelivery,strCustomerCode,strCustomerName,strDelBoyCode "
				+ "from tblitemrtemp where strTableNo='" + objBean.getStrTableNo() + "' "
				+ "group by strTableNo ;");

		List listsqlCheckHomeDelivery = objBaseServiceImpl.funGetList(sbSql, "sql");
		if (listsqlCheckHomeDelivery.size() > 0)
		{
			Object[] objM = (Object[]) listsqlCheckHomeDelivery.get(0);
			String homeDeliveryYesNo = objM[0].toString();
			String customerCode = objM[1].toString();
			String custName = objM[2].toString();
			String deliveryBoyCode = objM[3].toString();

			if ("Yes".equalsIgnoreCase(homeDeliveryYesNo))
			{
				Calendar c = Calendar.getInstance();
				int hh = c.get(Calendar.HOUR);
				int mm = c.get(Calendar.MINUTE);
				int ss = c.get(Calendar.SECOND);
				int ap = c.get(Calendar.AM_PM);

				String ampm = "AM";
				if (ap == 1)
				{
					ampm = "PM";
				}
				String currentTime = hh + ":" + mm + ":" + ss + ":" + ampm;
				clsHomeDeliveryHdModel objHomeDeliveryHdModel = new clsHomeDeliveryHdModel();
				objHomeDeliveryHdModel.setStrBillNo(voucherNo);
				objHomeDeliveryHdModel.setStrCustomerCode(customerCode);
				objHomeDeliveryHdModel.setStrDPCode(deliveryBoyCode);
				objHomeDeliveryHdModel.setDteDate(POSDate);
				objHomeDeliveryHdModel.setTmeTime(currentTime);
				objHomeDeliveryHdModel.setStrPOSCode(POSCode);
				objHomeDeliveryHdModel.setStrCustAddressLine1("");
				objHomeDeliveryHdModel.setStrCustAddressLine2("");
				objHomeDeliveryHdModel.setStrCustAddressLine3("");
				objHomeDeliveryHdModel.setStrCustAddressLine4("");
				objHomeDeliveryHdModel.setStrCustCity("");
				objHomeDeliveryHdModel.setStrClientCode(clientCode);
				objHomeDeliveryHdModel.setDblHomeDeliCharge(objBean.getDblDeliveryCharges());
				objHomeDeliveryHdModel.setDblLooseCashAmt(0);
				objHomeDeliveryHdModel.setStrDataPostFlag("N");
				objBaseServiceImpl.funSave(objHomeDeliveryHdModel);

				// Saving for home delivery Detail data
				if (objBean.getStrDeliveryBoyCode() != null)
				{
					clsHomeDeliveryDtlModel objDtlModel = new clsHomeDeliveryDtlModel();
					objDtlModel.setStrBillNo(voucherNo);
					objDtlModel.setDblDBIncentives(0);
					objDtlModel.setDteBillDate(POSDate);
					objDtlModel.setStrClientCode(clientCode);
					objDtlModel.setStrDataPostFlag("N");
					objDtlModel.setStrDPCode(deliveryBoyCode);
					objDtlModel.setStrSettleYN("N");
					objBaseServiceImpl.funSave(objDtlModel);
				}
			}
		}
	}

}
