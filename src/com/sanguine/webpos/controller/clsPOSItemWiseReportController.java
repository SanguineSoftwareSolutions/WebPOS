package com.sanguine.webpos.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.sanguine.webpos.bean.clsWebPOSReportBean;

@Controller
public class clsPOSItemWiseReportController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;
	@Autowired
	private ServletContext servletContext;

	Map<String, String> hmPOSData;

	@RequestMapping(value = "/frmPOSItemWiseReport", method = RequestMethod.GET)
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

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSItemWiseReport_1", "command", new clsWebPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSItemWiseReport", "command", new clsWebPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSItemWiseSales", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{
			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptitemWiseSalesReport.jrxml");

			String type = objBean.getStrDocType();
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

			String printComplimentaryYN = "No";

			String sqlFilters = "";

			String sqlLive = "select a.strItemCode,a.strItemName,c.strPOSName"
					+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
					+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
					+ "from tblbilldtl a,tblbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo "
					+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
					+ "and b.strPOSCode=c.strPosCode "
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ";

			String sqlLiveCompli = "select a.strItemCode,a.strItemName,c.strPOSName"
					+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
					+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
					+ "from tblbillcomplementrydtl a,tblbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo "
					+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
					+ "and b.strPOSCode=c.strPosCode "
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ";

			String sqlQFile = "select a.strItemCode,a.strItemName,c.strPOSName"
					+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
					+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
					+ "from tblqbilldtl a,tblqbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo "
					+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
					+ "and b.strPOSCode=c.strPosCode "
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ";
			String sqlQCompli = "select a.strItemCode,a.strItemName,c.strPOSName"
					+ ",sum(a.dblQuantity),sum(a.dblTaxAmount)\n"
					+ ",sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscountAmt),sum(a.dblDiscountAmt),DATE_FORMAT(date(a.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
					+ "from tblqbillcomplementrydtl a,tblqbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo "
					+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
					+ "and b.strPOSCode=c.strPosCode "
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " and a.strClientCode=b.strClientCode ";

			String sqlModLive = "select a.strItemCode,a.strModifierName,c.strPOSName"
					+ ",sum(a.dblQuantity),'0',sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
					+ "from tblbillmodifierdtl a,tblbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo "
					+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
					+ "and b.strPOSCode=c.strPosCode "
					+ "and a.dblamount>0 \n"
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
					+ " and a.strClientCode=b.strClientCode  ";

			String sqlModQFile = "select a.strItemCode,a.strModifierName,c.strPOSName"
					+ ",sum(a.dblQuantity),'0',sum(a.dblAmount),sum(a.dblAmount)-sum(a.dblDiscAmt),sum(a.dblDiscAmt),DATE_FORMAT(date(b.dteBillDate),'%d-%m-%Y'),'" + strUserCode + "'\n"
					+ "from tblqbillmodifierdtl a,tblqbillhd b,tblposmaster c\n"
					+ "where a.strBillNo=b.strBillNo "
					+ "AND DATE(a.dteBillDate)=DATE(b.dteBillDate) "
					+ "and b.strPOSCode=c.strPosCode "
					+ "and a.dblamount>0 \n"
					+ "and date( b.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "'"
					+ "and a.strClientCode=b.strClientCode  ";

			if (!posCode.equalsIgnoreCase("All"))
			{
				sqlFilters += " AND b.strPOSCode = '" + posCode + "' ";
			}
			if (!shiftNo.equalsIgnoreCase("All"))
			{
				sqlFilters += " AND b.intShiftCode = '" + shiftNo + "' ";
			}

			// sqlFilters += " GROUP BY a.strItemCode";
			sqlLive = sqlLive + " " + sqlFilters + "  GROUP BY a.strItemCode,a.strItemName ";
			sqlLiveCompli = sqlLiveCompli + " " + sqlFilters + "  GROUP BY a.strItemCode,a.strItemName ";
			sqlQFile = sqlQFile + " " + sqlFilters + "  GROUP BY a.strItemCode,a.strItemName ";
			sqlQCompli = sqlQCompli + " " + sqlFilters + "  GROUP BY a.strItemCode,a.strItemName ";

			sqlModLive = sqlModLive + " " + sqlFilters + "  GROUP BY a.strItemCode,a.strModifierName ";
			sqlModQFile = sqlModQFile + " " + sqlFilters + "  GROUP BY a.strItemCode,a.strModifierName ";

			Map<String, clsBillItemDtlBean> mapItemdtl = new HashMap<>();

			List listSqlLive = objGlobalFunctions.funGetList(sqlLive.toString());
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);

					}
					else
					{
						clsBillItemDtlBean obj = new clsBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);

						mapItemdtl.put(itemCode, obj);
					}
				}
			}

			listSqlLive = objGlobalFunctions.funGetList(sqlQFile.toString());
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);

					}
					else
					{
						clsBillItemDtlBean obj = new clsBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);

						mapItemdtl.put(itemCode, obj);
					}
				}
			}

			listSqlLive = objGlobalFunctions.funGetList(sqlModLive.toString());
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);

					}
					else
					{
						clsBillItemDtlBean obj = new clsBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);

						mapItemdtl.put(itemCode, obj);
					}
				}
			}
			
			listSqlLive = objGlobalFunctions.funGetList(sqlModQFile.toString());
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] objArr = (Object[]) listSqlLive.get(i);

					String itemCode = objArr[0].toString();
					String itemName = objArr[1].toString();
					String posName = objArr[2].toString();
					double qty = Double.parseDouble(objArr[3].toString());
					double taxAmt = Double.parseDouble(objArr[4].toString());
					double amount = Double.parseDouble(objArr[5].toString());
					double subTotal = Double.parseDouble(objArr[6].toString());
					double discAmt = Double.parseDouble(objArr[7].toString());
					String billDate = objArr[8].toString();

					if (mapItemdtl.containsKey(itemCode))
					{
						clsBillItemDtlBean obj = mapItemdtl.get(itemCode);

						obj.setDblQuantity(obj.getDblQuantity() + qty);
						obj.setDblTaxAmt(obj.getDblTaxAmt() + taxAmt);
						obj.setDblAmount(obj.getDblAmount() + amount);
						obj.setDblSubTotal(obj.getDblSubTotal() + subTotal);
						obj.setDblDiscountAmt(obj.getDblDiscountAmt() + discAmt);

					}
					else
					{
						clsBillItemDtlBean obj = new clsBillItemDtlBean();

						obj.setStrItemCode(itemCode);
						obj.setStrItemName(itemName);
						obj.setStrPosName(posName);
						obj.setDblQuantity(qty);
						obj.setDblTaxAmt(taxAmt);
						obj.setDblAmount(amount);
						obj.setDblSubTotal(subTotal);
						obj.setDblDiscountAmt(discAmt);
						obj.setDteBillDate(billDate);

						mapItemdtl.put(itemCode, obj);
					}
				}
			}

			/**
			 * substract compli qty
			 */
			if (printComplimentaryYN.equalsIgnoreCase("No"))
			{
				hm.put("Note", "Note:Report does not include complimentary quantities.");
				
				listSqlLive = objGlobalFunctions.funGetList(sqlLiveCompli.toString());
				if (listSqlLive.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++)
					{
						Object[] objArr = (Object[]) listSqlLive.get(i);
						
						String itemCode = objArr[0].toString();
						double qty=Double.parseDouble(objArr[3].toString());
						if (mapItemdtl.containsKey(itemCode))
						{
							clsBillItemDtlBean obj = mapItemdtl.get(itemCode);

							obj.setDblQuantity(obj.getDblQuantity() - qty);
						}
					}
				}
				
				listSqlLive = objGlobalFunctions.funGetList(sqlQCompli.toString());
				if (listSqlLive.size() > 0)
				{
					for (int i = 0; i < listSqlLive.size(); i++)
					{
						Object[] objArr = (Object[]) listSqlLive.get(i);
						
						String itemCode = objArr[0].toString();
						double qty=Double.parseDouble(objArr[3].toString());
						if (mapItemdtl.containsKey(itemCode))
						{
							clsBillItemDtlBean obj = mapItemdtl.get(itemCode);

							obj.setDblQuantity(obj.getDblQuantity() - qty);
						}
					}
				}
						
			}
			else
			{
				hm.put("Note", "Note:Report contains complimentary quantities.");
			}

			Comparator<clsBillItemDtlBean> itemCodeComparator = new Comparator<clsBillItemDtlBean>()
			{

				@Override
				public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
				{
					return o1.getStrItemCode().substring(0, 7).compareToIgnoreCase(o2.getStrItemCode().substring(0, 7));
				}
			};

			List<clsBillItemDtlBean> listOfItemData = new ArrayList<clsBillItemDtlBean>();
			for (clsBillItemDtlBean objItemDtlBean : mapItemdtl.values())
			{
				listOfItemData.add(objItemDtlBean);
			}

			Collections.sort(listOfItemData, itemCodeComparator);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();

			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfItemData);
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
					resp.setHeader("Content-Disposition", "inline;filename=ItemWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=ItemWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
