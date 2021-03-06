package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSBillReportBean;
import com.sanguine.webpos.bean.clsWebPOSReportBean;

@Controller
public class clsPosDailyCollectionReportController {

	
	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	
	 @Autowired
	 private ServletContext servletContext;
	 
	
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctions;
	
	@RequestMapping(value = "/frmPOSDailyCollectionReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
	{
		String strClientCode=request.getSession().getAttribute("clientCode").toString();	
		String urlHits="1";
		try{
			urlHits=request.getParameter("saddr").toString();
		}catch(NullPointerException e){
			urlHits="1";
		}
		model.put("urlHits",urlHits);
		List poslist = new ArrayList();
		poslist.add("ALL");
		
		
		JSONArray jArryPosList = objPOSGlobalFunctions.funGetAllPOSForMaster(strClientCode);
		
		for(int i =0 ;i<jArryPosList.size();i++)
		{
			JSONObject josnObjRet = (JSONObject) jArryPosList.get(i);
			poslist.add(josnObjRet.get("strPosName"));
		}
		model.put("posList",poslist);
		  String strPosCode=request.getSession().getAttribute("loginPOS").toString();
			String posURL =clsPOSGlobalFunctionsController.POSWSURL+"/APOSIntegration/funGetPOSDate"
					+ "?POSCode="+strPosCode;
		JSONObject jObj=objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);
		String posDate=jObj.get("POSDate").toString();
		request.setAttribute("POSDate", posDate);
		if("2".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDailyCollectionReport_1","command", new clsWebPOSReportBean());
		}else if("1".equalsIgnoreCase(urlHits)){
			return new ModelAndView("frmPOSDailyCollectionReport","command", new clsWebPOSReportBean());
		}else {
			return null;
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptDailyCollectionReport", method = RequestMethod.POST)	
	private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
	{
		//objGlobal=new clsGlobalFunctions();
		String clientCode=req.getSession().getAttribute("clientCode").toString();
		String userCode=req.getSession().getAttribute("usercode").toString();
		String companyName=req.getSession().getAttribute("companyName").toString();
	
		try
		{
		String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptDailyCollectionReport.jrxml");
		String imagePath = servletContext.getRealPath("/resources/images/company_Logo.png");
		
		 List<JasperPrint> jprintlist =new ArrayList<JasperPrint>();
		
		String strFromdate=objBean.getFromDate().split("-")[2]+"-"+objBean.getFromDate().split("-")[1]+"-"+objBean.getFromDate().split("-")[0];
		
		String strToDate=objBean.getToDate().split("-")[2]+"-"+objBean.getToDate().split("-")[1]+"-"+objBean.getToDate().split("-")[0]; 
		
		String type=objBean.getStrDocType();
		
	
		String strPOSName =objBean.getStrPOSName();
		String posCode= "ALL";
	
		if(!strPOSName.equalsIgnoreCase("ALL"))
		{
			posCode= funGetPOSCode(strPOSName);
		}
		
		
		
		
		JSONObject jObjFillter = new JSONObject();
		jObjFillter.put("strFromdate", strFromdate);
		jObjFillter.put("strToDate", strToDate);
		jObjFillter.put("posCode", posCode);
		jObjFillter.put("strShiftNo", "1");
		jObjFillter.put("userCode", userCode);
		
	
		JSONObject jObj = objGlobalFunctions.funPOSTMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSReport/funDailyCollectionReport",jObjFillter);
		List<clsWebPOSReportBean> list =new ArrayList<clsWebPOSReportBean>();
		JSONArray jarr = (JSONArray) jObj.get("jArr");
		for(int i=0;i<jarr.size();i++)
		{
			JSONObject jObjtemp =(JSONObject) jarr.get(i);
			
//			list.add(jObjtemp.get("strGroupCode"));
//			list.add(jObjtemp.get("strGroupName"));
//			list.add(jObjtemp.get("dblQuantity"));
//			list.add(jObjtemp.get("dblAmtLessDis"));
//			list.add(jObjtemp.get("strPOSName"));
//			list.add(jObjtemp.get("strUserCode"));
//			list.add(jObjtemp.get("dblRate"));
//			list.add(jObjtemp.get("dblAmount"));
//			list.add(jObjtemp.get("dblDiscountAmt"));
//			list.add(jObjtemp.get("strPOSCode"));
//			list.add(jObjtemp.get("dblAmtWithTax"));
			
			
			clsWebPOSReportBean objReportBean=new clsWebPOSReportBean();
			objReportBean.setStrBillNo(jObjtemp.get("strBillNo").toString());
			objReportBean.setDteBillDate(jObjtemp.get("dteBillDate").toString());
			objReportBean.setStrItemCode(jObjtemp.get("TableName").toString());
			objReportBean.setStrPosName(jObjtemp.get("strPosName").toString());
			objReportBean.setStrSettelmentMode(jObjtemp.get("payMode").toString());
			objReportBean.setDblSubTotal(Double.parseDouble(jObjtemp.get("dblSubTotal").toString()));
			objReportBean.setDblDiscountPer(Double.parseDouble(jObjtemp.get("dblDiscountPer").toString()));
			objReportBean.setDblDiscountAmt(Double.parseDouble(jObjtemp.get("dblDiscountAmt").toString()));
			objReportBean.setDblTaxAmt(Double.parseDouble(jObjtemp.get("dblTaxAmt").toString()));
			objReportBean.setDblSettlementAmt(Double.parseDouble(jObjtemp.get("dblSettlementAmt").toString()));
			objReportBean.setStrDiscType(jObjtemp.get("strUserCreated").toString());
			objReportBean.setStrDiscValue(jObjtemp.get("dteDateCreated").toString());
			objReportBean.setStrItemName(jObjtemp.get("CustName").toString());
			objReportBean.setDblAmount(Double.parseDouble(jObjtemp.get("dblDeliveryCharges").toString()));
			
			
		    		
			list.add(objReportBean);
		}
//		Comparator<clsGroupWaiseSalesBean> groupComparator = new Comparator<clsGroupWaiseSalesBean>()
//	            {
//
//	                @Override
//	                public int compare(clsGroupWaiseSalesBean o1, clsGroupWaiseSalesBean o2)
//	                {
//	                    return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
//	                }
//	            };
//		
//	            Collections.sort(list, new clsGroupWiseComparator(groupComparator)
//	            );
//	            
	            
	            HashMap hm = new HashMap();
	            hm.put("posCode", posCode);
	            hm.put("posName", strPOSName);
	            hm.put("imagePath", imagePath);
	            hm.put("clientName", companyName);
	            hm.put("fromDateToDisplay", strFromdate);
	            hm.put("toDateToDisplay", strToDate);
	            hm.put("shiftNo", "1");
	            hm.put("userName", userCode);
	           
	         
	    	    
	    	    JasperDesign jd = JRXmlLoader.load(reportName);
	    	    JasperReport jr = JasperCompileManager.compileReport(jd);
	    	   
	    	   //jp =  JasperFillManager.fillReport(jr, hm,  new JREmptyDataSource());
	            
	         
	            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
	            JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
	            jprintlist.add(print);
					
	            
	            if (jprintlist.size()>0)
			    {
			    	ServletOutputStream servletOutputStream = resp.getOutputStream();
			    	if(objBean.getStrDocType().equals("PDF"))
			    	{
					JRExporter exporter = new JRPdfExporter();
					resp.setContentType("application/pdf");
					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
					resp.setHeader("Content-Disposition", "inline;filename=GroupWiseSalesReport_"+strFromdate+"_To_"+strToDate+"_"+userCode+".pdf");
				    exporter.exportReport();
					servletOutputStream.flush();
					servletOutputStream.close();
			    	}else
			    	{
			    		JRExporter exporter = new JRXlsExporter();
			    		resp.setContentType("application/xlsx");
						exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
						exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
						exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
						resp.setHeader("Content-Disposition", "inline;filename=ShopOrderListTableWise_"+strFromdate+"_To_"+strToDate+"_"+userCode+".xls");
					    exporter.exportReport();
						servletOutputStream.flush();
						servletOutputStream.close();
			    	}
			    }else
			    {
			    	 resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			    	resp.getWriter().append("No Record Found");
			 		
			    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	            
		
		System.out.println("Hi");
			
	}
	 
	
	public String funGetPOSCode(String strPOSName)
	{
		String posCode="";
		String posUrl = clsPOSGlobalFunctionsController.POSWSURL+"/WebPOSPOSMaster/funGetPOSNameData";
			
		System.out.println(posUrl);
		
		try {
			JSONObject objRows = new JSONObject();
		    objRows.put("strPOSName", strPOSName);

		    JSONObject jObj = objGlobalFunctions.funPOSTMethodUrlJosnObjectData(posUrl,objRows);
		    posCode= jObj.get("strPosCode").toString();
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return posCode;
		
	}
	
	
}
