package com.sanguine.webpos.controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import com.sanguine.webpos.bean.clsGroupSubGroupWiseSales;
import com.sanguine.webpos.bean.clsWebPOSReportBean;
import com.sanguine.webpos.comparator.clsDiscountComparator;


@Controller
public class clsPOSDiscountWiseReportController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;

	@Autowired
	private ServletContext servletContext;
	Map map = new HashMap();

	@RequestMapping(value = "/frmPOSDiscountReport.html", method = RequestMethod.GET)
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

		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData(clsPOSGlobalFunctionsController.POSWSURL + "/APOSIntegration/funGetPOS");
		JSONArray jArryPosList = (JSONArray) jObj.get("posList");
		for (int i = 0; i < jArryPosList.size(); i++)
		{
			JSONObject josnObjRet = (JSONObject) jArryPosList.get(i);
			poslist.add(josnObjRet.get("strPosName"));
			map.put(josnObjRet.get("strPosName"), josnObjRet.get("strPosCode"));
		}
		model.put("posList", poslist);
		List sgNameList = new ArrayList<String>();
		sgNameList.add("ALL");

		model.put("sgNameList", sgNameList);
		// return new ModelAndView("frmPOSGroupMaster");

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSDiscountWiseReport_1", "command", new clsWebPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSDiscountWiseReport", "command", new clsWebPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSDiscountWiseSales", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{

			String reportName;
			String strViewType = objBean.getStrViewType();
			if (strViewType.equalsIgnoreCase("Summary"))
			{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillDiscountReport.jrxml");
			}
			else
			{
				reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptBillDiscountDetailReport.jrxml");
			}
			String posCode = "ALL";

			String strPOSName = objBean.getStrPOSName();
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				if (map.containsKey(strPOSName))
				{
					posCode = (String) map.get(strPOSName);
					// System.out.println(posCode.length());
				}

			}

			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String shiftNo = "1";

			List<clsBillItemDtlBean> listOfBillItemDtl = new ArrayList<>();
			if (strViewType.equalsIgnoreCase("Summary"))
			{

				StringBuilder sbSqlLiveDisc = new StringBuilder();
				StringBuilder sbSqlQFileDisc = new StringBuilder();

				sbSqlLiveDisc.setLength(0);
				sbSqlLiveDisc.append("select d.strPosName,date(a.dteBillDate),a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
						+ " ,c.strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
						+ " from \n"
						+ " tblbillhd a\n"
						+ " left outer join tblbilldiscdtl b on b.strBillNo=a.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
						+ " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
						+ " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
						+ " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
						+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
						+ " and a.strClientCode=b.strClientCode ");

				sbSqlQFileDisc.setLength(0);
				sbSqlQFileDisc.append("select d.strPosName,date(a.dteBillDate),a.strBillNo,b.dblDiscPer,b.dblDiscAmt,b.dblDiscOnAmt,b.strDiscOnType,b.strDiscOnValue "
						+ " ,c.strReasonName,b.strDiscRemarks,a.dblSubTotal,a.dblGrandTotal,b.strUserEdited "
						+ " from \n"
						+ " tblqbillhd a\n"
						+ " left outer join tblqbilldiscdtl b on b.strBillNo=a.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) \n"
						+ " left outer join tblreasonmaster c on c.strReasonCode=b.strDiscReasonCode\n"
						+ " left outer join tblposmaster d on d.strPOSCode=a.strPOSCode\n"
						+ " where  (b.dblDiscAmt> 0.00 or b.dblDiscPer >0.0) \n"
						+ " and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "' "
						+ " and a.strClientCode=b.strClientCode ");

				if (!posCode.equalsIgnoreCase("All"))
				{
					sbSqlLiveDisc.append(" and a.strPOSCode='" + posCode + "' ");
					sbSqlQFileDisc.append(" and a.strPOSCode='" + posCode + "' ");
				}

				if (!shiftNo.equalsIgnoreCase("All"))
				{
					sbSqlLiveDisc.append(" and a.intShiftCode = '" + shiftNo + "' ");
					sbSqlQFileDisc.append(" and a.intShiftCode = '" + shiftNo + "' ");
				}

				List list = objGlobalFunctions.funGetList(sbSqlLiveDisc.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						clsBillItemDtlBean objDiscBean = new clsBillItemDtlBean();

						objDiscBean.setStrPosName(objArr[0].toString()); // POSName
						objDiscBean.setDteBillDate(objArr[1].toString()); // BillDate
						objDiscBean.setStrBillNo(objArr[2].toString()); // BillNo
						objDiscBean.setDblDiscountPer(Double.parseDouble(objArr[3].toString()));// DiscPer
						objDiscBean.setDblDiscountAmt(Double.parseDouble(objArr[4].toString())); // DiscAmt
						objDiscBean.setDblBillDiscPer(Double.parseDouble(objArr[5].toString()));// DiscOnAmt
						objDiscBean.setStrDiscType(objArr[6].toString()); // DiscType
						objDiscBean.setStrDiscValue(objArr[7].toString()); // DiscValue
						objDiscBean.setStrItemCode(objArr[8].toString()); // DiscReason
						objDiscBean.setStrItemName(objArr[9].toString()); // DiscRemark
						objDiscBean.setDblSubTotal(Double.parseDouble(objArr[10].toString())); // SubTotal
						objDiscBean.setDblGrandTotal(Double.parseDouble(objArr[11].toString())); // GrandTotal
						objDiscBean.setStrSettelmentMode(objArr[12].toString()); // UserEdited

						listOfBillItemDtl.add(objDiscBean);
					}
				}

				list = objGlobalFunctions.funGetList(sbSqlQFileDisc.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						clsBillItemDtlBean objDiscBean = new clsBillItemDtlBean();

						objDiscBean.setStrPosName(objArr[0].toString()); // POSName
						objDiscBean.setDteBillDate(objArr[1].toString()); // BillDate
						objDiscBean.setStrBillNo(objArr[2].toString()); // BillNo
						objDiscBean.setDblDiscountPer(Double.parseDouble(objArr[3].toString()));// DiscPer
						objDiscBean.setDblDiscountAmt(Double.parseDouble(objArr[4].toString())); // DiscAmt
						objDiscBean.setDblBillDiscPer(Double.parseDouble(objArr[5].toString()));// DiscOnAmt
						objDiscBean.setStrDiscType(objArr[6].toString()); // DiscType
						objDiscBean.setStrDiscValue(objArr[7].toString()); // DiscValue
						objDiscBean.setStrItemCode(objArr[8].toString()); // DiscReason
						objDiscBean.setStrItemName(objArr[9].toString()); // DiscRemark
						objDiscBean.setDblSubTotal(Double.parseDouble(objArr[10].toString())); // SubTotal
						objDiscBean.setDblGrandTotal(Double.parseDouble(objArr[11].toString())); // GrandTotal
						objDiscBean.setStrSettelmentMode(objArr[12].toString()); // UserEdited

						listOfBillItemDtl.add(objDiscBean);
					}
				}
			}
			else
			// detail
			{
				StringBuilder sqlModiBuilder = new StringBuilder();
				StringBuilder sqlItemBuilder = new StringBuilder();

				// live
				sqlItemBuilder.setLength(0);
				sqlItemBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
						+ ",b.strItemCode,b.strItemName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscountAmt),b.dblDiscountPer,a.dblDiscountPer as dblBillDiscPer  "
						+ ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
						+ ",e.strUserEdited "
						+ "from tblbillhd a "
						+ "inner join  tblbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "LEFT OUTER JOIN tblbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
						+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
						+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
						+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
						+ "and b.dblDiscountPer>0 ");
				if (!posCode.equalsIgnoreCase("All"))
				{
					sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}

				sqlItemBuilder.append("group by a.strBillNo,b.strItemCode,b.strItemName "
						+ "order by a.strBillNo,b.strItemCode,b.strItemName");

				List list = objGlobalFunctions.funGetList(sqlItemBuilder.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						clsBillItemDtlBean objDiscBean = new clsBillItemDtlBean();

						objDiscBean.setStrBillNo(objArr[0].toString());
						objDiscBean.setDteBillDate(objArr[1].toString());
						objDiscBean.setStrPosName(objArr[2].toString());
						objDiscBean.setDblSubTotal(Double.parseDouble(objArr[3].toString()));
						objDiscBean.setDblGrandTotal(Double.parseDouble(objArr[4].toString()));
						objDiscBean.setStrItemCode(objArr[5].toString());
						objDiscBean.setStrItemName(objArr[6].toString());
						objDiscBean.setDblQuantity(Double.parseDouble(objArr[7].toString()));
						objDiscBean.setDblAmount(Double.parseDouble(objArr[8].toString()));
						objDiscBean.setDblDiscountAmt(Double.parseDouble(objArr[9].toString()));
						objDiscBean.setDblDiscountPer(Double.parseDouble(objArr[10].toString()));
						objDiscBean.setStrReasonName(objArr[12].toString());
						objDiscBean.setStrDiscountRemark(objArr[13].toString());
						objDiscBean.setStrSettelmentMode(objArr[14].toString()); // UserEdited

						listOfBillItemDtl.add(objDiscBean);
					}
				}
				// live modifiers
				sqlModiBuilder.setLength(0);
				sqlModiBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
						+ ",b.strItemCode,b.strModifierName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscAmt),b.dblDiscPer,a.dblDiscountPer as dblBillDiscPer "
						+ " ,ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
						+ ",e.strUserEdited "
						+ "from tblbillhd a "
						+ "inner join  tblbillmodifierdtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "LEFT OUTER JOIN tblbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
						+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
						+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
						+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
						+ "and b.dblDiscPer>0 ");
				if (!posCode.equalsIgnoreCase("All"))
				{
					sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}

				sqlModiBuilder.append("group by a.strBillNo,b.strItemCode,b.strModifierName "
						+ "order by a.strBillNo,b.strItemCode,b.strModifierName");
				list = objGlobalFunctions.funGetList(sqlModiBuilder.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						clsBillItemDtlBean objDiscBean = new clsBillItemDtlBean();

						objDiscBean.setStrBillNo(objArr[0].toString());
						objDiscBean.setDteBillDate(objArr[1].toString());
						objDiscBean.setStrPosName(objArr[2].toString());
						objDiscBean.setDblSubTotal(Double.parseDouble(objArr[3].toString()));
						objDiscBean.setDblGrandTotal(Double.parseDouble(objArr[4].toString()));
						objDiscBean.setStrItemCode(objArr[5].toString());
						objDiscBean.setStrItemName(objArr[6].toString());
						objDiscBean.setDblQuantity(Double.parseDouble(objArr[7].toString()));
						objDiscBean.setDblAmount(Double.parseDouble(objArr[8].toString()));
						objDiscBean.setDblDiscountAmt(Double.parseDouble(objArr[9].toString()));
						objDiscBean.setDblDiscountPer(Double.parseDouble(objArr[10].toString()));
						objDiscBean.setStrReasonName(objArr[12].toString());
						objDiscBean.setStrDiscountRemark(objArr[13].toString());
						objDiscBean.setStrSettelmentMode(objArr[14].toString()); // UserEdited

						listOfBillItemDtl.add(objDiscBean);
					}
				}

				// QFile
				sqlItemBuilder.setLength(0);
				sqlItemBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
						+ ",b.strItemCode,b.strItemName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscountAmt),b.dblDiscountPer,a.dblDiscountPer as dblBillDiscPer "
						+ ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
						+ ",e.strUserEdited "
						+ "from tblqbillhd a "
						+ "inner join  tblqbilldtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "LEFT OUTER JOIN tblqbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
						+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
						+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode "
						+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
						+ "and b.dblDiscountPer>0 ");
				if (!posCode.equalsIgnoreCase("All"))
				{
					sqlItemBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}

				sqlItemBuilder.append("group by a.strBillNo,b.strItemCode,b.strItemName "
						+ "order by a.strBillNo,b.strItemCode,b.strItemName");
				list = objGlobalFunctions.funGetList(sqlItemBuilder.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						clsBillItemDtlBean objDiscBean = new clsBillItemDtlBean();

						objDiscBean.setStrBillNo(objArr[0].toString());
						objDiscBean.setDteBillDate(objArr[1].toString());
						objDiscBean.setStrPosName(objArr[2].toString());
						objDiscBean.setDblSubTotal(Double.parseDouble(objArr[3].toString()));
						objDiscBean.setDblGrandTotal(Double.parseDouble(objArr[4].toString()));
						objDiscBean.setStrItemCode(objArr[5].toString());
						objDiscBean.setStrItemName(objArr[6].toString());
						objDiscBean.setDblQuantity(Double.parseDouble(objArr[7].toString()));
						objDiscBean.setDblAmount(Double.parseDouble(objArr[8].toString()));
						objDiscBean.setDblDiscountAmt(Double.parseDouble(objArr[9].toString()));
						objDiscBean.setDblDiscountPer(Double.parseDouble(objArr[10].toString()));
						objDiscBean.setStrReasonName(objArr[12].toString());
						objDiscBean.setStrDiscountRemark(objArr[13].toString());
						objDiscBean.setStrSettelmentMode(objArr[14].toString()); // UserEdited

						listOfBillItemDtl.add(objDiscBean);
					}
				}
				// QFile modifiers
				sqlModiBuilder.setLength(0);
				sqlModiBuilder.append("select a.strBillNo,DATE_FORMAT(a.dteBillDate,'%d-%m-%Y') as dteBillDate,c.strPosName,a.dblSubTotal,a.dblGrandTotal "
						+ ",b.strItemCode,b.strModifierName,b.dblQuantity,sum(b.dblAmount),sum(b.dblDiscAmt),b.dblDiscPer,a.dblDiscountPer as dblBillDiscPer "
						+ ",ifnull(d.strReasonName,'')strReasonName,ifnull(a.strDiscountRemark,'')strDiscountRemark "
						+ ",e.strUserEdited "
						+ "from tblqbillhd a "
						+ "inner join  tblqbillmodifierdtl b on a.strBillNo=b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "LEFT OUTER JOIN tblqbilldiscdtl e ON e.strBillNo=a.strBillNo AND DATE(a.dteBillDate)= DATE(e.dteBillDate) "
						+ "inner join tblposmaster c on a.strPOSCode=c.strPOSCode "
						+ "left JOIN  tblreasonmaster d on d.strReasonCode=e.strDiscReasonCode  "
						+ "where date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  "
						+ "and b.dblDiscPer>0 ");
				if (!posCode.equalsIgnoreCase("All"))
				{
					sqlModiBuilder.append(" and a.strPOSCode='" + posCode + "' ");
				}
				sqlModiBuilder.append("group by a.strBillNo,b.strItemCode,b.strModifierName "
						+ "order by a.strBillNo,b.strItemCode,b.strModifierName");
				list = objGlobalFunctions.funGetList(sqlModiBuilder.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						clsBillItemDtlBean objDiscBean = new clsBillItemDtlBean();

						objDiscBean.setStrBillNo(objArr[0].toString());
						objDiscBean.setDteBillDate(objArr[1].toString());
						objDiscBean.setStrPosName(objArr[2].toString());
						objDiscBean.setDblSubTotal(Double.parseDouble(objArr[3].toString()));
						objDiscBean.setDblGrandTotal(Double.parseDouble(objArr[4].toString()));
						objDiscBean.setStrItemCode(objArr[5].toString());
						objDiscBean.setStrItemName(objArr[6].toString());
						objDiscBean.setDblQuantity(Double.parseDouble(objArr[7].toString()));
						objDiscBean.setDblAmount(Double.parseDouble(objArr[8].toString()));
						objDiscBean.setDblDiscountAmt(Double.parseDouble(objArr[9].toString()));
						objDiscBean.setDblDiscountPer(Double.parseDouble(objArr[10].toString()));
						objDiscBean.setStrReasonName(objArr[12].toString());
						objDiscBean.setStrDiscountRemark(objArr[13].toString());
						objDiscBean.setStrSettelmentMode(objArr[14].toString()); // UserEdited

						listOfBillItemDtl.add(objDiscBean);
					}
				}

				double totalGrossSales = 0.00;

				// to calculate gross revenue
				// live
				sqlItemBuilder.setLength(0);
				sqlItemBuilder.append("select sum(a.dblSettlementAmt) "
						+ "from tblbillsettlementdtl a,tblbillhd b,tblposmaster c "
						+ "where a.strBillNo=b.strBillNo "
						+ "and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "and b.strPOSCode=c.strPosCode "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");
				if (!posCode.equalsIgnoreCase("All"))
				{
					sqlItemBuilder.append(" and b.strPOSCode='" + posCode + "' ");
				}

				list = objGlobalFunctions.funGetList(sqlItemBuilder.toString());
				if (list.size() > 0)
				{
					totalGrossSales += Double.parseDouble(list.get(0).toString());
				}

				// q
				sqlItemBuilder.setLength(0);
				sqlItemBuilder.append("select sum(a.dblSettlementAmt) "
						+ "from tblqbillsettlementdtl a,tblqbillhd b,tblposmaster c "
						+ "where a.strBillNo=b.strBillNo "
						+ "and date(a.dteBillDate)=date(b.dteBillDate) "
						+ "and b.strPOSCode=c.strPosCode "
						+ "and date(a.dteBillDate) between '" + fromDate + "' and '" + toDate + "'  ");
				if (!posCode.equalsIgnoreCase("All"))
				{
					sqlItemBuilder.append(" and b.strPOSCode='" + posCode + "' ");
				}
				list = objGlobalFunctions.funGetList(sqlItemBuilder.toString());
				if (list.size() > 0)
				{
					totalGrossSales += Double.parseDouble(list.get(0).toString());
				}

				Comparator<clsBillItemDtlBean> billDateComparator = new Comparator<clsBillItemDtlBean>()
				{

					@Override
					public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
					{
						return o1.getDteBillDate().compareTo(o2.getDteBillDate());
					}
				};

				Comparator<clsBillItemDtlBean> billNoComparator = new Comparator<clsBillItemDtlBean>()
				{

					@Override
					public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
					{
						return o1.getStrBillNo().compareTo(o2.getStrBillNo());
					}
				};

				Comparator<clsBillItemDtlBean> itemCodeComparator = new Comparator<clsBillItemDtlBean>()
				{

					@Override
					public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
					{
						return o1.getStrItemCode().substring(0, 7).compareTo(o2.getStrItemCode().substring(0, 7));
					}
				};

				Collections.sort(listOfBillItemDtl, new clsDiscountComparator(billDateComparator, billNoComparator, itemCodeComparator));

				// for group wise sales
				StringBuilder sbSqlLive = new StringBuilder();
				StringBuilder sbSqlQFile = new StringBuilder();
				StringBuilder sbSqlFilters = new StringBuilder();

				sbSqlLive.setLength(0);
				sbSqlQFile.setLength(0);
				sbSqlFilters.setLength(0);

				sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
						+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
						+ ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
						+ "sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
						+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
						+ ",tblitemmaster e,tblposmaster f "
						+ "where a.strBillNo=b.strBillNo "
						+ " and date(a.dteBillDate)=date(b.dteBillDate) "
						+ " and a.strPOSCode=f.strPOSCode  "
						+ " and a.strClientCode=b.strClientCode "
						+ "and b.strItemCode=e.strItemCode "
						+ "and c.strGroupCode=d.strGroupCode and d.strSubGroupCode=e.strSubGroupCode ");

				sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
						+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
						+ ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
						+ " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
						+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
						+ ",tblitemmaster e,tblposmaster f "
						+ "where a.strBillNo=b.strBillNo "
						+ " and date(a.dteBillDate)=date(b.dteBillDate) "
						+ " and a.strPOSCode=f.strPOSCode  "
						+ " and a.strClientCode=b.strClientCode   "
						+ "and b.strItemCode=e.strItemCode "
						+ "and c.strGroupCode=d.strGroupCode "
						+ " and d.strSubGroupCode=e.strSubGroupCode ");

				String sqlModLive = "select c.strGroupCode,c.strGroupName"
						+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
						+ ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
						+ " sum(b.dblAmount)-sum(b.dblDiscAmt)  "
						+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
						+ ",tblsubgrouphd e,tblgrouphd c "
						+ " where a.strBillNo=b.strBillNo "
						+ " and date(a.dteBillDate)=date(b.dteBillDate) "
						+ " and a.strPOSCode=f.strPosCode  "
						+ " and a.strClientCode=b.strClientCode  "
						+ " and LEFT(b.strItemCode,7)=d.strItemCode "
						+ " and d.strSubGroupCode=e.strSubGroupCode "
						+ " and e.strGroupCode=c.strGroupCode "
						+ " and b.dblamount>0 ";

				String sqlModQFile = "select c.strGroupCode,c.strGroupName"
						+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
						+ ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
						+ " sum(b.dblAmount)-sum(b.dblDiscAmt) "
						+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
						+ ",tblsubgrouphd e,tblgrouphd c "
						+ " where a.strBillNo=b.strBillNo "
						+ " and date(a.dteBillDate)=date(b.dteBillDate) "
						+ " and a.strPOSCode=f.strPosCode   "
						+ " and a.strClientCode=b.strClientCode   "
						+ " and LEFT(b.strItemCode,7)=d.strItemCode "
						+ " and d.strSubGroupCode=e.strSubGroupCode "
						+ " and e.strGroupCode=c.strGroupCode "
						+ " and b.dblamount>0 ";

				sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
				if (!posCode.equals("All"))
				{
					sbSqlFilters.append(" AND a.strPOSCode = '" + posCode + "' ");
				}

				if (!shiftNo.equalsIgnoreCase("All"))
				{
					sbSqlFilters.append(" and a.intShiftCode = '" + shiftNo + "' ");
				}
				sbSqlFilters.append(" Group BY c.strGroupCode ");

				sbSqlLive.append(sbSqlFilters);
				sbSqlQFile.append(sbSqlFilters);
				sqlModLive += " " + sbSqlFilters;
				sqlModQFile += " " + sbSqlFilters;

				TreeMap<String, clsGroupSubGroupWiseSales> mapGroupWiseSales = new TreeMap<String, clsGroupSubGroupWiseSales>();
				double totalDiscount = 0.00, totalNetRevenue = 0.00, totalSubTotal = 0.00;

				list = objGlobalFunctions.funGetList(sbSqlLive.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						String groupCode = objArr[0].toString();
						double netTotal = Double.parseDouble(objArr[3].toString());
						double subTotal = Double.parseDouble(objArr[7].toString());
						double discAmt = Double.parseDouble(objArr[8].toString());

						totalNetRevenue += netTotal;
						totalSubTotal += subTotal;
						totalDiscount += discAmt;

						if (mapGroupWiseSales.containsKey(groupCode))
						{
							clsGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));

							objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
							objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
							objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

						}
						else
						{
							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));
						}

					}
				}

				list = objGlobalFunctions.funGetList(sqlModLive.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						String groupCode = objArr[0].toString();
						double netTotal = Double.parseDouble(objArr[3].toString());
						double subTotal = Double.parseDouble(objArr[7].toString());
						double discAmt = Double.parseDouble(objArr[8].toString());

						totalNetRevenue += netTotal;
						totalSubTotal += subTotal;
						totalDiscount += discAmt;

						if (mapGroupWiseSales.containsKey(groupCode))
						{
							clsGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));

							objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
							objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
							objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

						}
						else
						{
							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));
						}

					}
				}

				list = objGlobalFunctions.funGetList(sbSqlQFile.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						String groupCode = objArr[0].toString();
						double netTotal = Double.parseDouble(objArr[3].toString());
						double subTotal = Double.parseDouble(objArr[7].toString());
						double discAmt = Double.parseDouble(objArr[8].toString());

						totalNetRevenue += netTotal;
						totalSubTotal += subTotal;
						totalDiscount += discAmt;

						if (mapGroupWiseSales.containsKey(groupCode))
						{
							clsGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));

							objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
							objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
							objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

						}
						else
						{
							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));
						}

					}
				}

				list = objGlobalFunctions.funGetList(sqlModQFile.toString());
				if (list.size() > 0)
				{
					for (int i = 0; i < list.size(); i++)
					{
						Object[] objArr = (Object[]) list.get(i);

						String groupCode = objArr[0].toString();
						double netTotal = Double.parseDouble(objArr[3].toString());
						double subTotal = Double.parseDouble(objArr[7].toString());
						double discAmt = Double.parseDouble(objArr[8].toString());

						totalNetRevenue += netTotal;
						totalSubTotal += subTotal;
						totalDiscount += discAmt;

						if (mapGroupWiseSales.containsKey(groupCode))
						{
							clsGroupSubGroupWiseSales objOldGroupWiseSales = mapGroupWiseSales.get(groupCode);

							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));

							objOldGroupWiseSales.setDblNetTotal(objOldGroupWiseSales.getDblNetTotal() + objNewGroupWiseSales.getDblNetTotal());
							objOldGroupWiseSales.setDiscAmt(objOldGroupWiseSales.getDiscAmt() + objNewGroupWiseSales.getDiscAmt());
							objOldGroupWiseSales.setSubTotal(objOldGroupWiseSales.getSubTotal() + objNewGroupWiseSales.getSubTotal());

						}
						else
						{
							clsGroupSubGroupWiseSales objNewGroupWiseSales = new clsGroupSubGroupWiseSales(groupCode, objArr[1].toString(), objArr[4].toString(), Double.parseDouble(objArr[2].toString()), Double.parseDouble(objArr[3].toString()), Double.parseDouble(objArr[7].toString()), Double.parseDouble(objArr[8].toString()), Double.parseDouble(objArr[10].toString()));
						}

					}
				}

				hm.put("totalGrossRevenue", totalGrossSales);
				hm.put("subTotal", totalSubTotal);
				hm.put("totalNetRevenue", totalNetRevenue);
				hm.put("totalDiscount", totalDiscount);
			}

			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(listOfBillItemDtl);
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
					resp.setHeader("Content-Disposition", "inline;filename=GroupWiseSalesReport_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".pdf");
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
					resp.setHeader("Content-Disposition", "inline;filename=ShopOrderListTableWise_" + fromDate + "_To_" + toDate + "_" + strUserCode + ".xls");
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
