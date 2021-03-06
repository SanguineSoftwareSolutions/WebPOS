package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsDiscountDtlsOnBill;
import com.sanguine.webpos.bean.clsItemDtlForTax;
import com.sanguine.webpos.bean.clsItemsDtlsInBill;
import com.sanguine.webpos.bean.clsPromotionItems;
import com.sanguine.webpos.bean.clsSettelementOptions;
import com.sanguine.webpos.bean.clsSettlementDtlsOnBill;
import com.sanguine.webpos.bean.clsTaxCalculation;
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
import com.sanguine.webpos.util.clsPOSSetupUtility;
import com.sanguine.webpos.util.clsTextFileGenerator;
import com.sanguine.webpos.util.clsUtilityController;

@Controller
public class clsBillSettlementController
{

	private List listItemCode;
	String clientCode = "",posCode = "",posDate = "",userCode = "",posClientCode = "";

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	JSONArray listReasonCode,listReasonName;
	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;
	@Autowired
	clsUtilityController objUtility;

	@Autowired
	clsTextFileGenerator objTextFileGeneration;

	@Autowired
	clsPOSSetupUtility objPOSSetupUtility;

	@Autowired
	clsBillingAPIController objBillingAPIController;

	private Map<String, clsPromotionItems> hmPromoItem = new HashMap<String, clsPromotionItems>();

	// for Direct Biller
	@RequestMapping(value = "/frmBillSettlementTemp", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		
		
		
		
		String urlHits = "1";
		try
		{
				
			urlHits = request.getParameter("saddr").toString();
			request.getSession().setAttribute("customerMobile", ""); // mobile
																		// no
		}
		catch (Exception e)
		{
			urlHits = "1";
		}
		clsWebPOSBillSettlementBean obBillSettlementBean = new clsWebPOSBillSettlementBean();
		clientCode = request.getSession().getAttribute("gClientCode").toString();
		posClientCode = request.getSession().getAttribute("gPOSCode").toString();
		posCode = request.getSession().getAttribute("gPOSCode").toString();
		posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		userCode = request.getSession().getAttribute("gUserCode").toString();

		// //////////////// Direct biller Tab Data

		JSONObject jObj = objBillingAPIController.funGetItemPricingDtl(clientCode, posDate, posCode);

		JSONArray jsonArrForDirectBillerMenuItemPricing = (JSONArray) jObj.get("MenuItemPricingDtl");
		obBillSettlementBean.setJsonArrForDirectBillerMenuItemPricing(jsonArrForDirectBillerMenuItemPricing);

		jObj = objBillingAPIController.funGetMenuHeads(posCode, userCode);
		JSONArray jsonArrForDirectBillerMenuHeads = (JSONArray) jObj.get("MenuHeads");
		obBillSettlementBean.setJsonArrForDirectBillerMenuHeads(jsonArrForDirectBillerMenuHeads);

		jObj = objBillingAPIController.funGetButttonList("DirectBiller", posCode, clientCode);

		JSONArray jsonArrForDirectBillerFooterButtons = (JSONArray) jObj.get("buttonList");
		obBillSettlementBean.setJsonArrForDirectBillerFooterButtons(jsonArrForDirectBillerFooterButtons);

		jObj = objBillingAPIController.funPopularItem(clientCode, posDate, posCode);
		JSONArray jsonArrForPopularItems = (JSONArray) jObj.get("PopularItems");

		obBillSettlementBean.setJsonArrForPopularItems(jsonArrForPopularItems);

		model.put("urlHits", urlHits);
		model.put("billNo", "");
		model.put("billDate", posDate.split("-")[2] + "-" + posDate.split("-")[1] + "-" + posDate.split("-")[0]);
		model.put("gCustAddressSelectionForBill", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CustAddressSelectionForBill"));
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));

		// ///////////Bill Settlement tab ////////////////////////////////////

		String usertype = request.getSession().getAttribute("gUserType").toString();
		boolean isSuperUser = false;
		if (usertype.equalsIgnoreCase("yes"))
		{
			isSuperUser = true;
		}
		else
		{
			isSuperUser = false;
		}

		List listSettlementObject = new ArrayList<clsSettelementOptions>();
		try
		{

			JSONObject jObj1 = objBillingAPIController.funSettlementMode(clientCode, posCode, isSuperUser);

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
		String operationFrom = "directBiller";
		model.put("operationFrom", operationFrom);
		model.put("billDate", posDate);
		model.put("gCMSIntegrationYN", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CMSIntegrationYN"));
		model.put("gCRMInterface", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CRMInterface"));
		String gPopUpToApplyPromotionsOnBill = objPOSSetupUtility.funGetParameterValuePOSWise(clientCode, posCode, "gPopUpToApplyPromotionsOnBill");
		model.put("gPopUpToApplyPromotionsOnBill", gPopUpToApplyPromotionsOnBill);
		model.put("gCreditCardSlipNo", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardSlipNoCompulsoryYN"));
		model.put("gCreditCardExpiryDate", clsPOSGlobalFunctionsController.hmPOSSetupValues.get("CreditCardExpiryDateCompulsoryYN"));

		funLoadAllReasonMasterData(request);
		List listDiscountCombo = new ArrayList<List>();
		List listSubGroupName = new ArrayList<>();
		List listSubGroupCode = new ArrayList<>();
		List listGroupName = new ArrayList<>();
		List listGroupCode = new ArrayList<>();
		listDiscountCombo = funLoadItemsGroupSubGroupData();
		if (listDiscountCombo.size() > 0)
		{
			listSubGroupName = (List) listDiscountCombo.get(0);
			listSubGroupCode = (List) listDiscountCombo.get(1);
			listGroupName = (List) listDiscountCombo.get(2);
			listGroupCode = (List) listDiscountCombo.get(3);

		}

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

		return new ModelAndView("frmBillSettlementTemp", "command", obBillSettlementBean);

	}

	public void funLoadAllReasonMasterData(HttpServletRequest request)
	{
		Map<String, String> mapModBill = new HashMap<String, String>();
		Map<String, String> mapComplementry = new HashMap<String, String>();
		Map<String, String> mapDiscount = new HashMap<String, String>();
		String clientCode = request.getSession().getAttribute("gClientCode").toString();

		JSONObject jObj = objBillingAPIController.funLoadAllReasonMasterData(clientCode);
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

	public List funLoadItemsGroupSubGroupData()
	{
		List listDiscountCombo = new ArrayList<List>();
		try
		{

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return listDiscountCombo;
	}

	public JSONObject funFillGroupSubGroupList(ArrayList<String> arrListItemCode)
	{
		JSONObject jsonAllListsData = new JSONObject();
		StringBuilder sb = new StringBuilder();
		try
		{
			StringBuilder sqlBuilder = new StringBuilder();
			List listSubGroupName = new ArrayList<>();
			List listSubGroupCode = new ArrayList<>();
			List listGroupName = new ArrayList<>();
			List listGroupCode = new ArrayList<>();
			listSubGroupName.add("--select--");
			listSubGroupCode.add("--select--");
			listGroupName.add("--select--");
			listGroupCode.add("--select--");
			boolean first = true;
			for (String test : arrListItemCode)
			{
				if (first)
				{
					sb.append("'").append(test).append("");
					first = false;
				}
				else
				{
					sb.append("','").append(test).append("");
				}
			}
			// String t1 = sb.toString()+"'";
			sb.append("'");
			if (sb.toString().trim().length() > 1)
			{

				sqlBuilder.setLength(0);
				sqlBuilder.append("select a.strSubGroupCode,b.strSubGroupName,c.strGroupCode,c.strGroupName "
						+ " from tblitemmaster a , tblsubgrouphd b,tblgrouphd c"
						+ " where a.strItemCode IN (" + sb.toString() + ") and a.strDiscountApply='Y' "
						+ " and a.strSubGroupCode=b.strSubGroupCode and b.strGroupCode=c.strGroupCode;");

				List list = objBaseServiceImpl.funGetList(sqlBuilder, "sql");
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] ob = (Object[]) list.get(i);
						if (!listSubGroupCode.contains(ob[0].toString()))
						{
							listSubGroupCode.add(ob[0].toString());
							listSubGroupName.add(ob[1].toString());
						}
						if (!listGroupCode.contains(ob[2].toString()))
						{
							listGroupCode.add(ob[2].toString());
							listGroupName.add(ob[3].toString());
						}
					}
				}

			}

			Gson gson = new Gson();
			Type type = new TypeToken<List<String>>()
			{
			}.getType();
			String strSubGroupCode = gson.toJson(listSubGroupCode, type);
			String strSubGroupName = gson.toJson(listSubGroupName, type);
			String strGroupCode = gson.toJson(listGroupCode, type);
			String strGroupName = gson.toJson(listGroupName, type);

			jsonAllListsData.put("listSubGroupCode", strSubGroupCode);
			jsonAllListsData.put("listSubGroupName", strSubGroupName);
			jsonAllListsData.put("listGroupCode", strGroupCode);
			jsonAllListsData.put("listGroupName", strGroupName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			sb = null;
		}
		return jsonAllListsData;
	}

	// @RequestMapping(value = "/funCalculateTaxInSettlement", method =
	// RequestMethod.POST)
	@RequestMapping(value = "/funCalculateTaxInSettlement", method = RequestMethod.POST, headers =
	{ "Content-type=application/json" })
	private @ResponseBody List<clsTaxDtlsOnBill> funCalculateTax(@RequestBody List listBillItem, HttpServletRequest request)
	{
		List<clsTaxDtlsOnBill> listTaxDtlOnBill = new ArrayList<clsTaxDtlsOnBill>();
		// if (billTransType.equalsIgnoreCase("Home Delivery"))
		// {
		// operationTypeForTax = "HomeDelivery";
		// }
		// else if (obBillItem.getTakeAwayYN().equals("Yes"))
		// {
		// operationTypeForTax = "TakeAway";
		// }
		// else
		// {
		// operationTypeForTax = "DineIn";
		// }

		// customerType=objDirectBillerBean.getStrCustomerType();
		String operationTypeForTax = "DineIn";
		clientCode = request.getSession().getAttribute("gClientCode").toString();
		posClientCode = request.getSession().getAttribute("gClientCode").toString();
		posCode = request.getSession().getAttribute("gPOSCode").toString();
		posDate = request.getSession().getAttribute("gPOSDate").toString().split(" ")[0];
		userCode = request.getSession().getAttribute("gUserCode").toString();

		JSONObject jsonSelectedItemDtl = new JSONObject();
		JSONArray jArrClass = new JSONArray();
		for (int cnt = 0; cnt < listBillItem.size(); cnt++)
		{
			Map listItemDtl = (Map) listBillItem.get(cnt);
			System.out.println(listItemDtl.get("itemName"));
			JSONObject objRows = new JSONObject();
			if (listItemDtl.get("itemCode") != null)
			{
				objRows.put("strPOSCode", posCode);
				objRows.put("strItemCode", listItemDtl.get("itemCode"));
				objRows.put("strItemName", listItemDtl.get("itemName"));
				objRows.put("dblItemQuantity", listItemDtl.get("quantity"));
				objRows.put("dblAmount", listItemDtl.get("amount"));
				objRows.put("strClientCode", clientCode);
				objRows.put("OperationType", operationTypeForTax);// operationTypeFor
																	// Tax
				objRows.put("AreaCode", "");
				objRows.put("POSDate", posDate);

				jArrClass.add(objRows);
			}

		}
		jsonSelectedItemDtl.put("TaxDtl", jArrClass);

		// call WebService

		JSONObject jObj = funCalculateTax(jsonSelectedItemDtl);
		JSONArray jArrTaxList = (JSONArray) jObj.get("listOfTax");
		String totalTaxAmt = jObj.get("totalTaxAmt").toString();
		clsTaxDtlsOnBill objTaxDtl;
		JSONObject jsonTax = new JSONObject();
		for (int i = 0; i < jArrTaxList.size(); i++)
		{
			objTaxDtl = new clsTaxDtlsOnBill();
			jsonTax = (JSONObject) jArrTaxList.get(i);
			objTaxDtl.setTaxName(jsonTax.get("TaxName").toString());
			objTaxDtl.setTaxAmount(Double.parseDouble(jsonTax.get("TaxAmt").toString()));
			objTaxDtl.setTaxCode(jsonTax.get("taxCode").toString());
			objTaxDtl.setTaxCalculationType(jsonTax.get("taxCalculationType").toString());
			objTaxDtl.setTaxableAmount(Double.parseDouble(jsonTax.get("taxableAmount").toString()));

			listTaxDtlOnBill.add(objTaxDtl);

		}

		return listTaxDtlOnBill;
	}

	public JSONObject funCalculateTax(JSONObject objKOTTaxData)
	{

		String taxAmt = "";
		double subTotalForTax = 0;
		double taxAmount = 0.0;
		JSONObject jsTaxDtl = new JSONObject();
		try
		{

			String posCode = "", areaCode = "", operationType = "", clientCode = "";

			List<clsItemDtlForTax> arrListItemDtls = new ArrayList<clsItemDtlForTax>();
			JSONArray mJsonArray = (JSONArray) objKOTTaxData.get("TaxDtl");
			String sql = "";
			String posDate = "";
			ResultSet rs;
			JSONObject mJsonObject = new JSONObject();
			for (int i = 0; i < mJsonArray.size(); i++)
			{
				clsItemDtlForTax objItemDtl = new clsItemDtlForTax();
				mJsonObject = (JSONObject) mJsonArray.get(i);
				String itemName = mJsonObject.get("strItemName").toString();
				String itemCode = mJsonObject.get("strItemCode").toString();
				System.out.println(itemName);
				double amt = Double.parseDouble(mJsonObject.get("dblAmount").toString());
				operationType = mJsonObject.get("OperationType").toString();
				posCode = mJsonObject.get("strPOSCode").toString();
				areaCode = mJsonObject.get("AreaCode").toString();
				posDate = mJsonObject.get("POSDate").toString();
				clientCode = mJsonObject.get("strClientCode").toString();
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

				objItemDtl.setItemCode(itemCode);
				objItemDtl.setItemName(itemName);
				objItemDtl.setAmount(amt);
				objItemDtl.setDiscAmt(0);
				objItemDtl.setDiscPer(0);
				arrListItemDtls.add(objItemDtl);
				subTotalForTax += amt;
				// tableNo=mJsonObject.get("strTableNo").toString();

			}

			Date dt = new Date();
			String date = (dt.getYear() + 1900) + "-" + (dt.getMonth() + 1) + "-" + dt.getDate();
			clsTaxCalculation objTaxCalculation = new clsTaxCalculation();

			List<clsTaxCalculationDtls> arrListTaxDtl = objUtility.funCalculateTax(arrListItemDtls, posCode
					, posDate, areaCode, operationType, subTotalForTax, 0.0, "");
			JSONArray jAyyTaxList = new JSONArray();
			JSONObject jsTax;
			for (int cnt = 0; cnt < arrListTaxDtl.size(); cnt++)
			{
				jsTax = new JSONObject();
				clsTaxCalculationDtls obj = (clsTaxCalculationDtls) arrListTaxDtl.get(cnt);
				System.out.println("Tax Dtl= " + obj.getTaxCode() + "\t" + obj.getTaxName() + "\t" + obj.getTaxAmount());
				taxAmount += obj.getTaxAmount();
				taxAmt = String.valueOf(taxAmount);
				jsTax.put("TaxName", obj.getTaxName());
				jsTax.put("TaxAmt", obj.getTaxAmount());
				jsTax.put("taxCode", obj.getTaxCode());
				jsTax.put("taxCalculationType", obj.getTaxCalculationType());
				jsTax.put("taxableAmount", obj.getTaxableAmount());

				jAyyTaxList.add(jsTax);
			}
			jsTaxDtl.put("listOfTax", jAyyTaxList);
			jsTaxDtl.put("totalTaxAmt", taxAmt);

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsTaxDtl;// Response.status(201).entity(jsTaxDtl).build();
	}

	@RequestMapping(value = "/promotionCalculate", method = RequestMethod.POST, headers =
	{ "Content-type=application/json" })
	private @ResponseBody Map funPromotionCalculate(@RequestBody List listItmeDtl, HttpServletRequest request) throws Exception
	{

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
		// Map<String,
		// clsPromotionItems>objPromotion=objUtility.funCalculatePromotions("DirectBiller",
		// "", "", new ArrayList(),"",areaCode,"",request,listofItems);

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

		// (String transType, String billFromKOTsList, String billNo,
		// List<clsBillDtl> listOfSplitedGridItems,String tableNo,String
		// areaCode,String voucherNo,HttpServletRequest
		// request,List<clsWebPOSBillSettlementBean> listItemDtl ) throws
		// Exception

		Map<String, clsPromotionItems> hmPromoItemsToDisplay = new HashMap<String, clsPromotionItems>();

		List<clsItemDtlForTax> arrListItemDtls = new ArrayList<clsItemDtlForTax>();
		List<clsWebPOSBillSettlementBean> listOfPromotionItem = new ArrayList<clsWebPOSBillSettlementBean>();
		Map<String, clsWebPOSBillSettlementBean> mapPromoItemDisc = new HashMap<String, clsWebPOSBillSettlementBean>();
		// directbiller

		for (int cnt = 0; cnt < listItmeDtl.size(); cnt++)
		{
			Map listItemDtl = (Map) listItmeDtl.get(cnt);
			double freeAmount = 0.00;
			String item = listItemDtl.get("itemName").toString().trim();
			double quantity = Double.parseDouble(listItemDtl.get("quantity").toString());
			double amount = Double.parseDouble(listItemDtl.get("amount").toString());
			String groupCode = listItemDtl.get("strGroupcode").toString().trim();
			String subGroupCode = listItemDtl.get("strSubGroupCode").toString().trim();

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
									hmPromoItem.put(itemCode, objPromoItemsDtl);
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

	@RequestMapping(value = "/funCalculateDeliveryChages", method = RequestMethod.POST)
	public @ResponseBody double funCalclulateDeliveryCharges(@RequestParam("buildingCode") String buildingCode, @RequestParam("gCustomerCode") String gCustomerCode, @RequestParam("totalBillAmount") double totalBillAmount)
	{
		double deliverycharges = objUtility.funCalculateDeliveryChages(buildingCode, totalBillAmount, gCustomerCode, clientCode, posCode);
		return deliverycharges;
	}

	@RequestMapping(value = "/actionBillSettlement", method = RequestMethod.POST)
	public ModelAndView printBill(@ModelAttribute("command") clsWebPOSBillSettlementBean objBean, BindingResult result, HttpServletRequest req) throws Exception
	{
		// Insert into tblbillhd table
		String voucherNo = "";
		voucherNo = funGenerateBillNo();
		Date dt = new Date();
		String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
		String dateTime = posDate + " " + currentDateTime.split(" ")[1];
		clsBillHdModel objBillHd = new clsBillHdModel();
		objBillHd.setStrBillNo(voucherNo);
		objBillHd.setStrAdvBookingNo("");
		objBillHd.setDteBillDate(posDate);
		objBillHd.setStrPOSCode(posCode);

		objBillHd.setDblDiscountAmt(objBean.getDblDiscountAmt());
		objBillHd.setDblDiscountPer(objBean.getDblDiscountPer());
		objBillHd.setDblTaxAmt(objBean.getDblTotalTaxAmt());
		objBillHd.setDblSubTotal(objBean.getDblSubTotal());
		objBillHd.setDblGrandTotal(objBean.getDblGrandTotal());
		// ///////////
		// objBillHd.setDblGrandTotalRoundOffBy(_grandTotalRoundOffBy);
		objBillHd.setStrTakeAway(objBean.getTakeAway());
		objBillHd.setStrOperationType(objBean.getBillTransType());
		objBillHd.setStrUserCreated(userCode);
		objBillHd.setStrUserEdited(userCode);
		objBillHd.setDteDateCreated(dateTime);
		objBillHd.setDteDateEdited(dateTime);
		objBillHd.setStrClientCode(clientCode);
		objBillHd.setStrTableNo("");
		objBillHd.setStrWaiterNo("");
		objBillHd.setStrCustomerCode(objBean.getStrCustomerCode());
		objBillHd.setStrManualBillNo(objBean.getStrManualBillNo());
		objBillHd.setIntShiftCode(0);// /////////////////////////
		objBillHd.setIntPaxNo(objBean.getIntPaxNo());
		objBillHd.setStrDataPostFlag("N");
		objBillHd.setStrReasonCode("");
		objBillHd.setStrRemarks(objBean.getStrRemarks());
		objBillHd.setDblTipAmount(0.0);
		objBillHd.setDteSettleDate(posDate);
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
		objBillHd.setDtBillDate(posDate);
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

		// Insert into tblbilldtl table
		List<clsBillDtlModel> listObjBillDtl = new ArrayList<clsBillDtlModel>();
		List<clsBillModifierDtlModel> listObjBillModBillDtls = new ArrayList<clsBillModifierDtlModel>();
		for (clsItemsDtlsInBill listclsItemRow : objBean.getListOfBillItemDtl())
		{

			if (!(listclsItemRow.getItemName().contains("-->")))
			{
				double rate = 0.00;
				if (listclsItemRow.getQuantity() == 0)
				{
					rate = listclsItemRow.getRate();
				}
				else
				{
					rate = listclsItemRow.getAmount() / listclsItemRow.getQuantity();
				}

				clsBillDtlModel objBillDtl = new clsBillDtlModel();
				objBillDtl.setStrItemCode(listclsItemRow.getItemCode());
				objBillDtl.setStrItemName(listclsItemRow.getItemName());
				objBillDtl.setStrAdvBookingNo("");
				objBillDtl.setDblRate(listclsItemRow.getRate());
				objBillDtl.setDblQuantity(listclsItemRow.getQuantity());
				objBillDtl.setDblAmount(listclsItemRow.getAmount());
				objBillDtl.setDblTaxAmount(0);
				objBillDtl.setDteBillDate(posDate);
				objBillDtl.setStrKOTNo("");
				objBillDtl.setStrCounterCode("");
				objBillDtl.setTmeOrderProcessing("00:00:00");
				objBillDtl.setStrDataPostFlag("N");
				objBillDtl.setStrMMSDataPostFlag("N");
				objBillDtl.setStrManualKOTNo("");
				boolean tdYN = listclsItemRow.isTdhYN();
				if (tdYN)
				{
					objBillDtl.setTdhYN("Y");
				}
				else
				{
					objBillDtl.setTdhYN("N");
				}
				objBillDtl.setStrPromoCode(listclsItemRow.getPromoCode());
				objBillDtl.setStrCounterCode("");
				objBillDtl.setStrWaiterNo("");
				objBillDtl.setSequenceNo(listclsItemRow.getSeqNo());
				objBillDtl.setTmeOrderPickup("00:00:00");

				objBillDtl.setDblDiscountAmt(listclsItemRow.getDiscountAmt() * listclsItemRow.getQuantity());
				objBillDtl.setDblDiscountPer(listclsItemRow.getDiscountPer());
				listObjBillDtl.add(objBillDtl);
			}
			// Else For Modifier Item
			else
			{

				if (listclsItemRow.getItemName().contains("-->"))
				{
					double rate = listclsItemRow.getAmount() / listclsItemRow.getQuantity();
					double amt = listclsItemRow.getAmount();
					String code[] = listclsItemRow.getItemCode().split("!");
					String itemCode = code[0] + "" + code[1];
					clsBillModifierDtlModel objBillModDtl = new clsBillModifierDtlModel();
					objBillModDtl.setStrItemCode(itemCode);
					objBillModDtl.setStrModifierCode(code[1]);
					objBillModDtl.setStrModifierName(listclsItemRow.getItemName());
					objBillModDtl.setDblRate(rate);
					objBillModDtl.setDblQuantity(listclsItemRow.getQuantity());
					StringBuilder sbTemp = new StringBuilder(objBillModDtl.getStrItemCode());
					objBillModDtl.setDblAmount(amt);
					objBillModDtl.setStrCustomerCode("");
					objBillModDtl.setStrDataPostFlag("N");
					objBillModDtl.setStrMMSDataPostFlag("N");
					objBillModDtl.setStrDefaultModifierDeselectedYN("N");
					objBillModDtl.setSequenceNo("");

					objBillModDtl.setDblDiscAmt(listclsItemRow.getDiscountAmt() * listclsItemRow.getQuantity());
					objBillModDtl.setDblDiscPer(listclsItemRow.getDiscountPer());

					listObjBillModBillDtls.add(objBillModDtl);
				}

			}
		}
		objBillHd.setListBillDtlModel(listObjBillDtl);
		objBillHd.setListBillModifierDtlModel(listObjBillModBillDtls);
		List<clsBillSettlementDtlModel> listBillSettlementDtlModel = funInsertBillSettlementDtlTable(objBean.getListSettlementDtlOnBill(), userCode, dateTime, voucherNo);
		List<clsBillDiscDtlModel> listBillDiscDtlModel = funSaveBillDiscountDetail(voucherNo, objBean, dateTime);
		objBillHd.setListBillDiscDtlModel(listBillDiscDtlModel);
		objBillHd.setListBillSettlementDtlModel(listBillSettlementDtlModel);
		objBillHd.setListBillDtlModel(listObjBillDtl);

		objBillHd.setStrSettelmentMode("");
		List<clsSettlementDtlsOnBill> listObjBillSettlementDtl=objBean.getListSettlementDtlOnBill();
		if (listObjBillSettlementDtl != null && listObjBillSettlementDtl.size() == 0)
		{
			objBillHd.setStrSettelmentMode("");
		}
		else if (listObjBillSettlementDtl != null && listObjBillSettlementDtl.size() == 1)
		{
			objBillHd.setStrSettelmentMode(listObjBillSettlementDtl.get(0).getStrSettelmentDesc());
		}
		else
		{
			objBillHd.setStrSettelmentMode("MultiSettle");
		}

		List<clsBillTaxDtl> listObjBillTaxBillDtls = new ArrayList<clsBillTaxDtl>();
		for (clsTaxDtlsOnBill objTaxCalculationDtls : objBean.getListTaxDtlOnBill())
		{
			double dblTaxAmt = objTaxCalculationDtls.getTaxAmount();
			// totalTaxAmt = totalTaxAmt + dblTaxAmt;
			clsBillTaxDtl objBillTaxDtl = new clsBillTaxDtl();
			objBillTaxDtl.setStrTaxCode(objTaxCalculationDtls.getTaxCode());
			objBillTaxDtl.setDblTaxableAmount(objTaxCalculationDtls.getTaxableAmount());
			objBillTaxDtl.setDblTaxAmount(dblTaxAmt);
			objBillTaxDtl.setStrDataPostFlag("");
			listObjBillTaxBillDtls.add(objBillTaxDtl);
		}

		objBillHd.setListBillTaxDtl(listObjBillTaxBillDtls);

		List<clsBillPromotionDtlModel> listBillPromotionDtlModel = funInsertIntoPromotion(voucherNo, objBean);
		objBillHd.setListBillPromotionDtlModel(listBillPromotionDtlModel);
		objBaseServiceImpl.funSave(objBillHd);

		// /// For Home Delivery

		if (objBean.getBillTransType().equalsIgnoreCase("HomeDelivery"))
		{
			funSaveHomeDelivery(voucherNo, objBean);
		}
		Map<String, Object> model = new HashMap<String, Object>();

		objTextFileGeneration.funGenerateAndPrintBill(voucherNo, posCode, clientCode);
		// return new
		// ModelAndView("frmBillSettlementTemp","command",obBillSettlementBean);
		return funOpenForm(model, req);

	}

	// generate bill no.
	private String funGenerateBillNo()
	{
		String voucherNo = "";
		try
		{
			long code = 0;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.setLength(0);
			sqlBuilder.append("select strBillNo from tblstorelastbill where strPosCode='" + posCode + "'");
			List listItemDtl = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

			if (listItemDtl != null && listItemDtl.size() > 0)
			{

				Object objItemDtl = (Object) listItemDtl.get(0);

				code = Math.round(Double.parseDouble(objItemDtl.toString()));
				code = code + 1;

				voucherNo = posCode + String.format("%05d", code);
				objBaseServiceImpl.funExecuteUpdate("update tblstorelastbill set strBillNo='" + code + "' where strPosCode='" + posCode + "'", "sql");
			}
			else
			{
				voucherNo = posCode + "00001";
				sqlBuilder.setLength(0);
				objBaseServiceImpl.funExecuteUpdate("insert into tblstorelastbill values('" + posCode + "','1')", "sql");
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

	private List funSaveBillDiscountDetail(String voucherNo, clsWebPOSBillSettlementBean objBean, String dateTime)
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
				objDiscModel.setStrPOSCode(posCode);
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

			// if (_subTotal == 0.00)
			// {
			// }
			// else
			// {
			// finalDiscPer = (totalDiscAmt / _subTotal) * 100;
			// }
			// dblDiscountAmt = totalDiscAmt;
			// dblDiscountPer = finalDiscPer;
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

	public List funInsertIntoPromotion(String voucherNo, clsWebPOSBillSettlementBean objBean)
	{

		List<clsBillPromotionDtlModel> listBillPromotionDtlModel = new ArrayList<clsBillPromotionDtlModel>();
		for (clsItemsDtlsInBill objBillDtl : objBean.getListOfBillItemDtl())
		{
			double freeQty = 0;
			if (null != hmPromoItem)
			{
				if (null != hmPromoItem.get(objBillDtl.getItemCode()))
				{
					clsPromotionItems objPromoItemDtl = hmPromoItem.get(objBillDtl.getItemCode());
					if (objPromoItemDtl.getPromoType().equals("ItemWise"))
					{
						freeQty = objPromoItemDtl.getFreeItemQty();
						double freeAmt = freeQty * objBillDtl.getRate();

						clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
						objPromortion.setStrItemCode(objBillDtl.getItemCode());
						objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());
						objPromortion.setDblAmount(freeAmt);
						objPromortion.setDblDiscountAmt(0);
						objPromortion.setDblDiscountPer(0);
						objPromortion.setDblQuantity(freeQty);
						objPromortion.setDblRate(objBillDtl.getRate());
						objPromortion.setStrDataPostFlag("N");
						objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
						objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

						listBillPromotionDtlModel.add(objPromortion);

						hmPromoItem.remove(objBillDtl.getItemCode());
					}
					else if (objPromoItemDtl.getPromoType().equals("Discount"))
					{
						if (objPromoItemDtl.getDiscType().equals("Value"))
						{
							double amount = freeQty * objBillDtl.getRate();
							double discAmt = objPromoItemDtl.getDiscAmt();

							clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
							objPromortion.setStrItemCode(objBillDtl.getItemCode());
							objPromortion.setStrPromotionCode("");
							objPromortion.setDblAmount(amount);
							objPromortion.setDblDiscountAmt(discAmt);
							objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
							objPromortion.setDblQuantity(0);
							objPromortion.setDblRate(objBillDtl.getRate());
							objPromortion.setStrDataPostFlag("N");
							objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
							objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

							listBillPromotionDtlModel.add(objPromortion);

							hmPromoItem.remove(objBillDtl.getItemCode());
						}
						else
						{
							double totalAmt = objBillDtl.getQuantity() * objBillDtl.getRate();
							double discAmt = totalAmt - (totalAmt * (objPromoItemDtl.getDiscPer() / 100));

							clsBillPromotionDtlModel objPromortion = new clsBillPromotionDtlModel();
							objPromortion.setStrItemCode(objBillDtl.getItemCode());
							objPromortion.setStrPromotionCode("");
							objPromortion.setDblAmount(totalAmt);
							objPromortion.setDblDiscountAmt(discAmt);
							objPromortion.setDblDiscountPer(objPromoItemDtl.getDiscPer());
							objPromortion.setDblQuantity(0);
							objPromortion.setDblRate(objBillDtl.getRate());
							objPromortion.setStrDataPostFlag("N");
							objPromortion.setStrPromoType(objPromoItemDtl.getPromoType());
							objPromortion.setStrPromotionCode(objPromoItemDtl.getPromoCode());

							listBillPromotionDtlModel.add(objPromortion);

							hmPromoItem.remove(objBillDtl.getItemCode());
						}
					}
				}
			}
		}

		return listBillPromotionDtlModel;
	}

	public void funSaveHomeDelivery(String voucherNo, clsWebPOSBillSettlementBean objBean) throws Exception
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
		String currentTime = hh + ":" + mm + ":" + ss + ":" + ampm;//
		// String sql =
		// "select a.strCustomerCode,a.strCustomerName,a.longMobileNo,a.strBuldingCode,a.strCustAddress as strHomeAddress  "
		// + ",a.strStreetName,a.strLandmark,a.intPinCode,a.strCity,a.strState "
		// +
		// ",a.strOfficeBuildingCode,a.strOfficeBuildingName as strOfficeAddress,a.strOfficeStreetName,a.strOfficeLandmark,a.intPinCode "
		// + ",a.strOfficeCity,a.strOfficeState "
		// + ",a.strTempAddress,a.strTempStreet,a.strTempLandmark "
		// + "from  tblcustomermaster a "
		// + "where longMobileNo like '%" + objBean.getSt + "%' ";
		if (objBean.getStrDeliveryBoyCode() != null)
		{
			clsHomeDeliveryHdModel objHomeDeliveryHdModel = new clsHomeDeliveryHdModel();
			objHomeDeliveryHdModel.setStrBillNo(voucherNo);
			objHomeDeliveryHdModel.setStrCustomerCode(objBean.getStrCustomerCode());
			objHomeDeliveryHdModel.setStrDPCode(objBean.getStrDeliveryBoyCode());
			objHomeDeliveryHdModel.setDteDate(posDate);
			objHomeDeliveryHdModel.setTmeTime(currentTime);
			objHomeDeliveryHdModel.setStrPOSCode(posCode);
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
		}
		else
		{
			// String sql_tblhomedelivery =
			// "insert into tblhomedelivery(strBillNo,strCustomerCode,dteDate,tmeTime"
			// +
			// ",strPOSCode,strCustAddressLine1,strCustAddressLine2,strCustAddressLine3,strCustAddressLine4"
			// + ",strCustCity,strClientCode,dblHomeDeliCharge)"
			// + " values('" + voucherNo + "','" + custCode + "','"
			// + clsGlobalVarClass.gPOSDateForTransaction + "','" + currentTime
			// + "','"
			// + clsGlobalVarClass.gPOSCode + "','" + custAddType + "','',''"
			// + ",'','','" + clsGlobalVarClass.gClientCode + "'," +
			// _deliveryCharge + ")";

			clsHomeDeliveryHdModel objHomeDeliveryHdModel = new clsHomeDeliveryHdModel();
			objHomeDeliveryHdModel.setStrBillNo(voucherNo);
			objHomeDeliveryHdModel.setStrCustomerCode(objBean.getStrCustomerCode());
			objHomeDeliveryHdModel.setStrDPCode(objBean.getStrDeliveryBoyCode());
			objHomeDeliveryHdModel.setDteDate(posDate);
			objHomeDeliveryHdModel.setTmeTime(currentTime);
			objHomeDeliveryHdModel.setStrPOSCode(posCode);
			objHomeDeliveryHdModel.setStrCustAddressLine1("");
			objHomeDeliveryHdModel.setStrCustAddressLine2("");
			objHomeDeliveryHdModel.setStrCustAddressLine3("");
			objHomeDeliveryHdModel.setStrCustAddressLine4("");
			objHomeDeliveryHdModel.setStrCustCity("");
			objHomeDeliveryHdModel.setStrClientCode(clientCode);
			objHomeDeliveryHdModel.setDblHomeDeliCharge(objBean.getDblDeliveryCharges());
			objHomeDeliveryHdModel.setDblLooseCashAmt(0);

			objBaseServiceImpl.funSave(objHomeDeliveryHdModel);
		}
		// //Saving for home delivery Detail data

		clsHomeDeliveryDtlModel objDtlModel = new clsHomeDeliveryDtlModel();
		objDtlModel.setStrBillNo(voucherNo);
		objDtlModel.setDblDBIncentives(0);
		objDtlModel.setDteBillDate(posDate);
		objDtlModel.setStrClientCode(clientCode);
		objDtlModel.setStrDataPostFlag("N");
		objDtlModel.setStrDPCode(objBean.getStrDeliveryBoyCode());
		objDtlModel.setStrSettleYN("N");
		objBaseServiceImpl.funSave(objDtlModel);

	}

}
