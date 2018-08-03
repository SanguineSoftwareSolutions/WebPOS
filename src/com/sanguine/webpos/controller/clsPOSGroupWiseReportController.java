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

import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sanguine.controller.clsGlobalFunctions;
import com.sanguine.webpos.bean.clsGroupWaiseSalesBean;
import com.sanguine.webpos.bean.clsWebPOSReportBean;
import com.sanguine.webpos.util.clsGroupWiseComparator;

@Controller
public class clsPOSGroupWiseReportController
{

	@Autowired
	private clsGlobalFunctions objGlobalFunctions;
	@Autowired
	private clsPOSGlobalFunctionsController objPOSGlobalFunctionsController;

	@Autowired
	private ServletContext servletContext;
	Map<String, String> hmSubGroupName = null;
	Map<String, String> hmPOSData = null;
	Map<String, String> hmSubGroupCode = null;

	@RequestMapping(value = "/frmPOSGroupWiseReport", method = RequestMethod.GET)
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

		List listSubGroup = funGetSubGroupGDetail(strClientCode);

		model.put("listSubGroup", listSubGroup);
		// return new ModelAndView("frmPOSGroupMaster");

		if ("2".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSGroupWiseReport_1", "command", new clsWebPOSReportBean());
		}
		else if ("1".equalsIgnoreCase(urlHits))
		{
			return new ModelAndView("frmPOSGroupWiseReport", "command", new clsWebPOSReportBean());
		}
		else
		{
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/rptPOSGroupWiseSales", method = RequestMethod.POST)
	private void funReport(@ModelAttribute("command") clsWebPOSReportBean objBean, HttpServletResponse resp, HttpServletRequest req)
	{
		try
		{
			List listLive = null;
			List listQFile = null;
			List listModLive = null;
			List listModQFile = null;

			String reportName = servletContext.getRealPath("/WEB-INF/reports/webpos/rptGroupWiseSalesReport.jrxml");

			Map hm = objGlobalFunctions.funGetCommonHashMapForJasperReport(objBean, req, resp);
			String strPOSName = objBean.getStrPOSName();
			String posCode = "ALL";
			if (!strPOSName.equalsIgnoreCase("ALL"))
			{
				posCode = hmPOSData.get(strPOSName);
			}
			hm.put("posCode", posCode);
			String fromDate = hm.get("fromDate").toString();
			String toDate = hm.get("toDate").toString();
			String strUserCode = hm.get("userName").toString();
			String strPOSCode = posCode;
			String strShiftNo = "1";

			String strSGName = objBean.getStrSGName();
			String sgCode = "ALL";
			if (!strSGName.equalsIgnoreCase("ALL"))
			{
				sgCode = hmSubGroupName.get("strSGName");// funGetSGCode(strSGName);
			}

			String strSGCode = sgCode;

			List listRet = new ArrayList();
			StringBuilder sbSqlLive = new StringBuilder();
			StringBuilder sbSqlQFile = new StringBuilder();
			StringBuilder sbSqlFilters = new StringBuilder();

			sbSqlLive.setLength(0);
			sbSqlQFile.setLength(0);
			sbSqlFilters.setLength(0);

			sbSqlQFile.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
					+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
					+ ",f.strPosName, '" + strUserCode + "'"
					+ ",b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
					+ "sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
					+ "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d"
					+ ",tblitemmaster e,tblposmaster f "
					+ "where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPOSCode  "
					+ "and a.strClientCode=b.strClientCode "
					+ "and b.strItemCode=e.strItemCode "
					+ "and c.strGroupCode=d.strGroupCode "
					+ "and d.strSubGroupCode=e.strSubGroupCode ");

			sbSqlLive.append("SELECT c.strGroupCode,c.strGroupName,sum( b.dblQuantity)"
					+ ",sum( b.dblAmount)-sum(b.dblDiscountAmt) "
					+ ",f.strPosName, '" + strUserCode + "',b.dblRate ,sum(b.dblAmount),sum(b.dblDiscountAmt),a.strPOSCode,"
					+ " sum( b.dblAmount)-sum(b.dblDiscountAmt)+sum(b.dblTaxAmount)  "
					+ "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d"
					+ ",tblitemmaster e,tblposmaster f "
					+ "where a.strBillNo=b.strBillNo "
					+ "and a.strPOSCode=f.strPOSCode  "
					+ "and a.strClientCode=b.strClientCode   "
					+ "and b.strItemCode=e.strItemCode "
					+ "and c.strGroupCode=d.strGroupCode "
					+ "and d.strSubGroupCode=e.strSubGroupCode ");

			String sqlModLive = "select c.strGroupCode,c.strGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName"
					+ ",'" + strUserCode + "','0' ,sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
					+ " sum(b.dblAmount)-sum(b.dblDiscAmt)  "
					+ " from tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d"
					+ ",tblsubgrouphd e,tblgrouphd c "
					+ " where a.strBillNo=b.strBillNo and a.strPOSCode=f.strPosCode  "
					+ "and a.strClientCode=b.strClientCode  "
					+ " and LEFT(b.strItemCode,7)=d.strItemCode "
					+ " and d.strSubGroupCode=e.strSubGroupCode "
					+ "and e.strGroupCode=c.strGroupCode "
					+ " and b.dblamount>0 ";

			String sqlModQFile = "select c.strGroupCode,c.strGroupName"
					+ ",sum(b.dblQuantity),sum(b.dblAmount)-sum(b.dblDiscAmt),f.strPOSName" + ",'" + strUserCode + "','0' "
					+ ",sum(b.dblAmount),sum(b.dblDiscAmt),a.strPOSCode,"
					+ " sum(b.dblAmount)-sum(b.dblDiscAmt) "
					+ " from tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d"
					+ ",tblsubgrouphd e,tblgrouphd c "
					+ " where a.strBillNo=b.strBillNo "
					+ "and a.strPOSCode=f.strPosCode   "
					+ "and a.strClientCode=b.strClientCode   "
					+ " and LEFT(b.strItemCode,7)=d.strItemCode "
					+ " and d.strSubGroupCode=e.strSubGroupCode "
					+ "and e.strGroupCode=c.strGroupCode "
					+ " and b.dblamount>0 ";

			sbSqlFilters.append(" and date( a.dteBillDate ) BETWEEN '" + fromDate + "' AND '" + toDate + "' ");
			if (!strPOSCode.equalsIgnoreCase("All"))
			{
				sbSqlFilters.append(" AND a.strPOSCode = '" + strPOSCode + "' ");
			}

			sbSqlFilters.append(" and a.intShiftCode = '" + strShiftNo + "' ");

			if (!strSGCode.equalsIgnoreCase("All"))
			{
				sbSqlFilters.append("AND d.strSubGroupCode='" + strSGCode + "' ");
			}
			sbSqlFilters.append(" Group BY c.strGroupCode, c.strGroupName, a.strPoscode ");

			sbSqlLive.append(sbSqlFilters);
			sbSqlQFile.append(sbSqlFilters);
			sqlModLive += " " + sbSqlFilters;
			sqlModQFile += " " + sbSqlFilters;

			Map<String, clsGroupWaiseSalesBean> mapGroup = new HashMap<>();

			List listSqlLive = objGlobalFunctions.funGetList(sbSqlLive.toString());
			if (listSqlLive.size() > 0)
			{
				for (int i = 0; i < listSqlLive.size(); i++)
				{
					Object[] obj = (Object[]) listSqlLive.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
					double salesAmt = Double.parseDouble(obj[10].toString());

					if (mapGroup.containsKey(groupCode))
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
					else
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}

				}
			}

			List listSqlQFile = objGlobalFunctions.funGetList(sbSqlQFile.toString());
			if (listSqlQFile.size() > 0)
			{

				for (int i = 0; i < listSqlQFile.size(); i++)
				{
					Object[] obj = (Object[]) listSqlQFile.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
					double salesAmt = Double.parseDouble(obj[10].toString());

					if (mapGroup.containsKey(groupCode))
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
					else
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
				}
			}

			List listSqlModLive = objGlobalFunctions.funGetList(sqlModLive.toString());
			if (listSqlModLive.size() > 0)
			{

				for (int i = 0; i < listSqlModLive.size(); i++)
				{
					Object[] obj = (Object[]) listSqlModLive.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
					double salesAmt = Double.parseDouble(obj[10].toString());

					if (mapGroup.containsKey(groupCode))
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
					else
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
				}
			}

			List listSqlModQFile = objGlobalFunctions.funGetList(sqlModQFile.toString());
			if (listSqlModQFile.size() > 0)
			{
				for (int i = 0; i < listSqlModQFile.size(); i++)
				{
					Object[] obj = (Object[]) listSqlModQFile.get(i);

					String groupCode = obj[0].toString();
					String groupName = obj[1].toString();
					double qty = Double.parseDouble(obj[2].toString());
					double netTotal = Double.parseDouble(obj[3].toString());
					String posName = obj[4].toString();
					double subTotal = Double.parseDouble(obj[7].toString());
					double discAmt = Double.parseDouble(obj[8].toString());
					double salesAmt = Double.parseDouble(obj[10].toString());

					if (mapGroup.containsKey(groupCode))
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = mapGroup.get(groupCode);
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(objClsGroupWaiseSalesBean.getQty() + qty);
						objClsGroupWaiseSalesBean.setSubTotal(objClsGroupWaiseSalesBean.getSubTotal() + subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(objClsGroupWaiseSalesBean.getDiscAmt() + discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(objClsGroupWaiseSalesBean.getNetTotal() + netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(objClsGroupWaiseSalesBean.getSalesAmt() + salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
					else
					{
						clsGroupWaiseSalesBean objClsGroupWaiseSalesBean = new clsGroupWaiseSalesBean();
						objClsGroupWaiseSalesBean.setGroupName(groupName);
						objClsGroupWaiseSalesBean.setPosName(posName);
						objClsGroupWaiseSalesBean.setQty(qty);
						objClsGroupWaiseSalesBean.setSubTotal(subTotal);
						objClsGroupWaiseSalesBean.setDiscAmt(discAmt);
						objClsGroupWaiseSalesBean.setNetTotal(netTotal);
						objClsGroupWaiseSalesBean.setSalesAmt(salesAmt);

						mapGroup.put(groupCode, objClsGroupWaiseSalesBean);
					}
				}
			}

			List<clsGroupWaiseSalesBean> list = new ArrayList<>();
			for (clsGroupWaiseSalesBean objGroupWaiseSalesBean : mapGroup.values())
			{
				list.add(objGroupWaiseSalesBean);
			}

			Comparator<clsGroupWaiseSalesBean> groupComparator = new Comparator<clsGroupWaiseSalesBean>()
			{

				@Override
				public int compare(clsGroupWaiseSalesBean o1, clsGroupWaiseSalesBean o2)
				{
					return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
				}
			};

			Collections.sort(list, new clsGroupWiseComparator(groupComparator));

			JasperDesign jd = JRXmlLoader.load(reportName);
			JasperReport jr = JasperCompileManager.compileReport(jd);

			// jp = JasperFillManager.fillReport(jr, hm, new
			// JREmptyDataSource());

			List<JasperPrint> jprintlist = new ArrayList<JasperPrint>();
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
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

	/*
	 * public String funGetPOSCode(String strPOSName) { String posCode="";
	 * String posUrl =
	 * "http://localhost:8080/prjSanguineWebService/WebPOSPOSMaster/funGetPOSNameData"
	 * ;
	 * 
	 * System.out.println(posUrl);
	 * 
	 * try { JSONObject objRows = new JSONObject(); objRows.put("strPOSName",
	 * strPOSName);
	 * 
	 * JSONObject jObj =
	 * objGlobalFunctions.funPOSTMethodUrlJosnObjectData(posUrl,objRows);
	 * posCode= jObj.get("strPosCode").toString();
	 * 
	 * // URL url = new URL(posUrl);
	 * 
	 * // HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //
	 * conn.setRequestMethod("POST"); // conn.setRequestProperty("Content-Type",
	 * "application/json"); // OutputStream os = conn.getOutputStream(); //
	 * os.write(objRows.toString().getBytes()); // os.flush(); // if
	 * (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) // { // throw
	 * new RuntimeException("Failed : HTTP error code : " +
	 * conn.getResponseCode()); // } // BufferedReader br = new
	 * BufferedReader(new InputStreamReader((conn.getInputStream()))); // String
	 * output = "", op = ""; // // while ((output = br.readLine()) != null) // {
	 * // op += output; // } // System.out.println("Result= " + op); //
	 * conn.disconnect(); // // JSONParser parser = new JSONParser(); // Object
	 * obj = parser.parse(op); // josnObjRet = (JSONObject) obj;
	 * 
	 * 
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return posCode;
	 * 
	 * }
	 */

	public ArrayList<String> funGetSubGroupGDetail(String clientCode)
	{

		JSONObject jObj = objGlobalFunctions.funGETMethodUrlJosnObjectData("http://localhost:8080/prjSanguineWebService/APOSMastersIntegration/funGetAllSubGroup?clientCode=" + clientCode);
		JSONArray jArr = (JSONArray) jObj.get("allSGData");
		JSONObject subJsonObject = new JSONObject();
		hmSubGroupName = new HashedMap();
		hmSubGroupCode = new HashedMap();
		ArrayList<String> lstSGData = new ArrayList<String>();
		lstSGData.add("ALL");
		if (null != jArr)
		{
			for (int i = 0; i < jArr.size(); i++)
			{
				subJsonObject = (JSONObject) jArr.get(i);

				hmSubGroupName.put(subJsonObject.get("strSubGroupName").toString(), subJsonObject.get("strSubGroupCode").toString());
				hmSubGroupCode.put(subJsonObject.get("strSubGroupCode").toString(), subJsonObject.get("strSubGroupName").toString());
				lstSGData.add(subJsonObject.get("strSubGroupName").toString());
			}
		}
		return lstSGData;
	}

	/*
	 * public String funGetSGCode(String strSGName) { String sgCode=""; String
	 * posUrl =
	 * "http://localhost:8080/prjSanguineWebService/WebPOSSGMaster/funGetSGNameData"
	 * ; try { JSONObject objRows = new JSONObject(); objRows.put("strSGName",
	 * strSGName);
	 * 
	 * JSONObject jObj =
	 * objGlobalFunctions.funPOSTMethodUrlJosnObjectData(posUrl,objRows);
	 * sgCode= jObj.get("strSubGroupCode").toString(); // URL url = new
	 * URL(posUrl); // // HttpURLConnection conn = (HttpURLConnection)
	 * url.openConnection(); // conn.setRequestMethod("POST"); //
	 * conn.setRequestProperty("Content-Type", "application/json"); //
	 * OutputStream os = conn.getOutputStream(); //
	 * os.write(objRows.toString().getBytes()); // os.flush(); // if
	 * (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) // { // throw
	 * new RuntimeException("Failed : HTTP error code : " +
	 * conn.getResponseCode()); // } // BufferedReader br = new
	 * BufferedReader(new InputStreamReader((conn.getInputStream()))); // String
	 * output = "", op = ""; // // while ((output = br.readLine()) != null) // {
	 * // op += output; // } // System.out.println("Result= " + op); //
	 * conn.disconnect(); // // JSONParser parser = new JSONParser(); // Object
	 * obj = parser.parse(op); // josnObjRet = (JSONObject) obj; //
	 * 
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return sgCode;
	 * 
	 * }
	 */

	// public List funGetPOSList()
	// {
	// List listPos= new ArrayList();
	// listPos.add("ALL");
	// String posUrl =
	// "http://localhost:8080/prjSanguineWebService/APOSIntegration/funGetPOS";
	//
	// System.out.println(posUrl);
	//
	// try {
	//
	// URL url = new URL(posUrl);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// conn.setRequestMethod("GET");
	// conn.setRequestProperty("Accept", "application/json");
	// BufferedReader br = new BufferedReader(new
	// InputStreamReader((conn.getInputStream())));
	// String output = "", op = "";
	// while ((output = br.readLine()) != null)
	// {
	// op += output;
	// }
	// System.out.println("Obj="+op);
	// conn.disconnect();
	//
	// JSONParser parser = new JSONParser();
	// Object obj = parser.parse(op);
	// JSONObject jObj = (JSONObject) obj;
	// JSONArray jArryPosList =(JSONArray) jObj.get("posList");
	// for(int i =0 ;i<jArryPosList.size();i++)
	// {
	// JSONObject josnObjRet = (JSONObject) jArryPosList.get(i);
	// listPos.add(josnObjRet.get("strPosName"));
	// }
	// // System.out.println("hhh");
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return listPos;
	//
	// }

}
