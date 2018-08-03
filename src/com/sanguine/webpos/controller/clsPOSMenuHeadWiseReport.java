package com.sanguine.webpos.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsBillDtl;
import com.sanguine.webpos.bean.clsGenericBean;
import com.sanguine.webpos.bean.clsWebPOSReportBean;

@Controller
public class clsPOSMenuHeadWiseReport
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	@Autowired
	private ServletContext servletContext;

	Map<String, String> hmPOSData;
	Map<String, String> hmWaiterData;

	// /frmPOSWaiterWiseItemWiseIncentiveReport frmPOSWaiterWiseIncentiveReport
	@RequestMapping(value = "/frmPOSMenuHeadWiseReport", method = RequestMethod.GET)
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
		poslist.add("All");

		hmPOSData = new HashMap<String, String>();
		JSONArray jArryPosList = objPOSGlobalFunctionsController.funGetAllPOSForMaster(strClientCode);
		for (int i = 0; i < jArryPosList.size(); i++)
		{
			JSONObject josnObjRet = (JSONObject) jArryPosList.get(i);
			poslist.add(josnObjRet.get("strPosName"));
			hmPOSData.put(josnObjRet.get("strPosName").toString(), josnObjRet.get("strPosCode").toString());
		}
		model.put("posList", poslist);

		// for pos date
		String strPosCode = request.getSession().getAttribute("loginPOS").toString();
		String posURL = clsPOSGlobalFunctionsController.POSWSURL + "/APOSIntegration/funGetPOSDate"
				+ "?POSCode=" + strPosCode;
		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(posURL);
		String posDate = jObj.get("POSDate").toString();
		request.setAttribute("POSDate", posDate);

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSMenuHeadWiseReport_1", "command", new clsWebPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSMenuHeadWiseReport", "command", new clsWebPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptMenuHeadWiseReport", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{

		try
		{

			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptMenuHeadWiseSalesReport.jrxml");

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
			String gAreaWisePricing = "N";

			StringBuilder sbSqlLive = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			StringBuilder sbSqlFilters = new StringBuilder();

			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);

			sbSqlQFile.append("SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate),sum(a.dblAmount) ,sum(a.dblDiscountAmt) "
					+ "FROM tblqbilldtl a\n"
					+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and b.strposcode =d.strposcode ");
			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sbSqlQFile.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlQFile.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sbSqlQFile.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ");

			sbSqlLive.append("SELECT ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ " sum(a.dblAmount)-sum(a.dblDiscountAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate) ,sum(a.dblAmount),sum(a.dblDiscountAmt) "
					+ " FROM tblbilldtl a\n"
					+ " left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ " left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ " left outer join tblmenuitempricingdtl d on a.strItemCode = d.strItemCode "
					+ " and b.strposcode =d.strposcode ");
			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sbSqlLive.append("and b.strAreaCode= d.strAreaCode ");
			}
			sbSqlLive.append("left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode");
			sbSqlLive.append(" where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ");

			String sqlModLive = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) "
					+ "FROM tblbillmodifierdtl a\n"
					+ "left outer join tblbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode ";

			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sqlModLive += "and b.strAreaCode= d.strAreaCode ";
			}
			sqlModLive += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
			sqlModLive += " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 "
					+ " and a.strClientCode=b.strClientCode ";

			String sqlModQFile = "SELECT  ifnull(d.strMenuCode,'ND'),ifnull(e.strMenuName,'ND'), sum(a.dblQuantity),\n"
					+ "sum(a.dblAmount)-sum(a.dblDiscAmt),f.strPosName,'" + strUserCode + "',sum(a.dblRate),sum(a.dblAmount),sum(a.dblDiscAmt) "
					+ "FROM tblqbillmodifierdtl a\n"
					+ "left outer join tblqbillhd b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
					+ "left outer join tblposmaster f on b.strposcode=f.strposcode "
					+ "left outer join tblmenuitempricingdtl d on LEFT(a.strItemCode,7)= d.strItemCode "
					+ " and b.strposcode =d.strposcode ";

			if (gAreaWisePricing.equalsIgnoreCase("Y"))
			{
				sqlModQFile += "and b.strAreaCode= d.strAreaCode ";
			}
			sqlModQFile += "left outer join tblmenuhd e on d.strMenuCode= e.strMenuCode";
			sqlModQFile += " where date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' and a.dblAmount>0 "
					+ " and a.strClientCode=b.strClientCode ";

			if (!posCode.equalsIgnoreCase("All"))
			{
				sbSqlFilters.append(" AND b.strPOSCode = '" + posCode + "' ");
			}
			if (!shiftNo.equalsIgnoreCase("All"))
			{
				sbSqlFilters.append(" AND b.intShiftCode = '" + shiftNo + "' ");
			}
			sbSqlFilters.append(" Group by b.strPoscode, d.strMenuCode,e.strMenuName");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive = sqlModLive + " " + sbSqlFilters.toString();
			sqlModQFile = sqlModQFile + " " + sbSqlFilters.toString();

			Map<String, clsGenericBean> mapMenuDtl = new HashMap<String, clsGenericBean>();

			// live data			
			List list = objGlobalFunctions.funGetList(sbSqlLive.toString());
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}

		
			// live modifiers			
			list = objGlobalFunctions.funGetList(sqlModLive.toString());
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// Q data			
			list = objGlobalFunctions.funGetList(sbSqlQFile.toString());
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// live modifiers			
			list = objGlobalFunctions.funGetList(sqlModQFile.toString());
			if (list.size() > 0)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Object[] objArr = (Object[]) list.get(i);

					String menuHeadCode = objArr[0].toString();
					String menuHeadName = objArr[1].toString();
					double qty = Double.parseDouble(objArr[2].toString());
					double subTotal = Double.parseDouble(objArr[3].toString());
					String posName = objArr[4].toString();
					double amount = Double.parseDouble(objArr[7].toString());
					double discAmt = Double.parseDouble(objArr[8].toString());

					if (mapMenuDtl.containsKey(menuHeadCode))
					{
						clsGenericBean obj = mapMenuDtl.get(menuHeadCode);

						obj.setDblQty(obj.getDblQty() + qty);
						obj.setDblAmt(obj.getDblAmt() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscAmt(obj.getDblDiscAmt() + discAmt);
						obj.setStrPOSName(posName);

						mapMenuDtl.put(menuHeadCode, obj);
					}
					else
					{
						clsGenericBean obj = new clsGenericBean();

						obj.setStrCode(menuHeadCode);
						obj.setStrName(menuHeadName);
						obj.setDblQty(qty);
						obj.setDblSubTotal(subTotal);
						obj.setStrPOSName(posName);
						obj.setDblAmt(amount);
						obj.setDblDiscAmt(discAmt);

						mapMenuDtl.put(menuHeadCode, obj);
					}
				}
			}
			// convert to list
			Collection<clsGenericBean> listOfMenuHead = mapMenuDtl.values();

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfMenuHead);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);
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
					resp.setHeader("Content-Disposition", "inline;filename=MenuHeadWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=MenuHeadWiseReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
		catch (Exception e)
		{

		}
	}
}
