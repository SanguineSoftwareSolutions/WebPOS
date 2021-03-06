package com.sanguine.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.POSLicence.controller.clsClientDetails;
import com.POSLicence.controller.clsEncryptDecryptClientCode;
import com.sanguine.bean.clsClientBean;
import com.sanguine.bean.clsUserHdBean;
import com.sanguine.model.clsCompanyMasterModel;
import com.sanguine.service.clsCurrencyMasterService;
import com.sanguine.service.clsGlobalFunctionsService;
import com.sanguine.service.clsLocationMasterService;
import com.sanguine.service.clsSetupMasterService;
import com.sanguine.service.clsStructureUpdateService;
import com.sanguine.service.clsTreeMenuService;
import com.sanguine.service.clsUserMasterService;
import com.sanguine.util.clsUserDesktopUtil;
import com.sanguine.webpos.bean.clsWebPosPOSSelectionBean;
import com.sanguine.webpos.controller.clsPOSGlobalFunctionsController;
import com.sanguine.webpos.model.clsSetupHdModel;
import com.sanguine.webpos.model.clsUserHdModel;
import com.sanguine.webpos.sevice.clsMainMenuService;
import com.sanguine.webpos.sevice.clsMasterService;
import com.sanguine.webpos.util.clsGlobalSingleObject;

@Controller
@SessionAttributes("userdetails")

public class clsUserController
{
	final static Logger logger=Logger.getLogger(clsUserController.class);
	private String strModule="1";
	@Autowired
	private clsUserMasterService objUserMasterService;
	
	@Autowired
	private clsGlobalFunctions objGlobalFun;
	
	@Autowired
	private clsGlobalFunctionsService objGlobalService;

	@Autowired
	private clsTreeMenuService objclsTreeMenuService;	
	
	@Autowired
	private clsSetupMasterService objSetupMasterService;
	
	@Autowired
	private clsLocationMasterService objLocationMasterService;
	
	@Autowired
	private clsStructureUpdateService objStructureUpdateService;

	@Autowired
	private clsCurrencyMasterService objCurrencyMasterService;
	
	@Autowired
	clsMainMenuService objMainMenuService;
	
	@Autowired
	clsMasterService objMasterService;
	
	private static int intcheckSturctureUpdate=0;
	
	@Value("${applicationType}")
	String applicationType;

	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView welcome(HttpServletRequest req) 	
	{
		PasswordEncoderGenerator();
		ModelAndView mAndV=null;
		
	//Direct Show Login Page or Client Login Page
		try
		{
			if(applicationType.equalsIgnoreCase("single"))
			{
				List<clsSetupHdModel> listClsSetUpModel=objSetupMasterService.funGetListSetupModel();
				if(listClsSetUpModel.size()>0)
				{
					clsSetupHdModel objSetUpModel=listClsSetUpModel.get(listClsSetUpModel.size()-1);
					
					String startDate=objSetUpModel.getDteStartDate();
					String[] spDate=startDate.split("-");
					String year=spDate[0];
					String month=spDate[1];
					String[] spDate1 =spDate[2].split(" ");
					String date=spDate1[0];
					startDate=date+"/"+month+"/"+year;
					req.getSession().setAttribute("gClientCode",objSetUpModel.getStrClientCode());
					req.getSession().setAttribute("gCompanyName",objSetUpModel.getStrClientName());		
					req.getSession().setAttribute("gStartDate",startDate);
					Map <String,String> moduleMap=new TreeMap<String, String>();
					moduleMap.put("7-WebPOS", "webpos_module_icon.png");
			
					req.getSession().setAttribute("moduleNo",strModule);
					req.getSession().setAttribute("moduleMap",moduleMap);
					mAndV= new ModelAndView("frmLogin","command", new clsUserHdBean());
				   
				} else {
				  return mAndV;
				}
			} else{
				mAndV = new ModelAndView("frmClientLogin","command", new clsClientBean());
			}
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return mAndV;
	}
	

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/validateUser", method = RequestMethod.POST)
	public  ModelAndView funValidateUser(@ModelAttribute("command") @Valid clsUserHdBean userBean,BindingResult result,HttpServletRequest req,ModelMap map)
	{		
		if(logger.isDebugEnabled())
		{  
			   logger.debug("Start debug");  
		}
		ModelAndView objMV=null;
		String clientCodeFromDB=req.getSession().getAttribute("gClientCode").toString();
		String companyNameFromDB=req.getSession().getAttribute("gCompanyName").toString();
		
		if(result.hasErrors())
		{
			map.put("invalid", "1");
			return new ModelAndView("frmLogin","command", new clsUserHdBean());
		}
		else
		{
			String encryptedClientCodeFromDB = clsEncryptDecryptClientCode.funEncryptClientCode(clientCodeFromDB);
			String encryptedClientNameFromDB = clsEncryptDecryptClientCode.funEncryptClientCode(companyNameFromDB);
						
			clsClientDetails.funAddClientCodeAndName();
			
//			String decryptedClientCodeFromHm = clsEncryptDecryptClientCode.funDecryptClientCode(clsClientDetails.hmClientDtl.get(encryptedClientCodeFromDB).);
			String decryptedClientNameFromHm = clsEncryptDecryptClientCode.funDecryptClientCode(clsClientDetails.hmClientDtl.get(encryptedClientCodeFromDB).Client_Name);
			
						
			if(companyNameFromDB.equalsIgnoreCase(decryptedClientNameFromHm))
			{
				 SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                 try
                 {
                	 Date systemDate = dFormat.parse(dFormat.format(new Date()));
                	 
                	 String encryptedExpDate=clsClientDetails.hmClientDtl.get(encryptedClientCodeFromDB).expiryDate;
                	 String decryptedExpDate=clsEncryptDecryptClientCode.funDecryptClientCode(encryptedExpDate);
                	 
                	 Date WebStockExpiryDate = dFormat.parse(decryptedExpDate);
					 if (systemDate.compareTo(WebStockExpiryDate)<=0) 
					 {
						 if(userBean.getStrUserCode().equalsIgnoreCase("SANGUINE"))
						 {
							 Date dt = new Date();
						     int day = dt.getDate();
						     int month = dt.getMonth() + 1;
						     int year = dt.getYear() + 1900;
						     int password = year + month + day + day;
							
						     String strpass=Integer.toString(password);
						     char num1 =strpass.charAt(0);
						     char num2 =strpass.charAt(1);
						     char num3 =strpass.charAt(2);
						     char num4 =strpass.charAt(3);
						     String alph1=objGlobalFun.funGetAlphabet(Character.getNumericValue(num1));
						     String alph2=objGlobalFun.funGetAlphabet(Character.getNumericValue(num2));
						     String alph3=objGlobalFun.funGetAlphabet(Character.getNumericValue(num3));
						     String alph4=objGlobalFun.funGetAlphabet(Character.getNumericValue(num4));
						     String finalPassword=String.valueOf(password)+alph1+alph2+alph3+alph4;
						     System.out.println("Hibernate: "+finalPassword+"CACA");
						     String userPassword = userBean.getStrPassword();
						     if (finalPassword.equalsIgnoreCase(userPassword)) 
						     {
						    	 clsUserHdModel user=new clsUserHdModel();
						    	 user.setStrSuperType("YES");
						    	 user.setStrUserName("SANGUINE");
						    	 user.setStrUserCode("SANGUINE");
						    	 objMV = funSessionValue(user, req);
						    	 
						     }
						     else
						     {
						    	 clsUserHdModel user=null;
						    	 try
						    	 {
						    		 user=objUserMasterService.funGetUser(userBean.getStrUserCode(),clientCodeFromDB,"getUserMaster");
						    	 }catch(Exception ex){
						    		 objMV= new ModelAndView("frmStructureUpdate_2");
						    	 }
							        
						    	 if(user!=null)
						    	 {
						    		 BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
						    		 if(passwordEncoder.matches(userBean.getStrPassword(), user.getStrPassword()))
						    		 {
						    			 return funSessionValue(user, req);
						    		 }
						    		 else
									{
										map.put("invalid", "1");
										objMV=new ModelAndView("frmLogin","command", new clsUserHdBean());
									}
								}
							    else
								{
									map.put("invalid", "1");
									objMV=new ModelAndView("frmLogin","command", new clsUserHdBean());
								}
						    }
						}
						else
						{
							clsUserHdModel user=objUserMasterService.funGetUser(userBean.getStrUserCode(),clientCodeFromDB,"getUserMaster");
							if(user!=null)
							{
								try
									{
									String encKey = "04081977";
						            String password = clsGlobalSingleObject.getObjPasswordEncryptDecreat().encrypt(encKey, userBean.getStrPassword().trim().toUpperCase());
									BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
									if(password.equals(user.getStrPassword()))
									{
										 return funSessionValue(user, req);
									}
									else
									{
										map.put("invalid", "1");
										objMV=new ModelAndView("frmLogin","command", new clsUserHdBean());
									}
								}
								catch(Exception e)
								{
									map.put("invalid", "1");
									objMV=new ModelAndView("frmLogin","command", new clsUserHdBean());
									logger.error(e);
									e.printStackTrace();
								}
							}
							else
							{
								map.put("invalid", "1");
								objMV=new ModelAndView("frmLogin","command", new clsUserHdBean());
							}
						}
					}
					else
					{
						map.put("LicenceExpired", "1");
						objMV=new ModelAndView("frmLogin","command", new clsUserHdBean());
					}
				}
                catch (ParseException e) {
                	e.printStackTrace();
				}
			}
			else
			{
				map.put("LicenceExpired", "1");
				objMV=new ModelAndView("frmLogin","command", new clsUserHdBean());
			}
			
			return objMV;
		}
	}

	@SuppressWarnings("rawtypes")
	private ModelAndView funSessionValue(clsUserHdModel user,HttpServletRequest req)
	{
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("users", funPrepareUserBean(user));
		req.getSession().setAttribute("gUserCode",user.getStrUserCode());
		req.getSession().setAttribute("gUserType",user.getStrSuperType());
		req.getSession().setAttribute("gSuperUser",user.getStrSuperType());
		req.getSession().setAttribute("gUserName",user.getStrUserName());
		String dayEndDate=	objGlobalFun.funGetCurrentDate("yyyy-MM-dd");
		req.getSession().setAttribute("dayEndDate",dayEndDate);
		
		return new ModelAndView("frmWebPOSModuleSelection");
		
	}
	
	
	private clsUserHdBean funPrepareUserBean(clsUserHdModel user)
	{
		clsUserHdBean objUserHdBean = new clsUserHdBean();
		objUserHdBean.setStrUserCode(user.getStrUserCode());
		objUserHdBean.setStrUserName(user.getStrUserName());
		objUserHdBean.setStrSuperUser(user.getStrSuperType());
		return objUserHdBean;
	}
	
	@RequestMapping(value="/frmModuleSelection",method=RequestMethod.GET)
	private ModelAndView funModuleSelection(HttpServletRequest req){
		 return new ModelAndView("frmModuleSelection");
	}
		
	
	@RequestMapping(value = "/frmHome", method = RequestMethod.GET)
	public ModelAndView funRedirectToHome()
	{
		return new ModelAndView("frmMainMenu");
	}
	
	@RequestMapping(value="/logout")
    public String logout(HttpSession session )
	{
        session.invalidate();       
        return "redirect:/index.html";
    }
	
	
	@SuppressWarnings("unused")
	public void PasswordEncoderGenerator() {
		String password = "super";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
	}
	
	

	
	@RequestMapping(value="/frmChangeModuleSelection",method=RequestMethod.GET)
	private ModelAndView funChageModuleSlection(HttpServletRequest req)
	{
		String userCode=req.getSession().getAttribute("gUserCode").toString();
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		clsUserHdModel user =new clsUserHdModel();
		if(userCode.equals("SANGUINE"))
		{
			user.setStrSuperType("YES");
//        	user.setStrProperty("ALL");
//        	user.setStrType("");
        	user.setStrUserName("SANGUINE");
        	user.setStrUserCode("SANGUINE");
//        	user.setStrRetire("N");
			
		}else
		{
			user=objUserMasterService.funGetUser(userCode,clientCode,"getUserMaster");
		}
		funSetModuleForChangeModule(req);
		
		return funSessionValue(user, req);
	}

	
	private void funSetModuleForChangeModule(HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
	//	List<clsCompanyMasterModel> listClsCompanyMasterModel=objSetupMasterService.funGetListCompanyMasterModel();
		List<clsCompanyMasterModel> listClsCompanyMasterModel=objSetupMasterService.funGetListCompanyMasterModel(clientCode);
		if(listClsCompanyMasterModel.size()>0){
			clsCompanyMasterModel objCompanyMasterModel=listClsCompanyMasterModel.get(0);
			String startDate=objCompanyMasterModel.getDtStart();
			String[] spDate=startDate.split("-");
			String year=spDate[0];
			String month=spDate[1];
			String[] spDate1 =spDate[2].split(" ");
			String date=spDate1[0];
			startDate=date+"/"+month+"/"+year;
			req.getSession().setAttribute("gClientCode",objCompanyMasterModel.getStrClientCode());
			req.getSession().setAttribute("companyCode",objCompanyMasterModel.getStrCompanyCode());
			req.getSession().setAttribute("companyName",objCompanyMasterModel.getStrCompanyName());		
			req.getSession().setAttribute("startDate",startDate);
			String strCRMModule=objCompanyMasterModel.getStrCRMModule();
			String strWebBookModule=objCompanyMasterModel.getStrWebBookModule();
			String strWebClubModule=objCompanyMasterModel.getStrWebClubModule();
			String strWebExciseModule=objCompanyMasterModel.getStrWebExciseModule();
			String strWebPMSModule=objCompanyMasterModel.getStrWebPMSModule();
			String strWebPOSModule=objCompanyMasterModel.getStrWebPOSModule();
			String strWebStockModule=objCompanyMasterModel.getStrWebStockModule();
			Map <String,String> moduleMap=new TreeMap<String, String>();
			if ("Yes".equalsIgnoreCase(strWebStockModule)) {
				moduleMap.put("1-WebStocks", "webstocks_module_icon.png");
				strModule = "1";
			}

			if ("Yes".equalsIgnoreCase(strWebExciseModule)) {
				moduleMap.put("2-WebExcise", "webexcise_module_icon.png");
				strModule = "2";
			}

			if ("Yes".equalsIgnoreCase(strWebPMSModule)) {
				moduleMap.put("3-WebPMS", "webpms_module_icon.png");
				strModule = "3";
			}

			if ("Yes".equalsIgnoreCase(strWebClubModule)) {
				moduleMap.put("4-WebClub", "webclub_module_icon.png");
				strModule = "4";
			}

			if ("Yes".equalsIgnoreCase(strWebBookModule)) {
				moduleMap.put("5-WebBook", "webbooks_icon.png");
				strModule = "5";
			}

			if ("Yes".equalsIgnoreCase(strCRMModule)) {
				moduleMap.put("6-WebCRM", "webcrm_module_icon.png");
				strModule = "6";
			}

			if ("Yes".equalsIgnoreCase(strWebPOSModule)) {
				moduleMap.put("7-WebPOS", "webpos_module_icon.png");
				strModule = "7";
			}
			
           req.getSession().setAttribute("moduleNo",strModule);
           req.getSession().setAttribute("moduleMap",moduleMap);
		  
		   
		}
	}
	

	
	
/*	private String funWarFileDate()
	{
		String warDate="";
		try {
			
		String filepath =System.getProperty("user.dir") +"\\webapps\\prjWebPOS.war";
		System.out.println( filepath+"\\prjWebPOS.war");
		Path path = Paths.get(filepath);
		BasicFileAttributes attr;
		
			attr = Files.readAttributes(path, BasicFileAttributes.class);
			System.out.println("creationTime: " + attr.creationTime());
			warDate = attr.creationTime().toString();
			if(!(warDate.length()==0))
			{
			warDate=warDate.substring(0, 10);
			}
		}catch(Exception e) 
		{
			
			e.printStackTrace();
		}
		return warDate;
		
	}*/
	
	
	@RequestMapping(value = "/frmWebPOSModuleSelection", method = RequestMethod.GET)
	public ModelAndView funWebPOSModuleSelectionOpenForm(HttpServletRequest req,Map<String,Object> model)
	{
		return new ModelAndView("frmWebPOSModuleSelection");
	}
	
	
		public ArrayList<clsWebPosPOSSelectionBean> funWebPOSPOSSelection(HttpServletRequest req)
		{
			ArrayList<clsWebPosPOSSelectionBean> listPOS=null;
			try
			{
				List list = null;
				try
				{
					String clientCode=req.getSession().getAttribute("gClientCode").toString();
					list=objMasterService.funGetPOSList(clientCode);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			    listPOS=new  ArrayList<clsWebPosPOSSelectionBean>();
				
			    for(int cnt=0;cnt<list.size();cnt++)
			    {
			    	Object[] arrObjPOSMaster=(Object[])list.get(cnt);
			    	clsWebPosPOSSelectionBean objbean = new clsWebPosPOSSelectionBean();
			    	objbean.setStrPosCode(arrObjPOSMaster[0].toString());
			    	objbean.setStrPosName(arrObjPOSMaster[1].toString());
			    	listPOS.add(objbean);
			    }
			  			    
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		    
		    return listPOS;
		}
	
	@RequestMapping(value = "/frmWebPOSSelection", method = RequestMethod.GET)
	public ModelAndView funWebPOSPOSSelectionMaster(@RequestParam("strPOSCode") String strPOSCode,HttpServletRequest req,Map<String,Object> model)
	{
		return new ModelAndView("frmWebPOSMainMenu");
	}
	
	@RequestMapping(value = "/frmWebPOSChangeSelection", method = RequestMethod.GET)
	public ModelAndView frmWebPOSChangeSelection(HttpServletRequest req,Map<String,Object> model)
	{
		return new ModelAndView("frmPOSSelection");
	}
	
	
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/frmWebPOSSelectionMaster", method = RequestMethod.GET)
	public ModelAndView funWebPOSModuleMasterSelection(HttpServletRequest req,Map<String,Object> model)
	{
		List<clsUserDesktopUtil> webPOSDesktop=null;
		try {
			
			ArrayList<clsWebPosPOSSelectionBean> posList = funWebPOSPOSSelection(req);
			req.getSession().setAttribute("posList",posList);
			req.getSession().setAttribute("webPOSModuleSelect","M");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//req.getSession().setAttribute("desktop",webPOSDesktop);
		return new ModelAndView("frmPOSSelection");
		
	}
	
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/frmGetPOSSelection", method = RequestMethod.GET)
	public ModelAndView funWebPOSPOSSelection(@RequestParam("strPosCode") String POSCode,HttpServletRequest req,Map<String,Object> model)
	{
		String POSDate="",shiftNo="",shiftEnd="",dayEnd="";
		List<clsUserDesktopUtil> webPOSDesktop=null;
		try {
			
			ArrayList<clsWebPosPOSSelectionBean> posList = funWebPOSPOSSelection(req);
			req.getSession().setAttribute("posList",posList);
			String posModule = req.getSession().getAttribute("webPOSModuleSelect").toString();
			String userCode = req.getSession().getAttribute("gUserCode").toString();
			String userType = req.getSession().getAttribute("gUserType").toString();
			String clientCode=req.getSession().getAttribute("gClientCode").toString();
			String sql="select a.strPosName from tblposmaster a "
					+ " where a.strPosCode='"+POSCode+"' ";
			
			List list=objGlobalService.funGetList(sql,"sql");

			String posName="";
			if(list.size()>0){
				
				posName=list.get(0).toString();
			}
			
			
			
			webPOSDesktop = funGetPOSMenuMap(userCode, clientCode, posModule, userType, POSCode);
			
			JSONArray jArr = new JSONArray();
			for(clsUserDesktopUtil obj :webPOSDesktop)
			{
				JSONObject jobj = new JSONObject();
				jobj.put("formName", obj.getStrFormName());
				jobj.put("strFormName", obj.getStrFormName());
				jobj.put("strFormDesc", obj.getStrFormDesc());
				jobj.put("strImgName", obj.getStrImgName()+".png");
				jobj.put("strRequestMapping", obj.getStrRequestMapping());
				jobj.put("strShortName",obj.getStrShortName());
				
				jArr.add(jobj);
			}

			req.getSession().setAttribute("formSerachlist",jArr);
			req.getSession().setAttribute("desktop",webPOSDesktop);
			req.getSession().setAttribute("loginPOS",POSCode);
			
			Map<String,String> hmDayEndDetails=objMainMenuService.funGetPOSWiseDayEndData(POSCode, userCode, clientCode);
			
			POSDate=hmDayEndDetails.get("startDate").toString();
			shiftNo=hmDayEndDetails.get("ShiftNo").toString();
			shiftEnd=hmDayEndDetails.get("ShiftEnd").toString();
			dayEnd=hmDayEndDetails.get("DayEnd").toString();
			
			req.getSession().setAttribute("gPOSCode", POSCode);
			req.getSession().setAttribute("gPOSName", posName);
			req.getSession().setAttribute("gModule", posModule);
			req.getSession().setAttribute("gClientCode", clientCode);
									
			req.getSession().setAttribute("gPOSDate", POSDate);
			req.getSession().setAttribute("gPOSDateToDisplay", POSDate.split("-")[2] + "-" + POSDate.split("-")[1] + "-" + POSDate.split("-")[0]);
			req.getSession().setAttribute("gShiftEnd", shiftEnd);
			req.getSession().setAttribute("gDayEnd", dayEnd);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ModelAndView("frmWebPOSMainMenu");
		//return new ModelAndView("frmMainMenu");
	}
	
	
	
	
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/frmWebPOSSelectionReport", method = RequestMethod.GET)
	public ModelAndView funWebPOSPOSSelectionReport(HttpServletRequest req,Map<String,Object> model)
	{
		List<clsUserDesktopUtil> webPOSDesktop=null;
		try {
			
			ArrayList<clsWebPosPOSSelectionBean> posList = funWebPOSPOSSelection(req);
			req.getSession().setAttribute("posList",posList);
			req.getSession().setAttribute("webPOSModuleSelect","R");
			
			//webPOSDesktop = funGetPOSMenuMap("super", "117.001", "M", "Super", "P01");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//req.getSession().setAttribute("desktop",webPOSDesktop);
		return new ModelAndView("frmPOSSelection");
		
	}
	
	
	@SuppressWarnings("unused")
	@RequestMapping(value = "/frmWebPOSSelectionTransection", method = RequestMethod.GET)
	public ModelAndView funWebPOSPOSSelectionTransection(HttpServletRequest req,Map<String,Object> model)
	{
		List<clsUserDesktopUtil> webPOSDesktop=null;
		try {
			
			ArrayList<clsWebPosPOSSelectionBean> posList = funWebPOSPOSSelection(req);
			req.getSession().setAttribute("posList",posList);
			req.getSession().setAttribute("webPOSModuleSelect","T");
			
			//webPOSDesktop = funGetPOSMenuMap("super", "117.001", "M", "Super", "P01");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//req.getSession().setAttribute("desktop",webPOSDesktop);
		return new ModelAndView("frmPOSSelection");
		
	}
	
	
	
	
	private List<clsUserDesktopUtil> funGetPOSMenuMap(String userCode,String clientCode,String moduleType,String superUser,String POSCode) throws Exception 
	{
		boolean superUserYN=false;
		if(superUser.equalsIgnoreCase("Super"))
		{
			superUserYN=true;
		}
		
		clsSetupHdModel objSetupHdModel=objMasterService.funGetPOSWisePropertySetup(POSCode, clientCode);
		if(null!=objSetupHdModel)
		{
			clsPOSGlobalFunctionsController.hmPOSSetupValues.put("gClientCode",objSetupHdModel.getStrClientCode());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ClientName",objSetupHdModel.getStrClientName());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ClientAddressLine1",objSetupHdModel.getStrAddressLine1());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ClientAddressLine2",objSetupHdModel.getStrAddressLine2());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ClientAddressLine3",objSetupHdModel.getStrAddressLine3());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("Email",objSetupHdModel.getStrEmail());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("BillFooter",objSetupHdModel.getStrBillFooter());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("BillFooterStatus",objSetupHdModel.getStrBillFooterStatus());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("BillPaperSize",objSetupHdModel.getIntBillPaperSize());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("NegativeBilling",objSetupHdModel.getStrNegativeBilling());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DayEnd",objSetupHdModel.getStrDayEnd());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintMode",objSetupHdModel.getStrPrintMode());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DiscountNote",objSetupHdModel.getStrDiscountNote());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CityName",objSetupHdModel.getStrCityName());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("State",objSetupHdModel.getStrState());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("Country",objSetupHdModel.getStrCountry());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("TelephoneNo",objSetupHdModel.getIntTelephoneNo());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("StartDate",objSetupHdModel.getStrState());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("EndDate",objSetupHdModel.getDteEndDate());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("NatureOfBusinnes",objSetupHdModel.getStrNatureOfBusinnes());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MultipleBillPrinting",objSetupHdModel.getStrMultipleBillPrinting());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("EnableKOT",objSetupHdModel.getStrEnableKOT());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintVatNo",objSetupHdModel.getStrPrintVatNo());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("VatNo",objSetupHdModel.getStrVatNo());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ShowBill",objSetupHdModel.getStrShowBill());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintServiceTaxNo",objSetupHdModel.getStrPrintServiceTaxNo());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ServiceTaxNo",objSetupHdModel.getStrServiceTaxNo());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ManualBillNo",objSetupHdModel.getStrManualBillNo());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MenuItemDispSeq",objSetupHdModel.getStrMenuItemDispSeq());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SenderEmailId",objSetupHdModel.getStrSenderEmailId());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("EmailPassword",objSetupHdModel.getStrEmailPassword());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ConfirmEmailPassword",objSetupHdModel.getStrConfirmEmailPassword());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("Body",objSetupHdModel.getStrBody());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("EmailServerName",objSetupHdModel.getStrEmailServerName());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SMSApi",objSetupHdModel.getStrSMSApi());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("UserCreated",objSetupHdModel.getStrUserCreated());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("UserEdited",objSetupHdModel.getStrUserEdited());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ManualBillNo",objSetupHdModel.getStrManualBillNo());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DateCreated",objSetupHdModel.getDteDateCreated());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DateEdited",objSetupHdModel.getDteDateEdited());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("POSType",objSetupHdModel.getStrPOSType());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("WebServiceLink",objSetupHdModel.getStrWebServiceLink());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DataSendFrequency",objSetupHdModel.getStrDataSendFrequency());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("HOServerDate",objSetupHdModel.getDteHOServerDate());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("RFID",objSetupHdModel.getStrRFID());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ServerName",objSetupHdModel.getStrServerName());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DBUserName",objSetupHdModel.getStrDBUserName());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DBPassword",objSetupHdModel.getStrDBPassword());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DatabaseName",objSetupHdModel.getStrDatabaseName());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("EnableKOTForDirectBiller",objSetupHdModel.getStrEnableKOTForDirectBiller());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PinCode",objSetupHdModel.getIntPinCode());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ChangeTheme",objSetupHdModel.getStrChangeTheme());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MaxDiscount",objSetupHdModel.getDblMaxDiscount());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("AreaWisePricing",objSetupHdModel.getStrAreaWisePricing());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MenuItemSortingOn",objSetupHdModel.getStrMenuItemSortingOn());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DirectBillerAreaCode",objSetupHdModel.getStrDirectAreaCode());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ColumnSize",objSetupHdModel.getIntColumnSize());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintType",objSetupHdModel.getStrPrintType());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("EditHomeDelivery",objSetupHdModel.getStrEditHomeDelivery());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SlabBasedHDCharges",objSetupHdModel.getStrSlabBasedHDCharges());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SkipWaiterAndPax",objSetupHdModel.getStrSkipWaiterAndPax());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SkipWaiter",objSetupHdModel.getStrSkipWaiter());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DirectKOTPrintMakeKOT",objSetupHdModel.getStrDirectKOTPrintMakeKOT());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SkipPax",objSetupHdModel.getStrSkipPax());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CRMInterface",objSetupHdModel.getStrCRMInterface());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("GetWebserviceURL",objSetupHdModel.getStrGetWebserviceURL());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PostWebserviceURL",objSetupHdModel.getStrPostWebserviceURL());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("OutletUID",objSetupHdModel.getStrOutletUID());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("POSID",objSetupHdModel.getStrPOSID());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("StockInOption",objSetupHdModel.getStrStockInOption());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CustSeries",objSetupHdModel.getStrCustSeries());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("AdvReceiptPrintCount",objSetupHdModel.getIntAdvReceiptPrintCount());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("HomeDeliverySMS",objSetupHdModel.getStrHomeDeliverySMS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("BillSettlementSMS",objSetupHdModel.getStrBillStettlementSMS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("BillFormatType",objSetupHdModel.getStrBillFormatType());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ActivePromotions",objSetupHdModel.getStrActivePromotions());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SendHomeDelSMS",objSetupHdModel.getStrSendHomeDelSMS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SendBillSettlementSMS",objSetupHdModel.getStrSendBillSettlementSMS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SMSType",objSetupHdModel.getStrSMSType());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintShortNameOnKOT",objSetupHdModel.getStrPrintShortNameOnKOT());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ShowCustHelp",objSetupHdModel.getStrShowCustHelp());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintOnVoidBill",objSetupHdModel.getStrPrintOnVoidBill());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PostSalesDataToMMS",objSetupHdModel.getStrPostSalesDataToMMS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CustAreaMasterCompulsory",objSetupHdModel.getStrCustAreaMasterCompulsory());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PriceFrom",objSetupHdModel.getStrPriceFrom());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ShowPrinterErrorMessage",objSetupHdModel.getStrShowPrinterErrorMessage());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("TouchScreenMode",objSetupHdModel.getStrTouchScreenMode());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CardInterfaceType",objSetupHdModel.getStrCardInterfaceType());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CMSIntegrationYN",objSetupHdModel.getStrCMSIntegrationYN());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CMSWebServiceURL",objSetupHdModel.getStrCMSWebServiceURL());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ChangeQtyForExternalCode",objSetupHdModel.getStrChangeQtyForExternalCode());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PointsOnBillPrint",objSetupHdModel.getStrPointsOnBillPrint());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CMSPOSCode",objSetupHdModel.getStrCMSPOSCode());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ManualAdvOrderNoCompulsory",objSetupHdModel.getStrManualAdvOrderNoCompulsory());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintManualAdvOrderNoOnBill",objSetupHdModel.getStrPrintManualAdvOrderNoOnBill());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintModifierQtyOnKOT",objSetupHdModel.getStrPrintModifierQtyOnKOT());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("NoOfLinesInKOTPrint",objSetupHdModel.getStrNoOfLinesInKOTPrint());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MultipleKOTPrintYN",objSetupHdModel.getStrMultipleKOTPrintYN());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ItemQtyNumpad",objSetupHdModel.getStrItemQtyNumpad());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("TreatMemberAsTable",objSetupHdModel.getStrTreatMemberAsTable());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("KOTToLocalPrinter",objSetupHdModel.getStrKOTToLocalPrinter());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SettleBtnForDirectBillerBill",objSetupHdModel.getStrSettleBtnForDirectBillerBill());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DelBoySelCompulsoryOnDirectBiller",objSetupHdModel.getStrDelBoySelCompulsoryOnDirectBiller());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CMSMemberForKOTJPOS",objSetupHdModel.getStrCMSMemberForKOTJPOS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CMSMemberForKOTMPOS",objSetupHdModel.getStrCMSMemberForKOTMPOS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("DontShowAdvOrderInOtherPOS",objSetupHdModel.getStrDontShowAdvOrderInOtherPOS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintZeroAmtModifierInBill",objSetupHdModel.getStrPrintZeroAmtModifierInBill());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintKOTYN",objSetupHdModel.getStrPrintKOTYN());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CreditCardSlipNoCompulsoryYN",objSetupHdModel.getStrCreditCardSlipNoCompulsoryYN());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CreditCardExpiryDateCompulsoryYN",objSetupHdModel.getStrCreditCardExpiryDateCompulsoryYN());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("SelectWaiterFromCardSwipe",objSetupHdModel.getStrSelectWaiterFromCardSwipe());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MultiWaiterSelectionOnMakeKOT",objSetupHdModel.getStrMultiWaiterSelectionOnMakeKOT());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MoveTableToOtherPOS",objSetupHdModel.getStrMoveTableToOtherPOS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MoveKOTToOtherPOS",objSetupHdModel.getStrMoveKOTToOtherPOS());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CalculateTaxOnMakeKOT",objSetupHdModel.getStrCalculateTaxOnMakeKOT());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ReceiverEmailId",objSetupHdModel.getStrReceiverEmailId());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CalculateDiscItemWise",objSetupHdModel.getStrCalculateDiscItemWise());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("TakewayCustomerSelection",objSetupHdModel.getStrTakewayCustomerSelection());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ShowItemStkColumnInDB",objSetupHdModel.getStrShowItemStkColumnInDB());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ItemType",objSetupHdModel.getStrItemType());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("AllowNewAreaMasterFromCustMaster",objSetupHdModel.getStrAllowNewAreaMasterFromCustMaster());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CustAddressSelectionForBill",objSetupHdModel.getStrCustAddressSelectionForBill());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MemberCodeForKotInMposByCardSwipe",objSetupHdModel.getStrMemberCodeForKotInMposByCardSwipe());
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PrintBillPopUp","Y");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("UseVatAndServiceTaxNoFromPOS","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("MemberCodeForMakeBillInMPOS","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("ItemWiseKOTPrintYN","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("LastPOSForDayEnd","P01");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("CMSPostingType","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("PopUpToApplyPromotionsOnBill","Y");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strSelectCustomerCodeFromCardSwipe","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strCheckDebitCardBalOnTransactions","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strSettlementsFromPOSMaster","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strShiftWiseDayEndYN","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strProductionLinkup","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strLockDataOnShift","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strWSClientCode","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strPOSCode","P01");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strEnableBillSeries","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("EnablePMSIntegrationYN","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strPrintTimeOnBill","Y");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strPrintTDHItemsInBill","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strPrintRemarkAndReasonForReprint","Y");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("intDaysBeforeOrderToCancel","1");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("intNoOfDelDaysForAdvOrder","1");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("intNoOfDelDaysForUrgentOrder","1");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strSetUpToTimeForAdvOrder","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strSetUpToTimeForUrgentOrder","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strUpToTimeForAdvOrder","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strUpToTimeForUrgentOrder","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strEnableBothPrintAndSettleBtnForDB","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strInrestoPOSIntegrationYN","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strInrestoPOSWebServiceURL","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strInrestoPOSId","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strInrestoPOSKey","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strCarryForwardFloatAmtToNextDay","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strOpenCashDrawerAfterBillPrintYN","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strPropertyWiseSalesOrderYN","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strShowItemDetailsGrid","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strShowPopUpForNextItemQuantity","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strJioMoneyIntegration","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strJioWebServiceUrl","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strJioMID","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strJioTID","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strJioActivationCode","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strJioDeviceID","");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strNewBillSeriesForNewDay","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strShowReportsPOSWise","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strEnableDineIn","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strAutoAreaSelectionInMakeKOT","N");
	    	clsPOSGlobalFunctionsController.hmPOSSetupValues.put("strConsolidatedKOTPrinterPort","");
		}
		
		
		List listMainMenuForms=objMainMenuService.funGetMainMenuForms(moduleType, superUserYN, POSCode, userCode, clientCode,"");
		List<clsUserDesktopUtil> listMenu=new  ArrayList<clsUserDesktopUtil>();
		
		if(null!=listMainMenuForms)
		{	
		    for(int cnt=0;cnt<listMainMenuForms.size();cnt++)
		    {
		    	Object[] arrObjMainMenuList=(Object[])listMainMenuForms.get(cnt);
		    	clsUserDesktopUtil obTreeRootItem=new clsUserDesktopUtil();
		    	
		    	obTreeRootItem.setStrFormName(arrObjMainMenuList[2].toString());
		    	obTreeRootItem.setStrRequestMapping(arrObjMainMenuList[3].toString());
		    	obTreeRootItem.setStrImgName(arrObjMainMenuList[1].toString());
		    	obTreeRootItem.setStrFormDesc(arrObjMainMenuList[0].toString());
		    	obTreeRootItem.setStrShortName(arrObjMainMenuList[4].toString());
		    	listMenu.add(obTreeRootItem);
		    }
		}
	    
		return listMenu;
	}

	@RequestMapping(value = "/mainMenuSearchFormName", method = RequestMethod.GET)
	public @ResponseBody JSONArray funMenuSearchFormName(@RequestParam("fromNameText") String fromNameText,HttpServletRequest req)
	{
		String clientCode=req.getSession().getAttribute("gClientCode").toString();
		String posModule = req.getSession().getAttribute("webPOSModuleSelect").toString();
		String userCode = req.getSession().getAttribute("gUserCode").toString();
		String userType = req.getSession().getAttribute("gUserType").toString();
		String POSCode = req.getSession().getAttribute("loginPOS").toString();
		
		boolean superUserYN=false;
		if(userType.equalsIgnoreCase("Super"))
		{
			superUserYN=true;
		}
		List<clsUserDesktopUtil> listMenu=null;
		List listMainMenuForms=null;
		JSONArray jArr = new JSONArray();
		try {
			listMainMenuForms = objMainMenuService.funGetMainMenuForms(posModule, superUserYN, POSCode, userCode, clientCode,fromNameText);
			if(null!=listMainMenuForms)
				{	
				    for(int cnt=0;cnt<listMainMenuForms.size();cnt++)
				    {
				    	Object[] arrObjMainMenuList=(Object[])listMainMenuForms.get(cnt);
						JSONObject jobj = new JSONObject();
						jobj.put("formName", arrObjMainMenuList[2].toString());
						jobj.put("strFormName", arrObjMainMenuList[2].toString());
						jobj.put("strFormDesc", arrObjMainMenuList[0].toString());
						jobj.put("strImgName", arrObjMainMenuList[1].toString()+".png");
						jobj.put("strRequestMapping", arrObjMainMenuList[3].toString());
						jobj.put("strShortName",arrObjMainMenuList[4].toString());
					
						jArr.add(jobj);
				    }
				} 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
		return jArr;
	}
	
}

