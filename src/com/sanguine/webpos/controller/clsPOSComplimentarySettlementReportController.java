	package com.sanguine.webpos.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsBillDtl;
import com.sanguine.webpos.bean.clsWebPOSReportBean;
import com.sanguine.webpos.model.clsReasonMasterModel;
import com.sanguine.webpos.sevice.clsMasterService;
import com.sanguine.webpos.sevice.clsReportService;


@Controller
public class clsPOSComplimentarySettlementReportController {

	
	        @Autowired
			private clsGlobalFunctions objGlobalFunctions;
			
			 @Autowired
			 private ServletContext servletContext;
			 
			 @Autowired
			 clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
				
			 @Autowired
			 clsMasterService objMasterService;
			 
			 @Autowired
			 clsReportService objReportService;
			 
			 Map mapReason=new TreeMap();
			 Map map=new TreeMap();
			
			@RequestMapping(value = "/frmPOSComplimentarySettlement.html", method = RequestMethod.GET)
			public ModelAndView funOpenForm(Map<String, Object> model,HttpServletRequest request)
			{
				String strClientCode=request.getSession().getAttribute("gClientCode").toString();	
				String urlHits="1";
				try{
					urlHits=request.getParameter("saddr").toString();
				}catch(NullPointerException e){
					urlHits="1";
				}
				model.put("urlHits",urlHits);
				List poslist = new ArrayList();
				poslist.add("ALL");
				
				List listOfPos = objMasterService.funFillPOSCombo(strClientCode);
				if(listOfPos!=null)
				{
					for(int i =0 ;i<listOfPos.size();i++)
					{
						Object[] obj = (Object[]) listOfPos.get(i);
						poslist.add( obj[1].toString());
						map.put( obj[1].toString(), obj[0].toString());
					}
				}
				model.put("posList",poslist);
				
				//area list
				
				List reasonList= new ArrayList();
				reasonList.add("ALL");
			   	List listOfReasons = objMasterService.funGetAllReasonMaster(strClientCode);
				for(int i =0 ;i<listOfReasons.size();i++)
							{
								clsReasonMasterModel objModel = (clsReasonMasterModel) listOfReasons.get(i);
								reasonList.add(objModel.getStrReasonName());
								mapReason.put(objModel.getStrReasonName(),objModel.getStrReasonCode());
							}
							
							model.put("reasonList",reasonList);
				
				if("2".equalsIgnoreCase(urlHits)){
					return new ModelAndView("frmPOSComplimentarySettlementReport_1","command", new clsWebPOSReportBean());
				}else if("1".equalsIgnoreCase(urlHits)){
					return new ModelAndView("frmPOSComplimentarySettlementReport","command", new clsWebPOSReportBean());
				}else {
					return null;
				}
				 
			}
			
			
			
			@SuppressWarnings("rawtypes")
			@RequestMapping(value = "/rptPOSComplimentarySettlement", method = RequestMethod.POST)	
			private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp,HttpServletRequest req)
			{
				
				try
				{

					List listLive = null;
					List listQFile = null;
					List listModLive = null;
					List listModQFile = null;
				
					
					Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
					String type=objBean.getStrDocType();
					String strReasonName=objBean.getStrReasonCode();
					String strViewType =objBean.getStrViewType();
					String strPOSName =objBean.getStrPOSName();
					String reportViewType = objBean.getStrViewType();
					String posCode= "ALL";
					if (!strPOSName.equalsIgnoreCase("ALL"))
					{
						posCode = (String) map.get(strPOSName);
					}
					hm.put("posCode", posCode);
					String strReasonCode="ALL";
					if(!strReasonName.equalsIgnoreCase("ALL"))
					{
						strReasonCode = (String) mapReason.get(strReasonName);
					}
					hm.put("reasonCode", strReasonCode);
					String fromDate = hm.get("fromDate").toString();
					String toDate = hm.get("toDate").toString();
					String strUserCode = hm.get("userName").toString();
					String strPOSCode = posCode;
					String strShiftNo = "1";
					
					StringBuilder sbSqlLive = new StringBuilder();
		            StringBuilder sbSqlQBill = new StringBuilder();
		            StringBuilder sqlLiveModifierBuilder = new StringBuilder();
		            StringBuilder sqlQModifierBuilder = new StringBuilder();

		            List<clsBillDtl> listOfCompliItemDtl = new ArrayList<>();

		            if (reportViewType.equals("Detail"))
		            {
		                
		                String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptComplimentarySettlementReport.jrxml");
		                listOfCompliItemDtl = objReportService.funProcessComplimentaryDetailReport(posCode,fromDate,toDate,strReasonCode,strShiftNo);		                
		                JasperDesign jd = JRXmlLoader.load(reportName);
		    			JasperReport jr = JasperCompileManager.compileReport(jd);

		    			// jp = JasperFillManager.fillReport(jr, hm, new
		    			// JREmptyDataSource());

		    			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
		    			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCompliItemDtl);
		    			JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
		    			jprintlist.add(print);

		    			if (jprintlist.size() > 0)
		    			{
		    				ServletOutputStream servletOutputStream = resp.getOutputStream();
		    				if (objBean.getStrDocType().equals("PDF"))
		    				{
		    					JRExporter exporter = new JRPdfExporter();
		    					resp.setContentType("application/pdf");
		    					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
		    					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
		    					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		    					resp.setHeader("Content-Disposition", "inline;filename=ComplimentarySettlementDetailReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
		    					exporter.exportReport();
		    					servletOutputStream.flush();
		    					servletOutputStream.close();
		    				}
		    				else
		    				{
		    					JRExporter exporter = new JRXlsExporter();
		    					resp.setContentType("application/xlsx");
		    					exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
		    					exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
		    					exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		    					resp.setHeader("Content-Disposition", "inline;filename=ComplimentarySettlementDetailReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
		    					exporter.exportReport();
		    					servletOutputStream.flush();
		    					servletOutputStream.close();
		    				}
		    			}
		    			else
		    			{
		    				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    				resp.getWriter().append("No Record Found");

		    			}

		            }
		            else if (reportViewType.equals("Summary"))
		            {
		            
		                String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptComplimentorySummaryReport.jrxml");
		                listOfCompliItemDtl = objReportService.funProcessComplimentarySummaryReport(posCode,fromDate,toDate,strReasonCode,strShiftNo);
		                
		                JasperDesign jd = JRXmlLoader.load(reportName);
		    			JasperReport jr = JasperCompileManager.compileReport(jd);

		    			// jp = JasperFillManager.fillReport(jr, hm, new
		    			// JREmptyDataSource());

		    			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
		    			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCompliItemDtl);
		    			JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
		    			jprintlist.add(print);

		    			if (jprintlist.size() > 0)
		    			{
		    				ServletOutputStream servletOutputStream = resp.getOutputStream();
		    				if (objBean.getStrDocType().equals("PDF"))
		    				{
		    					JRExporter exporter = new JRPdfExporter();
		    					resp.setContentType("application/pdf");
		    					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
		    					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
		    					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		    					resp.setHeader("Content-Disposition", "inline;filename=ComplimentarySettlementSummaryReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
		    					exporter.exportReport();
		    					servletOutputStream.flush();
		    					servletOutputStream.close();
		    				}
		    				else
		    				{
		    					JRExporter exporter = new JRXlsExporter();
		    					resp.setContentType("application/xlsx");
		    					exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
		    					exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
		    					exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		    					resp.setHeader("Content-Disposition", "inline;filename=ComplimentarySettlementSummaryReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
		    					exporter.exportReport();
		    					servletOutputStream.flush();
		    					servletOutputStream.close();
		    				}
		    			}
		    			else
		    			{
		    				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    				resp.getWriter().append("No Record Found");

		    			}
		            }
		            else
		            {
		               
		                String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptComplimentaryGroupWaiseReport.jrxml");
		                listOfCompliItemDtl = objReportService.funProcessComplimentaryGroupWiseReport(posCode,fromDate,toDate,strReasonCode,strShiftNo);		                
		                JasperDesign jd = JRXmlLoader.load(reportName);
		    			JasperReport jr = JasperCompileManager.compileReport(jd);

		    			// jp = JasperFillManager.fillReport(jr, hm, new
		    			// JREmptyDataSource());

		    			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
		    			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfCompliItemDtl);
		    			JasperPrint print = JasperFillManager.fillReport(jr, hm, beanCollectionDataSource);
		    			jprintlist.add(print);

		    			if (jprintlist.size() > 0)
		    			{
		    				ServletOutputStream servletOutputStream = resp.getOutputStream();
		    				if (objBean.getStrDocType().equals("PDF"))
		    				{
		    					JRExporter exporter = new JRPdfExporter();
		    					resp.setContentType("application/pdf");
		    					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jprintlist);
		    					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, servletOutputStream);
		    					exporter.setParameter(JRPdfExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		    					resp.setHeader("Content-Disposition", "inline;filename=ComplimentarySettlementGroupWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
		    					exporter.exportReport();
		    					servletOutputStream.flush();
		    					servletOutputStream.close();
		    				}
		    				else
		    				{
		    					JRExporter exporter = new JRXlsExporter();
		    					resp.setContentType("application/xlsx");
		    					exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT_LIST, jprintlist);
		    					exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, servletOutputStream);
		    					exporter.setParameter(JRXlsExporterParameter.IGNORE_PAGE_MARGINS, Boolean.TRUE);
		    					resp.setHeader("Content-Disposition", "inline;filename=ComplimentarySettlementGroupWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
		    					exporter.exportReport();
		    					servletOutputStream.flush();
		    					servletOutputStream.close();
		    				}
		    			}
		    			else
		    			{
		    				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    				resp.getWriter().append("No Record Found");

		    			}
		            }

				}
				
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			            
				
				System.out.println("Hi");
			}
}
