package com.sanguine.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.model.clsLocationMasterModel;
import com.sanguine.model.clsTreeMasterModel;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsSecurityShellService;
import com.sanguine.service.clsStructureUpdateService;

@Controller
public class clsStructureUpdateController {
	
@Autowired
private clsStructureUpdateService objStructureUpdateService; 


	
	@Autowired
	private clsGlobalFunctionsService objGlobalFunctionsService;


	/**
	 * Load Data on Form
	 * @param model
	 * @param request
	 * @return
	 */
//	@SuppressWarnings({ "rawtypes", "unused" })
//	@RequestMapping(value = "/frmFillActionList", method = RequestMethod.GET)
//	public @ResponseBody List funListForm(Map<String, Object> model,HttpServletRequest request){
//		
//		String strModuleNo=request.getSession().getAttribute("moduleNo").toString();
//		List<clsTreeMasterModel> objModel = objSecurityShellService.funGetFormList(strModuleNo); 
//		
//		List<String> objMasters=new ArrayList<String>();
//		String strType = request.getParameter("strHeadingType").toString();
//		List list=new ArrayList();
//		List<clsTreeMasterModel> ListTrans=new ArrayList<clsTreeMasterModel>();
//		List<clsTreeMasterModel> ListMaster=new ArrayList<clsTreeMasterModel>();
//		List<clsTreeMasterModel> objReports=new ArrayList<clsTreeMasterModel>();
//		List<clsTreeMasterModel> objUtilitys=new ArrayList<clsTreeMasterModel>();
//				
//		for(Object ob : objModel)
//		{
//			List<String> objTransactions=new ArrayList<String>();
//			Object[] arrOb=(Object[])ob;
//			String type=arrOb[2].toString();
//			clsTreeMasterModel objTree=new clsTreeMasterModel();
//			switch (type)
//			{
//			//Master
//			case "M":
//				
//				objTree.setStrFormName(arrOb[0].toString());
//				objTree.setStrFormDesc(arrOb[1].toString());
//				objTree.setStrDelete("false");
//				ListMaster.add(objTree);
//				break;
//			//Tools
//			case "L":
//				objTree.setStrFormName(arrOb[0].toString());
//				objTree.setStrFormDesc(arrOb[1].toString());
//				objTree.setStrDelete("false");
//				objUtilitys.add(objTree);
//				break;
//			//Transaction	
//			case "T":
//				
//				objTree.setStrFormName(arrOb[0].toString());
//				objTree.setStrFormDesc(arrOb[1].toString());
//				objTree.setStrDelete("false");
//				ListTrans.add(objTree);
//				break;
//			//Report	
//			case "R":
//				objTree.setStrFormName(arrOb[0].toString());
//				objTree.setStrFormDesc(arrOb[1].toString());
//				objTree.setStrDelete("false");
//				objReports.add(objTree);
//				break;
//
//			}
//			
//		}
//		if(strType.equalsIgnoreCase("Transaction"))
//		{
//			list=ListTrans;
//		}
//		else if(strType.equalsIgnoreCase("Master"))
//		{
//			list=ListMaster;
//		}
//		//Return List
//		return list;
//	}

/**
 * Open Structure Update Form
 * @param req
 * @return
 */
	@RequestMapping(value = "/frmStructureUpdate", method = RequestMethod.GET)
	public ModelAndView funOpenStructureUpdateForm(Map<String, Object> model,HttpServletRequest req){
		
		
		String urlHits="1";
		try{
			urlHits=req.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmStructureUpdate_1");
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmStructureUpdate");
		}else {
			return null;
		}
		
				
	}
	/**
	 * Update Structure in Data base 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/updateStructure",  method = RequestMethod.GET)
	public @ResponseBody String funUpdateStructure(HttpServletRequest req){
		String clientCode="";
		if(null!=req.getSession().getAttribute("clientCode"))
		{
			clientCode=req.getSession().getAttribute("clientCode").toString();
		}
		 
		objStructureUpdateService.funUpdateStructure(clientCode);
		return "Structure Update Successfully";
	}
	/**
	 * Clear Transaction 
	 * @param frmName
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/ClearTransaction",  method = RequestMethod.GET)
	public @ResponseBody String funClearTransaction(@RequestParam(value="frmName") String frmName ,@RequestParam(value="propName") String propName,@RequestParam(value="locName") String locName,HttpServletRequest req){
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String str[]=frmName.split(",");
		
		objStructureUpdateService.funClearTransaction(clientCode,str);
		return "Transaction Clear Successfully";
	}
	/**
	 * Clear Master
	 * @param frmName
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/ClearMaster",  method = RequestMethod.GET)
	public @ResponseBody String funClearMaster(@RequestParam(value="frmName") String frmName,HttpServletRequest req){
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String str[]=frmName.split(",");
		objStructureUpdateService.funClearMaster(clientCode,str);
		return "Master Clear Successfully";
	}
	
	@RequestMapping(value="/loadPropertyName",method =RequestMethod.GET)
	public @ResponseBody List funLoadPropertyMaster(@RequestParam(value="propName") String propName ,HttpServletRequest req )
	{
		
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		List<String> listPropertyName = new ArrayList<>();
		String sqlPropertyName="select strPropertyName from tblpropertymaster where strClientCode='"+clientCode+"' ";
		listPropertyName=objGlobalFunctionsService.funGetDataList(sqlPropertyName, "sql");
		return listPropertyName;
	}
	
	@RequestMapping(value="/loadLocName",method =RequestMethod.GET)
	public @ResponseBody List funLoadLoctionMaster(@RequestParam(value="propName") String propName ,HttpServletRequest req )
	{
		
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		List<String> listLocName = new ArrayList<>();
		String sqlLocName="select a.strLocName from tbllocationmaster a ,tblpropertymaster b "
				+ "where a.strPropertyCode=b.strPropertyCode and b.strPropertyName='"+propName+"' and a.strClientCode='"+clientCode+"' ";
		listLocName=objGlobalFunctionsService.funGetDataList(sqlLocName, "sql");
		return listLocName;
	}
	
	
	
}

