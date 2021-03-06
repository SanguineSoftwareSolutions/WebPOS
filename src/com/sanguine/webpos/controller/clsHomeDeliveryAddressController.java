package com.sanguine.webpos.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsHomeDeliveryAddressBean;
import com.sanguine.webpos.bean.clsMakeKotBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSCustomerMasterBean;
import com.sanguine.webpos.bean.clsPOSMakeKOTBean;
import com.sanguine.webpos.model.clsCustomerMasterModel;
import com.sanguine.webpos.model.clsCustomerMasterModel_ID;
import com.sanguine.webpos.sevice.clsMasterService;
import com.sanguine.webpos.util.clsUtilityController;

@Controller
public class clsHomeDeliveryAddressController
{
	@Autowired
	private clsGlobalFunctions objGlobal;

	@Autowired
	private clsPOSGlobalFunctionsController objGlobalFun;

	@Autowired
	private clsUtilityController objUtilityController;

	@Autowired
	clsMasterService objMasterService;

	@Autowired
	clsBaseServiceImpl objBaseServiceImpl;

	Vector vTableNo = new Vector();
	Map map = new HashMap();
	List openKOTList = new ArrayList();
	List openTableList = new ArrayList();
	Map mapBuildingCodes = new HashMap();
	Map mapBuildingNames = new HashMap();

	@RequestMapping(value = "/frmHomeDeliveryAddress", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsHomeDeliveryAddressBean objBean, BindingResult result, Map<String, Object> model, @RequestParam(value = "strMobNo") String strMobNo, HttpServletRequest request)
	{

		try
		{

			String posURL = clsPOSGlobalFunctionsController.POSWSURL + "/clsMakeKOTController/funGetCustomerAddress"
					+ "?strMobNo=" + strMobNo;

			JSONObject jObj = objGlobal.funGETMethodUrlJosnObjectData(posURL);

			objBean.setStrCustomerCode(jObj.get("strCustomerCode").toString());
			objBean.setStrHomeCustomerName(jObj.get("strCustomerName").toString());
			objBean.setStrHomeMobileNo(jObj.get("longMobileNo").toString());

			objBean.setStrHomeAddress(jObj.get("strCustAddress").toString());
			objBean.setStrHomeCity(jObj.get("strCity").toString());
			objBean.setStrHomeLandmark(jObj.get("strLandmark").toString());
			objBean.setStrHomePinCode(jObj.get("intPinCode").toString());
			objBean.setStrHomeState(jObj.get("strState").toString());
			objBean.setStrHomeStreetName(jObj.get("strStreetName").toString());
			objBean.setStrOfficeCity(jObj.get("strOfficeCity").toString());
			objBean.setStrOfficeCustAddress(jObj.get("strOfficeAddress").toString());
			objBean.setStrOfficeLandmark(jObj.get("strOfficeLandmark").toString());
			objBean.setStrOfficePinCode(jObj.get("intOfficePinCode").toString());
			objBean.setStrOfficeState(jObj.get("strOfficeState").toString());
			objBean.setStrOfficeStreetName(jObj.get("strOfficeStreetName").toString());
			objBean.setStrTempCustAddress(jObj.get("strTempAddress").toString());
			objBean.setStrTempLandmark(jObj.get("strTempStreet").toString());
			objBean.setStrTempStreetName(jObj.get("strTempLandmark").toString());

			objBean.setStrHomeBuildingName(jObj.get("strHomeBuildingName").toString());
			objBean.setStrOfficeBuildingName(jObj.get("strOfficeBuildingName").toString());
			objBean.setStrTempBuildingName(jObj.get("strTempBuildingName").toString());

			String selectedAddress = "Home";
			if (request.getSession().getAttribute("homeDeliveryAddress") != null)
			{
				selectedAddress = request.getSession().getAttribute("homeDeliveryAddress").toString();
			}
			model.put("selectedAddress", selectedAddress);

			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select a.strBuildingCode,a.strBuildingName from tblbuildingmaster a");
			List listOfBuildingName = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

			if (listOfBuildingName != null && listOfBuildingName.size() > 0)
			{
				for (int i = 0; i < listOfBuildingName.size(); i++)
				{
					Object[] arrBuildings = (Object[]) listOfBuildingName.get(i);

					String buildingCode = arrBuildings[0].toString();
					String buildingName = arrBuildings[1].toString();

					mapBuildingNames.put(buildingCode, buildingName);
					mapBuildingCodes.put(buildingName, buildingCode);

				}
			}

			model.put("mapBuildingNames", mapBuildingNames);

			// strHomeBuildingName
			if (mapBuildingCodes.containsKey(objBean.getStrHomeBuildingName()))
			{
				model.put("strHomeBuildingName", mapBuildingCodes.get(objBean.getStrHomeBuildingName()));
			}

			// strOfficeBuildingName
			if (mapBuildingCodes.containsKey(objBean.getStrHomeBuildingName()))
			{
				model.put("strOfficeBuildingName", mapBuildingCodes.get(objBean.getStrOfficeBuildingName()));
			}

			// strTempBuildingName
			if (mapBuildingCodes.containsKey(objBean.getStrHomeBuildingName()))
			{
				model.put("strTempBuildingName", mapBuildingCodes.get(objBean.getStrTempBuildingName()));
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return new ModelAndView("frmHomeDeliveryAddress", "command", objBean);
	}

	@RequestMapping(value = "/updateHomeDeliveryAddress", method = RequestMethod.POST)
	public ModelAndView funUpdateHomeDeliveryAddress(@ModelAttribute("command") @Valid clsHomeDeliveryAddressBean objBean, BindingResult result, HttpServletRequest req, Map<String, Object> model)
	{
		JSONObject jObjMakeKOT = new JSONObject();
		try
		{

			String homeBuildingCode = objBean.getStrHomeBuildingName();
			String homeBuildingName = "";
			if (mapBuildingNames.get(homeBuildingCode) != null)
			{
				homeBuildingName=mapBuildingNames.get(homeBuildingCode).toString();
			}

			String officeBuildingCode = objBean.getStrOfficeBuildingName();
			String officeBuildingName = "";
			if (mapBuildingNames.get(officeBuildingCode) != null)
			{
				officeBuildingName=mapBuildingNames.get(officeBuildingCode).toString();
			}

			req.getSession().setAttribute("homeDeliveryAddress", objBean.getStrHomeDeliveryAddressFlag());
			model.put("selectedAddress", objBean.getStrHomeDeliveryAddressFlag());

			if (objBean.getStrHomeDeliveryAddressFlag().equalsIgnoreCase("Home"))
			{
				req.getSession().setAttribute("gAreaName", homeBuildingName);
			}
			else if (objBean.getStrHomeDeliveryAddressFlag().equalsIgnoreCase("Office"))
			{
				req.getSession().setAttribute("gAreaName", officeBuildingName);
			}
			else
			{
				req.getSession().setAttribute("gAreaName", objBean.getStrTempBuildingName());
			}

			String clientCode = req.getSession().getAttribute("gClientCode").toString();
			String userCode = req.getSession().getAttribute("gUserCode").toString();

			String customerMasterCode = objBean.getStrCustomerCode();
			boolean isNewCustomer = false;

			if (customerMasterCode.trim().isEmpty())
			{
				isNewCustomer = true;

				long lastNo = 1;
				String propertCode = clientCode.substring(4);

				List list = objUtilityController.funGetDocumentCode("POSCustomerMaster");
				if (list != null && !list.get(0).toString().equals("0"))
				{
					String strCode = "00";
					String code = list.get(0).toString();
					System.out.println("code-->" + code);
					StringBuilder sb = new StringBuilder(code);

					strCode = sb.substring(1, sb.length());

					lastNo = Long.parseLong(strCode);

					lastNo++;
					customerMasterCode = propertCode + "C" + String.format("%07d", lastNo);
				}
				else
				{
					customerMasterCode = propertCode + "C" + String.format("%07d", lastNo);
				}
			}
			clsCustomerMasterModel objModel = new clsCustomerMasterModel(new clsCustomerMasterModel_ID(customerMasterCode, clientCode));

			objModel.setStrCustomerName(objBean.getStrHomeCustomerName());
			objModel.setStrBuldingCode(homeBuildingCode);
			objModel.setStrBuildingName(homeBuildingName);
			objModel.setStrCustAddress(objBean.getStrHomeAddress());
			objModel.setStrStreetName(objBean.getStrHomeStreetName());
			objModel.setStrLandmark(objBean.getStrHomeLandmark());
			objModel.setStrArea("");
			objModel.setStrCity(objBean.getStrHomeCity());
			objModel.setStrState(objBean.getStrHomeState());
			objModel.setIntPinCode(objBean.getStrHomePinCode());
			objModel.setLongMobileNo(objBean.getStrHomeMobileNo());
			objModel.setStrOfficeBuildingCode(officeBuildingCode);
			objModel.setStrOfficeBuildingName(officeBuildingName);
			objModel.setStrOfficeStreetName(objBean.getStrOfficeStreetName());
			objModel.setStrOfficeLandmark(objBean.getStrOfficeLandmark());

			objModel.setStrOfficeCity(objBean.getStrOfficeCity());
			objModel.setStrOfficePinCode(objBean.getStrOfficePinCode());
			objModel.setStrOfficeState(objBean.getStrOfficeState());
			objModel.setStrOfficeNo(objBean.getStrHomeMobileNo());
			objModel.setStrOfficeAddress(objBean.getStrOfficeCustAddress());

			objModel.setStrExternalCode("");
			objModel.setStrCustomerType("");
			objModel.setDteDOB("1990-01-01");
			objModel.setStrGender("Male");
			objModel.setStrEmailId("");
			objModel.setDteAnniversary("1990-01-01");

			objModel.setStrUserCreated(userCode);
			objModel.setStrUserEdited(userCode);

			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime());
			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime());

			objModel.setStrCRMId("N");			
			objModel.setStrDataPostFlag("N");
			objModel.setStrGSTNo("");
			objModel.setStrTempAddress(objBean.getStrTempCustAddress());
			objModel.setStrTempLandmark(objBean.getStrTempLandmark());
			objModel.setStrTempStreet(objBean.getStrTempStreetName());

			objModel.setStrOfficeArea("");

			objMasterService.funSaveCustomerMaster(objModel);

			req.getSession().setAttribute("success", true);
			req.getSession().setAttribute("successMessage", " " + customerMasterCode);

			return new ModelAndView("frmHomeDeliveryAddress", "command", objBean);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}

	}

	@RequestMapping(value = "/frmHomeDeliveryAddressForNewCustomer", method = RequestMethod.GET)
	public ModelAndView funHomeDeliveryAddressForNewCustomer(@ModelAttribute("command") @Valid clsHomeDeliveryAddressBean objBean, BindingResult result, Map<String, Object> model, @RequestParam(value = "strMobNo") String strMobNo, HttpServletRequest request)
	{

		try
		{

			objBean.setStrHomeMobileNo(strMobNo);
			objBean.setStrHomeCustomerName("");

			objBean.setStrHomeAddress("");
			objBean.setStrHomeCity("");
			objBean.setStrHomeCustomerName("");
			objBean.setStrHomeLandmark("");
			objBean.setStrHomePinCode("");

			objBean.setStrHomeState("");
			objBean.setStrHomeStreetName("");
			objBean.setStrOfficeCity("");
			objBean.setStrOfficeCustAddress("");
			objBean.setStrOfficeLandmark("");
			objBean.setStrOfficePinCode("");
			objBean.setStrOfficeState("");
			objBean.setStrOfficeStreetName("");
			objBean.setStrTempCustAddress("");
			objBean.setStrTempLandmark("");
			objBean.setStrTempStreetName("");

			objBean.setStrHomeBuildingName("");
			objBean.setStrOfficeBuildingName("");
			objBean.setStrTempBuildingName("");

			String selectedAddress = "Home";
			if (request.getSession().getAttribute("homeDeliveryAddress") != null)
			{
				selectedAddress = request.getSession().getAttribute("homeDeliveryAddress").toString();
			}
			model.put("selectedAddress", selectedAddress);

			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select a.strBuildingCode,a.strBuildingName from tblbuildingmaster a");
			List listOfBuildingName = objBaseServiceImpl.funGetList(sqlBuilder, "sql");

			if (listOfBuildingName != null && listOfBuildingName.size() > 0)
			{
				for (int i = 0; i < listOfBuildingName.size(); i++)
				{
					Object[] arrBuildings = (Object[]) listOfBuildingName.get(i);

					String buildingCode = arrBuildings[0].toString();
					String buildingName = arrBuildings[1].toString();

					mapBuildingNames.put(buildingCode, buildingName);
					mapBuildingCodes.put(buildingName, buildingCode);

				}
			}

			model.put("mapBuildingNames", mapBuildingNames);

			// strHomeBuildingName
			if (mapBuildingCodes.containsKey(objBean.getStrHomeBuildingName()))
			{
				model.put("strHomeBuildingName", mapBuildingCodes.get(objBean.getStrHomeBuildingName()));
			}

			// strOfficeBuildingName
			if (mapBuildingCodes.containsKey(objBean.getStrHomeBuildingName()))
			{
				model.put("strOfficeBuildingName", mapBuildingCodes.get(objBean.getStrOfficeBuildingName()));
			}

			// strTempBuildingName
			if (mapBuildingCodes.containsKey(objBean.getStrHomeBuildingName()))
			{
				model.put("strTempBuildingName", mapBuildingCodes.get(objBean.getStrTempBuildingName()));
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return new ModelAndView("frmHomeDeliveryAddress", "command", objBean);
	}

}
