package com.sanguine.webpos.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPOSCustomerTypeMasterBean;
import com.sanguine.webpos.model.clsCustomerTypeMasterModel;
import com.sanguine.webpos.model.clsCustomerTypeMasterModel_ID;
import com.sanguine.webpos.sevice.clsMasterService;
import com.sanguine.webpos.util.clsUtilityController;

@Controller
public class clsPOSCustomerTypeMasterController {
	
	@Autowired
	private clsGlobalFunctions objGlobal;
	
	@Autowired
	private  clsPOSGlobalFunctionsController objPOSGlobal;
	
	@Autowired
	private clsUtilityController objUtilityController;
	
	@Autowired
	clsMasterService objMasterService;

	@RequestMapping(value = "/frmPOSCustomerTypeMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)

	{
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCustomerTypeMaster_1","command", new clsPOSCustomerTypeMasterBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSCustomerTypeMaster","command", new clsPOSCustomerTypeMasterBean());
		}else {
			return null;
		}
		 
	}
	
	
	 @RequestMapping(value ="/checkCustomerTypeName" ,method =RequestMethod.GET)
		public  @ResponseBody boolean funCheckAreaName(@RequestParam("strTypeMasterCode")  String code,@RequestParam("strCustomerType")  String Name,HttpServletRequest req) 
		{
			String clientCode =req.getSession().getAttribute("clientCode").toString();

			int count=objPOSGlobal.funCheckName(Name,code,clientCode,"POSCustomerTypeMaster");

			if(count>0)
			 return false;
			else
				return true;
			
		}
	
	@RequestMapping(value = "/savePOSCustomerTypeMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPOSCustomerTypeMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		String urlHits="1";
		
		try
		{
			urlHits=req.getParameter("saddr").toString();
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("usercode").toString();
			String customerTypeMasterCode = objBean.getStrCustomerTypeMasterCode();
						
			 if (customerTypeMasterCode.trim().isEmpty())
			    {
			    	long lngCode=objUtilityController.funGetDocumentCodeFromInternal("POSCustomerTypeMaster");
			    	if(lngCode>0)
			    	{
			    		customerTypeMasterCode = "CT" + String.format("%03d", lngCode);
			    	}
			    }
			    clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel(new clsCustomerTypeMasterModel_ID(customerTypeMasterCode,clientCode));
			    //clsCustomerTypeMasterModel objModel = new clsCustomerTypeMasterModel(new clsCustomerTypeMasterModel_ID(customerTypeMasterCode, clientCode));
			    objModel.setStrCustType(objBean.getStrCustomerType());
			    objModel.setDblDiscPer(objBean.getDblDiscount());
			    objModel.setStrPlayZoneCustType(objBean.getStrPlayZoneCustType());
			    objModel.setStrClientCode(clientCode);
			    objModel.setStrUserCreated(webStockUserCode);
			    objModel.setStrUserEdited(webStockUserCode);
			    objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
			    objModel.setStrDataPostFlag("N");
			    objMasterService.funSaveUpdateCustomerTypeMaster(objModel);
			
			req.getSession().setAttribute("success", true);
		
			req.getSession().setAttribute("successMessage"," "+customerTypeMasterCode);
									
			return new ModelAndView("redirect:/frmPOSCustomerTypeMaster.html?saddr="+urlHits);
		}
		catch(Exception ex)
		{
			urlHits="1";
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	//Assign filed function to set data onto form for edit transaction.
			@RequestMapping(value = "/loadPOSCustomerTypeMasterData", method = RequestMethod.GET)
			public @ResponseBody clsPOSCustomerTypeMasterBean funSetSearchFields(@RequestParam("POSCustomerTypeCode") String CustomerTypeCode,HttpServletRequest req)
			{
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				clsPOSCustomerTypeMasterBean objPOSCustomerTypeMaster = new clsPOSCustomerTypeMasterBean();
				
				clsCustomerTypeMasterModel objModel= (clsCustomerTypeMasterModel) objMasterService.funSelectedCustomerTypeMasterData(CustomerTypeCode,clientCode);
				objPOSCustomerTypeMaster = new clsPOSCustomerTypeMasterBean();
				objPOSCustomerTypeMaster.setStrCustomerTypeMasterCode(objModel.getStrCustTypeCode());
				objPOSCustomerTypeMaster.setStrCustomerType(objModel.getStrCustType());
				objPOSCustomerTypeMaster.setDblDiscount(objModel.getDblDiscPer());
				
				if(null==objPOSCustomerTypeMaster)
				{
					objPOSCustomerTypeMaster = new clsPOSCustomerTypeMasterBean();
					objPOSCustomerTypeMaster.setStrCustomerTypeMasterCode("Invalid Code");
				}
				
				return objPOSCustomerTypeMaster;
			}

	
}
