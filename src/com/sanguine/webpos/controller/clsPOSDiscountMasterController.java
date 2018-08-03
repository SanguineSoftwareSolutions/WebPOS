package com.sanguine.webpos.controller;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
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

import com.sanguine.base.service.clsBaseServiceImpl;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsPosDiscountMasterBean;
import com.sanguine.webpos.sevice.clsMasterService;
import com.sanguine.webpos.util.clsUtilityController;

@Controller
public class clsPOSDiscountMasterController {
	@Autowired
	private clsGlobalFunctions objGlobal;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobal;
	@Autowired 
	private clsBaseServiceImpl objBaseServiceImpl;
	
	@Autowired
	private clsUtilityController objUtilityController;
	
	@Autowired
	clsMasterService objMasterService;
	
	Map map=new HashMap();
	@RequestMapping(value = "/frmDiscountMaster", method = RequestMethod.GET)
	public ModelAndView funOpenForm(@ModelAttribute("command") @Valid clsPosDiscountMasterBean objBean,BindingResult result,Map<String,Object> model, HttpServletRequest request){
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		
		model.put("urlHits",urlHits);
		
		String clientCode=request.getSession().getAttribute("clientCode").toString();
		
		List list=objMasterService.funFillPOSCombo(clientCode);
		
		map.put("All", "All");
		for(int cnt=0;cnt<list.size();cnt++)
		{
			Object obj=list.get(cnt);
			
			map.put(Array.get(obj, 0), Array.get(obj, 1));
		}
		model.put("posList", map);
		
		}catch(Exception e){
			urlHits="1";
		}
		
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmDiscountMaster_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmDiscountMaster");
		}else {
			return null;
		}
		 
	}
	
	@RequestMapping(value = "/savePOSDiscountMaster", method = RequestMethod.POST)
	public ModelAndView funAddUpdate(@ModelAttribute("command") @Valid clsPosDiscountMasterBean objBean,BindingResult result,HttpServletRequest req)
	{
		try
		{
			String clientCode=req.getSession().getAttribute("clientCode").toString();
			String webStockUserCode=req.getSession().getAttribute("usercode").toString();
	
//			String areaCode=objBean.getStrAreaCode();
//			
//			if (areaCode.trim().isEmpty())
//			{
//				long intCode =objUtilityController.funGetDocumentCodeFromInternal("Area");
//				areaCode = "A" + String.format("%03d", intCode);
//				
//				
//			}
//
//			clsAreaMasterModel objModel = new clsAreaMasterModel(new clsAreaMasterModel_ID(areaCode, clientCode));
//			objModel.setStrAreaName(objBean.getStrAreaName());
//			objModel.setStrPOSCode(objBean.getStrPOSName());
//			objModel.setDteDateCreated(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
//			objModel.setDteDateEdited(objGlobal.funGetCurrentDateTime("yyyy-MM-dd"));
//			objModel.setStrDataPostFlag("");
//			objModel.setStrUserCreated(webStockUserCode);
//			objModel.setStrUserEdited(webStockUserCode);
//
//			objMasterService.funSaveUpdateAreaMaster(objModel);
			
			
//			req.getSession().setAttribute("success", true);
//			req.getSession().setAttribute("successMessage"," "+areaCode);
									
			return new ModelAndView("redirect:/frmPOSAreaMaster.html");
		}
		catch(Exception ex)
		{
			
			ex.printStackTrace();
			return new ModelAndView("redirect:/frmFail.html");
		}
	}
	
	//Assign filed function to set data onto form for edit transaction.
			@RequestMapping(value = "/loadPOSDiscountMasterData", method = RequestMethod.GET)
			public @ResponseBody clsPosDiscountMasterBean funSetSearchFields(@RequestParam("POSAreaCode") String areaCode,HttpServletRequest req)
			{
				String clientCode=req.getSession().getAttribute("clientCode").toString();
				clsPosDiscountMasterBean objPOSDiscountMaster = new clsPosDiscountMasterBean();
				
//				clsAreaMasterModel objModel= (clsAreaMasterModel) objMasterService.funSelectedAreaMasterData(areaCode,clientCode);
//				objPOSAreaMaster.setStrAreaCode(objModel.getStrAreaCode());
//				objPOSAreaMaster.setStrAreaName(objModel.getStrAreaName());
//				objPOSAreaMaster.setStrPOSName(objModel.getStrPOSCode());
//
//				
//				if(null==objPOSAreaMaster)
//				{
//					objPOSAreaMaster = new clsPOSAreaMasterBean();
//					objPOSAreaMaster.setStrAreaCode("Invalid Code");
//				}
				
				return objPOSDiscountMaster;
			}
			
		
				
//			 @RequestMapping(value ="/checkDiscountName" ,method =RequestMethod.GET)
//				public  @ResponseBody boolean funCheckAreaName(@RequestParam("areaName")  String name,@RequestParam("areaCode")  String code,HttpServletRequest req) 
//				{
//					String clientCode =req.getSession().getAttribute("clientCode").toString();
//
//					int count=objPOSGlobal.funCheckName(name,code,clientCode,"POSAreaMaster");
//
//					if(count>0)
//					 return false;
//					else
//						return true;
//					
//				}
				
}
