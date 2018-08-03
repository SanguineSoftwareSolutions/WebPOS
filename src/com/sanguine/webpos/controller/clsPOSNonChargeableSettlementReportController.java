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
import com.sanguine.webpos.bean.clsBillDtl;
import com.sanguine.webpos.bean.clsPOSNonChargableKOTReportBean;
import com.sanguine.webpos.bean.clsWebPOSReportBean;

@Controller
public class clsPOSNonChargeableSettlementReportController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	@Autowired
	private ServletContext servletContext;

	Map<String, String> hmPOSData;
	Map<String, String> hmReasonData;

	@RequestMapping(value = "/frmPOSNonChargableSettlementReport", method = RequestMethod.GET)
	public ModelAndView funOpenForm(Map<String, Object> model, HttpServletRequest request)
	{
		String strClientCode = request.getSession().getAttribute("clientCode").toString();
		String urlHits = "1";
		try
		{
			urlHits = request.getParameter("saddr").toString();
		}
		catch (NullPointerException e)
		{
			urlHits = "1";
		}
		model.put("urlHits", urlHits);
		List poslist = new ArrayList();
		poslist.add("ALL");

		hmPOSData = new HashMap<String, String>();
		JSONArray jArryPosList = objPOSGlobalFunctionsController.funGetAllPOSForMaster(strClientCode);
		for (int i = 0; i < jArryPosList.size(); i++)
		{
			JSONObject josnObjRet = (JSONObject) jArryPosList.get(i);
			poslist.add(josnObjRet.get("strPosName"));
			hmPOSData.put(josnObjRet.get("strPosName").toString(), josnObjRet.get("strPosCode").toString());
		}
		model.put("posList", poslist);

		List Reasonlist = new ArrayList();
		Reasonlist.add("ALL");
		hmReasonData = new HashMap<String, String>();
		JSONArray jArryReasonList = objPOSGlobalFunctionsController.funGetAllReasonMaster(strClientCode);
		for (int i = 0; i < jArryReasonList.size(); i++)
		{
			JSONObject josnObjRet = (JSONObject) jArryReasonList.get(i);
			Reasonlist.add(josnObjRet.get("strReasonName"));
			hmReasonData.put(josnObjRet.get("strReasonCode").toString(), josnObjRet.get("strReasonName").toString());
		}
		model.put("ReasonMasterList", Reasonlist);

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSNonChargeableSettlementReport_1", "command", new clsWebPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSNonChargeableSettlementReport", "command", new clsWebPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptNonChargeableSettlementReport", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{

		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptNonChargableKOTReport.jrxml");

			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = hmPOSData.get(strPOSName).toString();
			}
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String shiftNo = "1";

			String type = objBean.getStrDocType();
			String strReasonMaster = objBean.getStrReasonMaster();
			String reasonCode = "ALL";
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = hmPOSData.get(strPOSName);
			}
			if (!strReasonMaster.equalsIgnoreCase("ALL"))
			{
				reasonCode = hmReasonData.get(strReasonMaster);
			}

			StringBuilder sqlBuilder = new StringBuilder();

			// live
			sqlBuilder.setLength(0);
			sqlBuilder.append("select a.strKOTNo, DATE_FORMAT(a.dteNCKOTDate,'%d-%m-%Y %H:%i'), a.strTableNo, b.strReasonName,d.strPosName,\n"
					+ "a.strRemark,  a.strItemCode, c.strItemName, a.dblQuantity, a.dblRate, a.dblQuantity * a.dblRate as Amount\n"
					+ ",e.strTableName\n"
					+ "from tblnonchargablekot a, tblreasonmaster b, tblitemmaster c,tblposmaster d,tbltablemaster e\n"
					+ "where  a.strReasonCode = b.strReasonCode \n"
					+ "and a.strTableNo=e.strTableNo \n"
					+ "and a.strItemCode = c.strItemCode  and a.strPosCode=d.strPOSCode\n"
					+ "and date(a.dteNCKOTDate) between '" + fromDate + "' and  '" + toDate + "'\n ");
			if (!posCode.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
			}
			if (!reasonCode.equalsIgnoreCase("All"))
			{
				sqlBuilder.append("and a.strReasonCode='" + reasonCode + "'  ");
			}

			List<clsBillDtl> listOfNCKOTData = new ArrayList<clsBillDtl>();
			List list = objGlobalFunctions.funGetList(sqlBuilder.toString());
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					clsBillDtl obj = new clsBillDtl();
					
					obj.setStrKOTNo(objArr[0].toString());
					obj.setDteNCKOTDate(objArr[1].toString());
					obj.setStrTableNo(objArr[2].toString());
					obj.setStrReasonName(objArr[3].toString());
					obj.setStrPosName(objArr[4].toString());
					obj.setStrRemarks(objArr[5].toString());
					obj.setStrItemCode(objArr[6].toString());
					obj.setStrItemName(objArr[7].toString());
					obj.setDblQuantity(Double.parseDouble(objArr[8].toString()));
					obj.setDblRate(Double.parseDouble(objArr[9].toString()));
					obj.setDblAmount(Double.parseDouble(objArr[10].toString()));
					obj.setStrTableName(objArr[11].toString());
					
					
					listOfNCKOTData.add(obj);
				}
			}

			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfNCKOTData);
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
					resp.setHeader("Content-Disposition", "inline;filename=NonChargeableKOTReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=NonChargeableKOTReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		System.out.println("Hi");

	}

}
