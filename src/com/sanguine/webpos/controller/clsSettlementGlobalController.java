package com.sanguine.webpos.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.webpos.bean.clsBillDetails;

@Controller
public class clsSettlementGlobalController {
	
	@Autowired
	private clsPOSDirectBillerController obPOSDirectBillerController;
	
	
	public clsBillDetails obBillItem;
	
	@RequestMapping(value="/frmPOSSettlementGlobalController",method=RequestMethod.GET) 
	public ModelAndView funOpenSettlementWindow(Map<String,Object> model,HttpServletRequest request,@RequestParam("OperationFrom") String operationFrom)
	{
//		if(obPOSDirectBillerController.obBillItem!=null)
//		obBillItem=obPOSDirectBillerController.obBillItem;
//	
		
		/*listOfPunchedItemsDtl=new ArrayList<clsBillItemDtlBean>();
		listOfPunchedItemsDtl=obPOSDirectBillerController.listOfDirectBillerBillItemDtl; //load list from direct biller
	*/	

		return new ModelAndView("redirect:/frmPOSBillSettlement.html?OperationFrom="+operationFrom);
//		return new ModelAndView("redirect:/frmBillSettlement.html?OperationFrom="+operationFrom);
	}

}
